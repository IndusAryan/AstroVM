package com.aryan.astro.repository

import android.annotation.SuppressLint
import android.util.Log
import com.aryan.astro.helpers.JsonHelper
import com.aryan.astro.repositorydata.AstroApiResponse
import com.aryan.astro.repositorydata.MoonSignData
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
    private var moonSign : String? = null
    private val vedAstroEndpoint = "https://vedastroapi.azurewebsites.net/api/Calculate/MoonSignName/Location"

    fun fetchMoonSign(
        bornCity: String?,
        birthTime: String?,
        day: Any?,
        month: Any?,
        year: Any?,
        timezone: String?,
    ): MoonSignData? {
        val astroAPI =
            "$vedAstroEndpoint/$bornCity/Time/$birthTime/$day/$month/$year/$timezone"

        Log.i(TAG, astroAPI)
        try {
            val request = Request.Builder()
                .url(astroAPI)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) { // (HTTP code 200)
                val astroResponse = response.body?.string()
                // Check if the JSON response is not null
                if (!astroResponse.isNullOrEmpty()) {
                    Log.d(TAG, astroResponse)

                    val astroAPIResponse = JsonHelper.json.decodeFromString<AstroApiResponse>(astroResponse)

                    // Check if payload is not null
                    moonSign = astroAPIResponse.Payload.MoonSignName
                    Log.d(TAG, astroAPI)
                    Log.i(TAG, "Moon Sign: $moonSign")

                } else {
                    Log.e(TAG, "$astroAPI $response")
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return moonSign?.let { MoonSignData(it) }
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
