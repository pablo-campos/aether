package com.oeuvre.aether.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

actual fun nowLocalDateTime(): LocalDateTime = Instant
    .fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
    .toLocalDateTime(TimeZone.currentSystemDefault())
