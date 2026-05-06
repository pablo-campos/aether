package com.oeuvre.aether.platform

import platform.Foundation.NSBundle

actual fun placesApiKey(): String =
    NSBundle.mainBundle.objectForInfoDictionaryKey("PLACES_API_KEY") as? String ?: ""
