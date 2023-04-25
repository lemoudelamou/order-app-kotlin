package com.example.solubox.Utils



import android.content.SharedPreferences
import com.example.solubox.entities.AccessToken

class TokenManager private constructor(private val prefs: SharedPreferences) {


    private val editor: SharedPreferences.Editor = prefs.edit()
    fun saveToken(token: AccessToken) {
        editor.putString("ACCESS_TOKEN", token.accessToken).commit()
        editor.putString("REFRESH_TOKEN", token.refreshToken).commit()
        editor.putString("REMEMBER_TOKEN", token.rememberToken).commit()
    }

    fun deleteToken() {
        editor.remove("ACCESS_TOKEN").commit()
        editor.remove("REFRESH_TOKEN").commit()
        editor.remove("REMEMBER_TOKEN").commit()
    }

    val token: AccessToken
        get() {
            val token = AccessToken()
            token.accessToken = prefs.getString("ACCESS_TOKEN", null)
            token.refreshToken = prefs.getString("REFRESH_TOKEN", null)
            token.rememberToken = prefs.getString("REMEMBER_TOKEN", null)
            return token
        }


    companion object {
        private var INSTANCE: TokenManager? = null
        @Synchronized
        fun getInstance(prefs: SharedPreferences): TokenManager? {
            if (INSTANCE == null) {
                INSTANCE = TokenManager(prefs)
            }
            return INSTANCE
        }
    }

}