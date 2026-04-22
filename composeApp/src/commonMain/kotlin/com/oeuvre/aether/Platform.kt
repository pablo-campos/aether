package com.oeuvre.aether

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform