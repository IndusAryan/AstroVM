package com.aryan.astro.repository

import android.util.Log
import com.aryan.astro.helpers.JsonHelper
import com.aryan.astro.repositorydata.TimezoneResponse
import okhttp3.OkHttpClient
import okhttp3.Request

class TimezoneAPI {

    private val apiUrl = "https://api.api-ninjas.com/v1/timezone"
    private var client = OkHttpClient()
    private val TAG = "TZApi"

    fun getTimezone(city: Any?): String {
        val requestUrl = "$apiUrl?city=$city"

        return try {
                val request = Request.Builder()
                    .url(requestUrl)
                    .header("X-Api-Key", "+nmEz/w5auFTjEYKv5JgNQ==dSS4VRLCtcor7VnJ")
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val timezoneResponse =
                        JsonHelper.json.decodeFromString<TimezoneResponse>(responseBody.toString())
                    val timezone = timezoneResponse.timezone?.replace("/", ":").toString()

                    Log.d(TAG, "$responseBody")
                    timezone
                } else {
                    throw Exception("API request failed with code: ${response.code}")
                }
        } catch (t: Throwable) {
            throw Exception("Network error occurred: ${t.message}")
        }
    }
}
