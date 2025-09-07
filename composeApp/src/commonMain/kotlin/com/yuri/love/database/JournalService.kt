package com.yuri.love.database

import com.yuri.love.Database
import com.yuri.love.Journal
import com.yuri.love.JournalQueries
import com.yuri.love.share.JournalDatabaseName
import com.yuri.love.utils.TimeUtils
import kotlin.random.Random
import kotlin.time.ExperimentalTime

/**
 * sql服务
 */
object JournalService {
    val query: JournalQueries by lazy {
        val driver = DriverFactory.create().createDriver(JournalDatabaseName)
        Database.Companion(driver).journalQueries
    }

    fun test() {
        query.insert(Journal(
            id = Random.nextLong(),
            title = "",
            content = "",
            createdAt = TimeUtils.now,
            updatedAt = TimeUtils.now,
            mood = "",
            weather = ""
        ))
    }

    fun add(
        title: String,
        content: String,
        mood: String? = null,
        weather: String? = null,
    ) {
        query.insert(Journal(
            id = Random.nextLong(),
            title = title,
            content = content,
            createdAt = TimeUtils.now,
            updatedAt = TimeUtils.now,
            mood = mood,
            weather = weather
        ))
    }
}