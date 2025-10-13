package com.yuri.love.database

import android.annotation.SuppressLint
import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.yuri.love.Database
import com.yuri.love.share.DatabaseSuffix

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(name: String): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, context, "$name.$DatabaseSuffix")
    }

    actual companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

        actual fun create(): DriverFactory = DriverFactory(context!!)
    }

    actual fun path(name: String): String {
        return context.getDatabasePath("$name.$DatabaseSuffix")?.path ?: ""
    }
}