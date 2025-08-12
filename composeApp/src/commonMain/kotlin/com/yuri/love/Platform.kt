package com.yuri.love

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform