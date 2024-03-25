package com.aryan.astro.api

import android.util.Log
import com.aryan.astro.obj.AstroApiResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class VedAstroAPI {

    private val client = OkHttpClient()
    private val TAG = "AstroAPI" +
            ""
    fun fetchMoonSign(
        bornCity: String,
        birthTime: String,
        day: Any,
        month: Any,
        year: Any,
        timezone: String,
        onMoonSignFetched: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val astroAPI =
            "https://api.vedastro.org/Calculate/MoonSignName/Location/$bornCity/Time/$birthTime/$day/$month/$year/$timezone"
        val gson = Gson()

        try {
            val request = Request.Builder()
                .url(astroAPI)
                .build()

            val response = client.newCall(request).execute()

            // (HTTP code 200)
            if (response.isSuccessful) {
                val astroResponse = response.body?.string()
                if (astroResponse != null) {
                    Log.d(TAG, astroResponse)
                }

                // Check if the JSON response is not null
                if (!astroResponse.isNullOrEmpty()) {
                    val astroAPIResponse = gson.fromJson(astroResponse, AstroApiResponse::class.java)

                    // Check if payload is not null
                    val moonSign = astroAPIResponse.Payload.MoonSignName
                    if (moonSign.isNotEmpty()) {
                        onMoonSignFetched(moonSign)
                    } else {
                        Log.d(TAG, astroAPI)
                        Log.e(TAG, "$astroAPIResponse")
                        onError("Moon sign not found.")
                    }
                } else {
                    Log.d(TAG, astroAPI)
                    Log.e(TAG, "$response")
                    onError("Empty response.")
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            onError("Error fetching moon sign.")
        }
    }
}
