package com.yuri.love.database

import app.cash.sqldelight.db.SqlDriver
import com.yuri.love.Database

expect class DriverFactory {
    fun createDriver(name: String): SqlDriver

    companion object {
        fun create(): DriverFactory
    }
}

