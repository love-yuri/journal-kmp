package com.yuri.love.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.yuri.love.Journal
import com.yuri.love.database.JournalService
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

class JournalFlow(): ViewModel() {
    private val _journals = MutableStateFlow(emptyList<Journal>())
    val journals: StateFlow<List<Journal>> = _journals.asStateFlow()
    private val log = logger {}

    // 在init或构造函数中开始收集数据
    init {
        loadDiaries()
    }

    private fun loadDiaries() {
        viewModelScope.launch {
            val journals = JournalService.query.all().asFlow().mapToList(Dispatchers.IO)
            journals.collect {
                log.info { "load data ${it.size}" }

                _journals.value = it
            }
        }
    }
}
