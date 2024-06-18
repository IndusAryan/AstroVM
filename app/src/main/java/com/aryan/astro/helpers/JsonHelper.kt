package com.aryan.astro.helpers

import kotlinx.serialization.json.Json

object JsonHelper {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
    }
}