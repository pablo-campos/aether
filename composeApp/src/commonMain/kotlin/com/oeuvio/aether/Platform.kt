package com.oeuvio.aether

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform