package com.example.solubox.network

import com.example.solubox.Utils.TokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

class CustomAuthenticator private constructor(private val tokenManager: TokenManager) : Authenticator {
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {

        val token = tokenManager.token
        val service = RetrofitBuilder.createService(ApiService::class.java)
        val call = service.refresh(token.refreshToken)
        val res = call?.execute()
        return if (res?.isSuccessful == true) {
            val newToken = res.body()
            if (newToken != null) {
                tokenManager.saveToken(newToken)
            }
            response.request.newBuilder().header("Authorization", "Bearer " + res.body()!!.accessToken).build()
        } else {
            null
        }
    }


    companion object {
        private var INSTANCE: CustomAuthenticator? = null
        @JvmStatic
        @Synchronized
        fun getInstance(tokenManager: TokenManager): CustomAuthenticator? {
            if (INSTANCE == null) {
                INSTANCE = CustomAuthenticator(tokenManager)
            }
            return INSTANCE
        }
    }
}