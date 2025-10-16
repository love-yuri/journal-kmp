package com.yuri.love.database

import com.yuri.love.Database
import com.yuri.love.SystemConfig
import com.yuri.love.SystemConfigQueries
import com.yuri.love.share.SystemConfigDatabaseName

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