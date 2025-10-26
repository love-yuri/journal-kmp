package com.yuri.love.database

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Storage
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.db.SqlDriver
import com.yuri.love.Database
import com.yuri.love.Journal
import com.yuri.love.JournalInfo
import com.yuri.love.JournalQueries
import com.yuri.love.database.JournalService.JournalBackupType.*
import com.yuri.love.retrofit.WebDavService
import com.yuri.love.share.DatabaseSuffix
import com.yuri.love.share.JournalDatabaseName
import com.yuri.love.share.TempRestoreFilePrefix
import com.yuri.love.share.TempRestoreFileSuffix
import com.yuri.love.utils.TimeUtils
import com.yuri.love.utils.notification.Notification
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
import kotlinx.serialization.Serializable
import java.io.File

val log = logger {}

/**
 * sql服务
 */
object JournalService {
    @Serializable
    data class JournalBackupInfo(
        val name: String,
        val date: Long,
        val type: JournalBackupType
    ) {
        /**
         * 图标
         */
        val icon get() = when (type) {
            Local -> Icons.Default.Storage
            Webdav -> Icons.Default.Cloud
        }

        /**
         * 标题
         */
        val title get() = when (type) {
            Local -> "本地备份"
            Webdav -> "Webdav备份"
        }
    }

    enum class JournalBackupType {
        Local,
        Webdav
    }

    const val SIZE = 10L
    private val _currentPage = MutableStateFlow(0L) // 使用 StateFlow 管理页码
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _journals = MutableStateFlow<List<Journal>>(emptyList())
    private val factory by lazy { DriverFactory.create() }
    private val _journalInfo = MutableStateFlow(JournalInfo(0, 0))

    private val driver: SqlDriver by lazy {
        factory.createDriver(JournalDatabaseName)
    }

    /**
     * 查询器
     */
    private val query: JournalQueries by lazy {
        Database(driver).journalQueries
    }

    /**
     * 所有日记
     */
    val journals: StateFlow<List<Journal>> = _journals.asStateFlow()

    /**
     * 日记信息
     */
    val JournalInfo: StateFlow<JournalInfo> = _journalInfo.asStateFlow()

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
     * 创建备份文件
     */
    private fun createBackupFile(type: JournalBackupType): JournalBackupInfo {
        val database = File(factory.path(JournalDatabaseName))
        if (!database.exists()) {
            throw Exception("文件不存在!!")
        }

        val now = TimeUtils.now
        val backupInfo = JournalBackupInfo (
            "${JournalDatabaseName}_backup_${now}",
            now,
            type
        )

        database.copyTo(File(factory.path(backupInfo.name)))
        return backupInfo
    }

    /**
     * 本地备份
     */
    fun localBackup(info: JournalBackupInfo? = null) {
        try {
            val backupInfo: JournalBackupInfo = info?.copy(type = Local) ?: createBackupFile(JournalBackupType.Local)
            SystemConfig.journal_backups += backupInfo
            Notification.notificationState?.success("本地备份成功!!")
        } catch (e: Exception) {
            Notification.notificationState?.error("本地备份失败!! ${e.message}")
        }
    }

    /**
     * webdav备份
     */
    fun webdavBackup(info: JournalBackupInfo? = null) {
        try {
            val backupInfo: JournalBackupInfo = info?.copy(type = Webdav) ?: createBackupFile(Webdav)
            scope.launch {
                val res = WebDavService.upload(File(factory.path(backupInfo.name)))
                if (!res) {
                    WebDavService.mkdir()
                    WebDavService.upload(File(factory.path(backupInfo.name)))
                }
                Notification.notificationState?.success("webdav备份成功!!")
            }
            SystemConfig.journal_backups += backupInfo
        } catch (e: Exception) {
            Notification.notificationState?.error("本地备份失败!! ${e.message}")
        }
    }

    /**
     * 都备份
     */
    fun backupAll() {
        try {
            val info = createBackupFile(JournalBackupType.Local)
            localBackup(info)
            webdavBackup(info)
        } catch (e: Exception) {
            Notification.notificationState?.error("备份失败!! ${e.message}")
        }
    }

    /**
     * 从本地恢复
     * @param info 备份信息
     */
    private fun restoreFromLocal(info: JournalBackupInfo) {
        val file = File(factory.path(info.name))
        if (!file.exists()) {
           throw Exception("本地备份文件不存在!! ${file.path}")
        }

        file.copyTo(File(factory.path(JournalDatabaseName)), overwrite = true)

        scope.launch {
            _journalInfo.value = query.journalInfo().executeAsOne()
            refresh()
        }
    }

    /**
     * 从webdav恢复
     * @param info 备份信息
     */
    private fun restoreFromWebdav(info: JournalBackupInfo) {
        scope.launch {
            try {
                val fileName = "${info.name}.$DatabaseSuffix"
                val file = WebDavService.dir()
                    .firstOrNull { it.fileName == fileName }
                    ?: throw Exception("webdav备份文件不存在!!")

                val tempFile = File.createTempFile(TempRestoreFilePrefix, TempRestoreFileSuffix)
                if (!WebDavService.download(file.fileName, tempFile)) {
                    throw Exception("下载失败!!")
                }
                tempFile.copyTo(File(factory.path(JournalDatabaseName)), overwrite = true)
                _journalInfo.value = query.journalInfo().executeAsOne()
                refresh()
            } catch (e: Exception) {
                log.error { "恢复失败: ${e.message}" }
                Notification.notificationState?.error("恢复失败: ${e.message}")
            }
        }
    }

    /**
     * 恢复
     * @param info 备份信息
     */
    fun restore(info: JournalBackupInfo) {
        try {
            when (info.type) {
                Local -> restoreFromLocal(info)
                Webdav -> restoreFromWebdav(info)
            }
            Notification.notificationState?.success("恢复成功!!")
        } catch (e: Exception) {
            log.error { "恢复失败!! ${e.message}" }
            Notification.notificationState?.error("恢复失败!! ${e.message}")
        }
    }

    /**
     * 删除备份信息
     */
    fun deleteBackup(info: JournalBackupInfo) {
        try {
            if (info.type == Local) {
                val file = File(factory.path(info.name))
                if (!file.exists()) {
                    throw Exception("${info.name} 文件不存在")
                }
                file.delete()
            }
            val list = SystemConfig.journal_backups.toMutableList()
            list.remove(info)
            SystemConfig.journal_backups = list
            Notification.notificationState?.success("${info.name} 删除成功!!")
        } catch (ex: Exception) {
            log.info { "删除备份失败: ${info.name} msg: ${ex.message}" }
            Notification.notificationState?.error("${info.name} 删除失败: ${ex.message}")
        }
    }
}