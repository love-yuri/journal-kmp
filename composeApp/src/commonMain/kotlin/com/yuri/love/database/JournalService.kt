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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.time.ExperimentalTime

/**
 * sql服务
 */
object JournalService {
    const val SIZE = 10L
    var page = 0L
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _journals = MutableStateFlow<MutableList<Journal>>(mutableListOf())
    val journals: StateFlow<MutableList<Journal>> = _journals.asStateFlow()
    private val log = logger {}

    val query: JournalQueries by lazy {
        val driver = DriverFactory.create().createDriver(JournalDatabaseName)
        Database.Companion(driver).journalQueries
    }

    init {
        scope.launch {
            page()
        }
    }

    suspend fun page() {
        ++page
        query.page(SIZE, (page - 1) * SIZE)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .collect {
                _journals.value.addAll(it)
            }
    }
}