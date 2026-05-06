package com.oeuvre.aether.util

fun String.formatCategory(): String {
    return this.split('_')
        .joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}
