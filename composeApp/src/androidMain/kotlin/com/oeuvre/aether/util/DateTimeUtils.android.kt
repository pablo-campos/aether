package com.oeuvre.aether.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual fun nowLocalDateTime() = Instant
    .fromEpochMilliseconds(System.currentTimeMillis())
    .toLocalDateTime(TimeZone.currentSystemDefault())
