package com.example.solubox.menuItems

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.solubox.MainActivity
import com.example.solubox.Utils.TokenManager
import com.example.solubox.databinding.ActivityInviteBinding
import com.example.solubox.model.InviteCode
import com.example.solubox.model.User
import com.example.solubox.network.ApiService
import com.example.solubox.network.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InviteActivity : AppCompatActivity() {

    var call2: Call<List<User>?>? = null
    var service: ApiService? = null
    var tokenManager: TokenManager? = null
    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null
    private lateinit var binding: ActivityInviteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getPromoInv()

        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?

        binding.goBack.setOnClickListener {
            val i = Intent(this@InviteActivity, MainActivity::class.java)
            startActivity(i)
        }

        binding.copyCode.setOnClickListener{
            copyText(view)
        }

        binding.showBtn.setOnClickListener{
            shareApp(this)
        }

    }

    fun shareApp(context: Context) {
        val appPackageName: String = context.getPackageName()
        val sendIntent = Intent()
        myClip = ClipData.newPlainText("text", binding.codeField.text)
         myClipboard?.setPrimaryClip(myClip!!)
        val abc = myClipboard?.getPrimaryClip()
        val item = abc?.getItemAt(0)
        val copiedCode  = item?.text.toString()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Gutschein für uns beide! Nutze meinen Code nach der Registrierung und wir erhalten beide 10 x 15 % Rabatt. Verwende den Code: $copiedCode. Viel Spaß! Die App laden: https://play.google.com/store/apps/details?id=$appPackageName"
        )
        sendIntent.type = "text/plain"
        context.startActivity(sendIntent)
    }


    private fun getPromoInv(){
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",
            MODE_PRIVATE
        ))
        val sharedPreferences = getSharedPreferences("MySharedPrefs",
            MODE_PRIVATE
        )
        val myEdit = sharedPreferences.edit()
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)
        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val id = sh.getString("id", "")
       call2 = service!!.getInvCode(id.toString())
        call2!!.enqueue(object : Callback<List<User>?> {
            override fun onResponse(
                call2: Call<List<User>?>,
                response: Response<List<User>?>
            ) {
                Log.w("PromoInvit", "onResponse: $response")
                if (response.isSuccessful) {
                    if(response.body() != null) {
                        if(response.body()!!.isNotEmpty()) {

                            val promo: String = response.body()!!.get(0).invit_promo.toString()
                            myEdit.putString("invitationCode", promo)
                            binding.codeField.text = promo
                            Log.d("PromoCode", promo)
                            myEdit.apply()
                        } else{
                            Toast.makeText(applicationContext, "Not able to load the code", Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {
                        Toast.makeText(applicationContext, "Die Anfrage an der Server ist falschgeschlagen", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call2: Call<List<User>?>, t: Throwable) {
                Log.w("PromoInvit", "onFailure: " + t.message)
            }
        })

    }




    fun copyText(view: View) {
        myClip = ClipData.newPlainText("text", binding.codeField.text)
        myClipboard?.setPrimaryClip(myClip!!)

        Toast.makeText(this, "Kopiert", Toast.LENGTH_SHORT).show()
    }
}