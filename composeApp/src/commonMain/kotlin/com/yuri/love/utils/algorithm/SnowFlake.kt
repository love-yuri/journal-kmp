package com.yuri.love.utils.algorithm

import com.yuri.love.utils.TimeUtils
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * @author Yuri
 * @date 2025 9.11
 * 雪花算法 0 | timestamp(41) | datacenter(10) | sequence(12)
 * timestamp: 41位时间戳，精确到毫秒，可以使用65年
 */
class SnowFlake {
    companion object {
        private var lastTimestamp = -1L
        private const val DATACENTER_BITS = 10
        private const val SEQUENCE_BITS = 12
        private const val SEQUENCE_MASK = (1L shl SEQUENCE_BITS) - 1
        private const val EPOCH = 1609459200000L // 2021-01-01 00:00:00
        private var sequence = 0L // 0 - 4095

        /**
         * create next id
         */
        @Synchronized
        fun nextId(): Long {
            val datacenterId = 1L // 0 - 1023
            var timestamp = TimeUtils.now - EPOCH
            if (timestamp < lastTimestamp) {
                throw RuntimeException("Clock moved backwards. Refusing to generate id for ${lastTimestamp - timestamp} milliseconds")
            } else if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) and SEQUENCE_MASK
                if (sequence == 0L) {
                    timestamp = tilNextMillis()
                }
            } else {
                sequence = 0L
            }

            lastTimestamp = timestamp
            val result = (timestamp shl (DATACENTER_BITS + SEQUENCE_BITS)) or (datacenterId shl SEQUENCE_BITS) or sequence
            return result
        }

        private fun tilNextMillis(): Long {
            var timestamp = TimeUtils.now - EPOCH
            while (timestamp <= lastTimestamp) {
                timestamp = TimeUtils.now - EPOCH
            }
            return timestamp
        }
    }
}
