package com.oeuvre.aether.platform

import platform.Foundation.NSBundle

actual fun geminiApiKey(): String =
    NSBundle.mainBundle.objectForInfoDictionaryKey("GEMINI_API_KEY") as? String ?: ""
