package com.aryan.astro.api

import android.util.Log
import com.aryan.astro.obj.TimezoneResponse
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request

class TimezoneAPI {

    private val apiUrl = "https://api.api-ninjas.com/v1/timezone"
    private val gson = Gson()
    private var client = OkHttpClient()
    private val TAG = "TZApi"

    suspend fun getTimezone(city: String): String {
        val requestUrl = "$apiUrl?city=$city"

        return try {
            coroutineScope {
                val request = Request.Builder()
                    .url(requestUrl)
                    .header("X-Api-Key", "+nmEz/w5auFTjEYKv5JgNQ==dSS4VRLCtcor7VnJ")
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val timezoneResponse =
                        gson.fromJson(responseBody, TimezoneResponse::class.java)
                    val timezone = timezoneResponse.timezone.replace("/", ":")

                    Log.d(TAG, "$responseBody")
                    timezone

                } else {
                    throw Exception("API request failed with code: ${response.code}")
                }
            }
        } catch (t: Throwable) {
            throw Exception("Network error occurred: ${t.message}")
        }
    }
}
