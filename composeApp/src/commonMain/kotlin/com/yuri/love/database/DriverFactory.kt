package com.yuri.love.database

import app.cash.sqldelight.db.SqlDriver
import com.yuri.love.Database

expect class DriverFactory {
    fun createDriver(name: String): SqlDriver

    /**
     * 获取数据库文件路径
     */
    fun path(name: String): String

    companion object {
        fun create(): DriverFactory
    }
}

