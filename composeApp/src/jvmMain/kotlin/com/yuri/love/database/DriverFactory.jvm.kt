package com.yuri.love.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.yuri.love.Database
import com.yuri.love.share.DatabaseSuffix
import java.util.Properties

actual class DriverFactory {
    actual fun createDriver(name: String): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$name.$DatabaseSuffix", Properties(), Database.Schema)
        return driver
    }

    actual companion object {
        actual fun create(): DriverFactory = DriverFactory()
    }
}
