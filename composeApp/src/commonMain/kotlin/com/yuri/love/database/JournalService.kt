package com.yuri.love.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.yuri.love.Database
import com.yuri.love.Journal
import com.yuri.love.JournalInfo
import com.yuri.love.JournalQueries
import com.yuri.love.share.JournalDatabaseName
import com.yuri.love.utils.TimeUtils
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * sql服务
 */
object JournalService {
    const val SIZE = 10L
    private val _currentPage = MutableStateFlow(0L) // 使用 StateFlow 管理页码
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _journals = MutableStateFlow<List<Journal>>(emptyList())

    private val factory by lazy { DriverFactory.create() }
    private val _journalInfo = MutableStateFlow(JournalInfo(0, 0))
    val journals: StateFlow<List<Journal>> = _journals.asStateFlow()

    /**
     * 日记信息
     */
    val JournalInfo: StateFlow<JournalInfo> = _journalInfo.asStateFlow()

    val query: JournalQueries by lazy {
        val driver = factory.createDriver(JournalDatabaseName)
        Database(driver).journalQueries
    }

    init {
        // 监听页码变化，自动加载对应页数据
        scope.launch {
            _currentPage.collect { page ->
                loadPage(page)
            }
        }

        scope.launch {
            query.journalInfo()
                .asFlow()
                .mapToOne(Dispatchers.IO)
                .collect {
                    _journalInfo.value = it
                    if (it.total != 0L) {
                        refresh()
                    }
                }
        }
    }

    // 加载指定页数据
    private suspend fun loadPage(page: Long) {
        val offset = page * SIZE
        query.page(SIZE, offset)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .distinctUntilChanged() // 避免重复数据触发更新
            .first() // 只取第一次结果，避免持续监听
            .let { data ->
                if (page == 0L) {
                    _journals.update { data }
                } else {
                    _journals.update { _journals.value + data }
                }
            }
    }

    // 刷新
    suspend fun refresh() {
        if (_currentPage.value != 0L) {
            _currentPage.update { 0 }
        } else {
            loadPage(0L)
        }
    }

    /**
     * update journal
     */
    fun update(journal: Journal): Boolean {
        return query.updateById (
            journal.title,
            journal.content,
            journal.mood,
            journal.weather,
            TimeUtils.now,
            journal.id
        ).value > 0
    }

    fun insert(journal: Journal): Boolean {
        return query.insert(journal).value > 0
    }

    /**
     * load next page
     */
    fun nextPage() {
        if (_journalInfo.value.total > _journals.value.size) {
            _currentPage.update { it + 1 }
        }
    }

    /**
     * 本地备份
     */
    fun localBackup() {

    }

    /**
     * webdav备份
     */
    fun webdavBackup() {
        TODO("Not yet implemented")
    }
}