package com.yuri.love.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.yuri.love.Database
import com.yuri.love.Journal
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
    val journals: StateFlow<List<Journal>> = _journals.asStateFlow()
    private val log = logger {}

    private val query: JournalQueries by lazy {
        val driver = DriverFactory.create().createDriver(JournalDatabaseName)
        Database(driver).journalQueries
    }

    init {
        // 监听页码变化，自动加载对应页数据
        scope.launch {
            _currentPage.collect { page ->
                val size = query.size().executeAsOne()
                if (size > _journals.value.size) {
                    loadPage(page)
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
    fun refresh() {
        _currentPage.update { 0 }
    }

    /**
     * update journal
     */
    fun update(journal: Journal): Boolean {
        val res = query.updateById (
            journal.title,
            journal.content,
            journal.mood,
            journal.weather,
            TimeUtils.now,
            journal.id
        ).value > 0

        if (res) {
            _journals.update { currentList ->
                val index = currentList.indexOfFirst { it.id == journal.id }
                if (index != -1) {
                    currentList.toMutableList().also { it[index] = journal }
                } else {
                    currentList
                }
            }
        }
        return res
    }

    fun insert(journal: Journal): Boolean {
        val res = query.insert(journal).value > 0
        if (res) {
            _journals.update { it + journal }
        }
        return res
    }

    /**
     * load next page
     */
    fun nextPage() {
        _currentPage.update { it + 1 }
    }
}