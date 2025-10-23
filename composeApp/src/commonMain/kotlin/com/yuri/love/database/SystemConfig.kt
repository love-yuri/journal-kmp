package com.yuri.love.database

import com.yuri.love.Database
import com.yuri.love.SystemConfig
import com.yuri.love.SystemConfigQueries
import com.yuri.love.share.SystemConfigDatabaseName
import com.yuri.love.share.json

object SystemConfig {
    private const val WEBDAV_ACCOUNT = "webdav_account"
    private const val WEBDAV_PASSWORD = "webdav_password"

    /**
     * 获取webdav账号
     */
    var webdav_account
        get () = get(WEBDAV_ACCOUNT)
        set (value) = set(WEBDAV_ACCOUNT, value)

    /**
     * 获取webdav密码
     */
    var webdav_password
        get () = get(WEBDAV_PASSWORD)
        set (value) = set(WEBDAV_PASSWORD, value)

    /**
     * 是否登录
     */
    var isLoggedIn: Boolean
        get() = get("isLoggedIn").toBoolean()
        set(value) = set("isLoggedIn", value)

    /**
     * 开始记录日记时间
     * 该值由建表时写入
     */
    val start_time: String get() = get("start_time") ?: "2025-6-10 15:43:21"

    /**
     * 日记备份列表
     */
    var journal_backups: List<JournalService.JournalBackupInfo>
        set(value) = set("journal_backups", json.encodeToString(value))
        get() = get("journal_backups")?.let {
            json.decodeFromString<List<JournalService.JournalBackupInfo>>(it)
        } ?: emptyList()

    private val query: SystemConfigQueries by lazy {
        val driver = DriverFactory.create().createDriver(SystemConfigDatabaseName)
        Database(driver).systemConfigQueries
    }

    /**
     * 设置配置
     */
    fun <T> set(key: String, value: T) {
        query.set(key, value.toString())
    }

    /**
     * 获取配置
     */
    fun get(key: String): String? {
        return query.get(key).executeAsOneOrNull()?.value_
    }
}