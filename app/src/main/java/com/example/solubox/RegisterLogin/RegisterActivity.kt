package com.example.solubox.RegisterLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.transition.TransitionManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.example.solubox.MainActivity
import com.example.solubox.R
import com.example.solubox.UserDataActivity
import com.example.solubox.Utils.TokenManager
import com.example.solubox.Utils.TokenManager.Companion.getInstance
import com.example.solubox.Utils.Utils.converErrors
import com.example.solubox.databinding.ActivityRegisterBinding
import com.example.solubox.entities.AccessToken
import com.example.solubox.network.ApiService
import com.example.solubox.network.NetworkConnectivityChecker
import com.example.solubox.network.RetrofitBuilder.createService
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class RegisterActivity : AppCompatActivity() {

    var snackbar: Snackbar? = null
    var service: ApiService? = null
    var call: Call<AccessToken?>? = null
    var validator: AwesomeValidation? = null
    var tokenManager: TokenManager? = null
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        service = createService(ApiService::class.java)
        validator = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)
        tokenManager = getInstance(getSharedPreferences("prefs", MODE_PRIVATE))

        val generate = generateInvCode()

        setupRules()
        binding.btnRegister.setOnClickListener {
            setInternetConnectivityObserver()
            register(generate)
        }

        binding.toLogin.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }

        if (tokenManager!!.token.accessToken != null) {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        NetworkConnectivityChecker.checkForConnection()
    }


    private fun setInternetConnectivityObserver() {
        NetworkConnectivityChecker.observe(this, liveDataObserver)
    }

    private val liveDataObserver: Observer<Boolean?> = Observer { isConnected ->
        if (!isConnected!!) {
            //Can use your own logic later -- Using snackbar as default. Build your own listener to create custom view
            binding.registerLayout.let {
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
    private fun showLoading() {
        TransitionManager.beginDelayedTransition(binding.registerLayout)
        binding.formContainer.visibility = View.GONE
        binding.loader.visibility = View.VISIBLE
    }

    /**
     * Ausblenden des Ladelogos
     * Anzeigen der Bildschirmelemente
     */
    private fun showForm() {
        TransitionManager.beginDelayedTransition(binding.registerLayout)
        binding.formContainer.visibility = View.VISIBLE
        binding.loader.visibility = View.GONE
    }

    /**
     * Erstellen eines Aufrufs unter Verwendung des Retrofit-Objekts und Aufrufen der register()-Methode
     * Senden der Anfrage an das Netz unter Verwendung der Call.enqueue-Methode von Retrofit.
     * Darin wird ein Callback übergeben, der eine Antwort in Form eines AccessToken-Objekts liefert.
      Dann werden die beiden Methoden implementiert: onResponse und onFailure.
     * Abhören, wenn die Registrieren-Schaltfläche angeklickt wird,
    prüfen, ob der Benutzer einer gültigen E-Mail und ein gültiges Passwort eingegeben hat
    wenn ja, Reggistrierung durchführen, sonst Fehlermeldung ausgeben.
     */
    private fun register(gen: String) {
        val sharedPreferences = getSharedPreferences("MySharedPrefs",
            MODE_PRIVATE
        )
        val myEdit = sharedPreferences.edit()

        val email = binding.tilEmail.editText?.text.toString()
        val password = binding.tilPassword.editText?.text.toString()
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        validator!!.clear()
        if (validator!!.validate()) {
            showLoading()
            call = service?.register( email, password, gen)
            call!!.enqueue(object : Callback<AccessToken?> {
                override fun onResponse(call: Call<AccessToken?>, response: Response<AccessToken?>) {
                    Log.w(TAG, "onResponse: $response")
                    if (response.isSuccessful) {
                        tokenManager!!.saveToken(response.body()!!)

                        val code: String =  gen
                        Log.w(TAG, "onResponse: " + response.body())
                        myEdit.putString("invitCode", code)
                        myEdit.apply()

                        startActivity(Intent(this@RegisterActivity, UserDataActivity::class.java))
                        finish()
                    } else {
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


    /**
     * Verwendung der basgeekball AwsomeValidation Bibliothek
     * Testen Sie die Gültigkeit der Registrierung-Felder
     */
    private fun setupRules() {
        validator!!.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email)
        validator!!.addValidation(this, R.id.til_password, "[a-zA-Z0-9]{8,}", R.string.err_password)
    }


    /**
     * einen eindeutigen Code für jeden neuen Benutzer generieren
     */
    fun generateInvCode(): String{
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString: String = List(8) { alphabet.random() }.joinToString("")

        return randomString
    }


    override fun onDestroy() {
        super.onDestroy()
        if (call != null) {
            call!!.cancel()
            call = null
        }
    }


    override fun onBackPressed() {
        startActivity(Intent(this@RegisterActivity, LogRegActivity::class.java))
        finish()
    }

    companion object { private const val TAG = "RegisterActivity" }
}