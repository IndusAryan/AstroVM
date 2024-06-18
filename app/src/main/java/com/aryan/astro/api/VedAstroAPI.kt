package com.aryan.astro.api

import android.annotation.SuppressLint
import android.util.Log
import com.aryan.astro.obj.AstroApiResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class VedAstroAPI {

    private val client = getUnsafeOkHttpClient()
    private val TAG = "AstroAPI"

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
        Log.i(TAG, astroAPI)
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

    /** VedAPI is foss and safe but should be available if certificate expires, it thrives on donations **/
    @SuppressLint("TrustAllX509TrustManager")
    private fun getUnsafeOkHttpClient(): OkHttpClient {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers() = arrayOf<X509Certificate>() }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }.build()
    }
}
