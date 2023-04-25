package com.example.solubox.Utils


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.solubox.entities.AccessToken
import com.example.solubox.network.ApiService
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FacebookManager(private val apiService: ApiService, private val tokenManager: TokenManager) {
    interface FacebookLoginListener {
        fun onSuccess()
        fun onError(message: String?)
    }

    private val callbackManager: CallbackManager?
    private var listener: FacebookLoginListener? = null
    private var call: Call<AccessToken?>? = null
    private val facebookCallback: FacebookCallback<LoginResult> = object : FacebookCallback<LoginResult> {
        override fun onSuccess(loginResult: LoginResult) {
            fetchUser(loginResult.accessToken)
        }

        override fun onCancel() {}
        override fun onError(error: FacebookException) {
            listener!!.onError(error.message)
        }
    }

    fun login(activity: Activity?, listener: FacebookLoginListener?) {
        this.listener = listener
        if (com.facebook.AccessToken.getCurrentAccessToken() != null) {
            //Get the user
            fetchUser(com.facebook.AccessToken.getCurrentAccessToken()!!)
        } else {
            LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"))
        }
    }

    private fun fetchUser(accessToken: com.facebook.AccessToken) {
        val request = GraphRequest.newMeRequest(accessToken) { `object`, _ ->
            try {
                val id = `object`?.getString("id")
                val name = `object`?.getString("first_name")
                val email = `object`?.getString("email")
                getTokenFromBackend(name!!, email!!, PROVIDER, id!!)
            } catch (e: JSONException) {
                e.printStackTrace()
                listener!!.onError(e.message)
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun getTokenFromBackend(name: String, email: String, provider: String, providerUserId: String) {
        call = apiService.socialAuth(name, email, provider, providerUserId)
        call!!.enqueue(object : Callback<AccessToken?> {
            override fun onResponse(call: Call<AccessToken?>?, response: Response<AccessToken?>) {
                if (response.isSuccessful) {
                    tokenManager.saveToken(response.body()!!)
                    listener!!.onSuccess()
                } else {
                    listener!!.onError("An error occured")
                }
            }

            override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                listener!!.onError(t.message)
            }
        })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    fun onDestroy() {
        if (call != null) {
            call!!.cancel()
        }
        call = null
        if (callbackManager != null) {
            LoginManager.getInstance().unregisterCallback(callbackManager)
        }
    }

    fun clearSession() {
        LoginManager.getInstance().logOut()
    }

    companion object {
        private const val TAG = "FacebookManager"
        private const val PROVIDER = "facebook"
    }

    init {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback)
    }
}