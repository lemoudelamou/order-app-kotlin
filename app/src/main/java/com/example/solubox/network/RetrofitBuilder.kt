@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")


package com.example.solubox.network


import com.example.solubox.Utils.TokenManager
import com.example.solubox.network.CustomAuthenticator.Companion.getInstance
import com.facebook.stetho.okhttp3.BuildConfig
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Der Retrofit-Client wird mit einer Server-URL (BASE_URL) und einem Anfragekonverter konfiguriert.
 *
 * Dieser Konverter wird die JSON-Antwort automatisch in Objekte umwandeln.
 *
 * Hier verwenden wir den Moshi-Konverter
 */


object RetrofitBuilder {
    private const val BASE_URL = "http://192.168.178.38:8000/api/"
    private val client = buildClient()

    @JvmStatic
    val retrofit = buildRetrofit(client)
    private fun buildClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()
                val builder = request.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Connection", "close")
                request = builder.build()
                chain.proceed(request)
            }

        if(BuildConfig.DEBUG){
            builder.addNetworkInterceptor( StethoInterceptor())
        }

        return builder.build()
    }

    private fun buildRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @JvmStatic
    fun <T> createService(service: Class<T>?): T {
        return retrofit.create(service)
    }

    @JvmStatic
    fun <T> createServiceWithAuth(service: Class<T>?, tokenManager: TokenManager): T {
        val newClient = getInstance(tokenManager)?.let {
            client.newBuilder().addInterceptor { chain ->
                var request = chain.request()
                val builder = request.newBuilder()
                if (tokenManager.token.accessToken != null) {
                    builder.addHeader("Authorization", "Bearer " + tokenManager.token.accessToken.toString())
                }
                request = builder.build()
                chain.proceed(request)
            }.authenticator(it).build()
        }
        val newRetrofit = retrofit.newBuilder().client(newClient).build()
        return newRetrofit.create(service)
    }


}