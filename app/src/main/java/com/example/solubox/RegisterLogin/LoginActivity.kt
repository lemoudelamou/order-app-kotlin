@file:Suppress("DEPRECATION")


package com.example.solubox.RegisterLogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.example.solubox.MainActivity
import com.example.solubox.R
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.example.solubox.Utils.FacebookManager
import com.example.solubox.Utils.TokenManager
import com.example.solubox.Utils.Utils.converErrors
import com.example.solubox.databinding.ActivityLoginBinding
import com.example.solubox.entities.AccessToken
import com.example.solubox.entities.ApiError
import com.example.solubox.model.UserResponse
import com.example.solubox.network.ApiService
import com.example.solubox.network.NetworkConnectivityChecker
import com.example.solubox.network.RetrofitBuilder.createService
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {


    companion object { private const val TAG = "LoginActivity" }

    var snackbar: Snackbar? = null
    var service: ApiService? = null
    var tokenManager: TokenManager? = null
    var validator: AwesomeValidation? = null
    var call: Call<AccessToken?>? = null
    var call2: Call<UserResponse?>? = null
    var facebookManager: FacebookManager? = null
    private lateinit var binding: ActivityLoginBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        service = createService(ApiService::class.java)
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
        validator = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)
        facebookManager = FacebookManager(service!!, tokenManager!!)

        setupRules()

        binding.btnLogin.setOnClickListener {
            NetworkConnectivityChecker.checkForConnection()
            setInternetConnectivityObserver()
            login()
        }

        binding.pswForgotten.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ChangePasswordActivity::class.java))
            finish()
        }

        if(tokenManager?.token?.getAccessToken() != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val s1 = sh.getString("email", "")
        val s2 = sh.getString("password", "")
        val cb = sh.getBoolean("checkbox", binding.checkBox.isChecked)

        if (cb && binding.tilEmail.editText!!.text.toString().isEmpty() && binding.tilPassword.editText!!.text.toString().isEmpty() ) {
            binding.checkBox.isChecked = cb
            binding.tilEmail.editText!!.text.append(s1)
            binding.tilPassword.editText!!.text.append(s2)
        }
    }

    override fun onStart() {
        super.onStart()
        NetworkConnectivityChecker.checkForConnection()

    }

    /**
     * Erstellen eines Shared-Prefs-Objekts mit dem Dateinamen "MySharedPrefs" im privaten Modus
     * alle vom Benutzer eingegebenen Daten in SharedPreference speichern und danach anwenden
     **/

     override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()

        myEdit.putString("email", binding.tilEmail.editText!!.text.toString())
        myEdit.putString("password", binding.tilPassword.editText!!.text.toString())
        myEdit.putBoolean("checkbox", binding.checkBox.isChecked)
        myEdit.apply()

    }

    /**
     * Überprüfung der Internetkonnektivität mit einem "Observer"
     */
    private fun setInternetConnectivityObserver() { NetworkConnectivityChecker.observe(this, liveDataObserver) }

    private val liveDataObserver: Observer<Boolean?> = Observer { isConnected ->
        if (!isConnected!!) {
            binding.container.let {
                snackbar = Snackbar.make(it, "No Internet Connection", Snackbar.LENGTH_LONG)
                snackbar?.show()
            }
        } else {
            snackbar?.dismiss()
        }
    }

    /**
     * Ausblenden der Bildschirmelemente
     * Anzeige des Ladelogos
     */
    fun showLoading() {
        TransitionManager.beginDelayedTransition(binding.container)
        binding.formContainer.visibility = View.GONE
        binding.loader.visibility = View.VISIBLE
    }

    /**
     * Ausblenden des Ladelogos
     * Anzeigen der Bildschirmelemente
     */
    fun showForm() {
        TransitionManager.beginDelayedTransition(binding.container)
        binding.formContainer.visibility = View.VISIBLE
        binding.loader.visibility = View.GONE
    }

    /**
     * Erstellen eines Aufrufs unter Verwendung des Retrofit-Objekts und Aufrufen der login()-Methode
     * Senden der Anfrage an das Netz unter Verwendung der Call.enqueue-Methode von Retrofit.
     * Darin wird ein Callback übergeben, der eine Antwort in Form eines AccessToken-Objekts liefert.
    Dann werden die beiden Methoden implementiert: onResponse und onFailure.
     * Abhören, wenn die Login-Schaltfläche angeklickt wird,
    prüfen, ob der Benutzer Benutzername und Passwort eingegeben hat
    wenn ja, Login durchführen, sonst Fehlermeldung ausgeben.
     */
    private fun login() {
        val email = binding.tilEmail.editText!!.text.toString()
        val password = binding.tilPassword.editText!!.text.toString()
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        validator!!.clear()
        if (validator!!.validate()) {
            showLoading()
            call = service?.login(email, password)
            call!!.enqueue(object : Callback<AccessToken?> {
                @SuppressLint("CommitPrefEdits")
                override fun onResponse(
                    call: Call<AccessToken?>,
                    response: Response<AccessToken?>
                ) {
                    Log.w(TAG, "onResponse: $response")
                    if (response.isSuccessful) {

                        tokenManager?.saveToken(response.body()!!)
                        Log.w(TAG, "onResponse: " + response.body())
                        Log.i("tokenLog", tokenManager?.token?.getAccessToken().toString())

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()

                    } else {
                        if (response.code() == 422) {
                            handleErrors(response.errorBody())
                        }
                        if (response.code() == 401) {
                            var apiError: ApiError? = null
                            if (response.errorBody() != null) {
                                apiError = converErrors(response.errorBody()!!)
                            }
                            if (apiError != null) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    apiError.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        showForm()
                    }
                }

                override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                    Log.w(TAG, "onFailure: " + t.message)
                    showForm()
                }
            })
        }
    }

    private fun handleErrors(response: ResponseBody?) {
        val apiError = converErrors(response!!)
        if (apiError != null) {
            for ((key, value) in apiError.errors!!) {
                if (key == "username") { binding.tilEmail.error = value[0] }
                if (key == "password") { binding.tilPassword.error = value[0] }
            }
        }
    }

    /**
     * Verwendung der basgeekball AwsomeValidation Bibliothek
     * Testen Sie die Gültigkeit der Login-Felder
     */
    private fun setupRules() {
        validator!!.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email)
        validator!!.addValidation(
            this,
            R.id.til_password,
            RegexTemplate.NOT_EMPTY,
            R.string.err_password
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        facebookManager!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (call != null) {
            call!!.cancel()
            call = null
        }
        facebookManager!!.onDestroy()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@LoginActivity, LogRegActivity::class.java))
    }

}