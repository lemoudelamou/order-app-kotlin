package com.example.solubox.menuItems

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.solubox.MainActivity
import com.example.solubox.R
import com.example.solubox.Utils.TokenManager
import com.example.solubox.databinding.ActivityPromoCodeBinding
import com.example.solubox.model.InviteCode
import com.example.solubox.model.StorePromo
import com.example.solubox.model.User
import com.example.solubox.network.ApiService
import com.example.solubox.network.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromoCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPromoCodeBinding
    var call3: Call<List<InviteCode>?>? = null
    var call2: Call<List<User>?>? = null
    var tokenManager: TokenManager? = null
    var service: ApiService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPromoCodeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        displayCode()


        binding.einloesenBtn.setOnClickListener {
            if(TextUtils.isEmpty(binding.gutscheinEdt.text.toString())){
                binding.gutscheinEdt.error = "Bitte gutschein code eingeben "
            } else {
                getPromoCode(binding.gutscheinEdt.text.toString())
            }
        }

        binding.back.setOnClickListener {
            val i = Intent(this@PromoCodeActivity, MainActivity::class.java)
            startActivity(i)
        }
    }

    // Gutschein Code einlösen
    private fun getPromoCode(txt: String){
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",
            MODE_PRIVATE
        ))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)
        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val user_id = sh.getString("id", "")
        val invitationCode = sh.getString("invit_promo", "")
        call2 = service!!.getPromo(txt)
        call2!!.enqueue(object : Callback<List<User>?> {
            override fun onResponse(
                call2: Call<List<User>?>,
                response: Response<List<User>?>
            ) {
                Log.w("RedeemPromo", "onResponse: $response")
                if (response.isSuccessful) {
                    if(response.body() != null) {

                    if(response.body()!!.isNotEmpty()) {

                        val promoList: List<User> = response.body()!!
                        val promoCodeId = promoList.get(0).id.toString()
                        val promo: String = promoList.get(0).invit_promo.toString()

                        if (txt == promo && promo != invitationCode) {
                            Log.d("useridCode", user_id.toString())
                            Log.d("promoidCode", promoCodeId)
                            Log.d("InvitationCode", invitationCode.toString())

                            binding.gutscheinCode.text = "Gutschein-Code: $promo"
                            binding.anzahlTxt.text = getString(R.string.anzahl, "10")
                            binding.rabattTxt.text = getString(R.string.rabatt, "15")
                            if( binding.anzahlTxt.text != null){
                                binding.anzahlTxt.text = getString(R.string.anzahl, "20")
                            }
                            storePromoCode(user_id.toString(),promo, "invitation", 10, 1, 15)
                        } else{
                            Toast.makeText(applicationContext, "Gutschein code ist ungültig", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Gutschein code ist falsch", Toast.LENGTH_SHORT)
                            .show()
                    }
                    } else {
                        Toast.makeText(applicationContext, "Die Anfrage an der Server ist falschgeschlagen", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call2: Call<List<User>?>, t: Throwable) {
                Log.w("RedeemPromo", "onFailure: " + t.message)
            }
        })
    }

    // Gutschein Anzeigen nach der Einlösung
    fun displayCode(){
        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val user_id = sh.getString("id", "")

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",
            MODE_PRIVATE
        ))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)
        call3 = service!!.displayCode(user_id.toString())
        call3!!.enqueue(object : Callback<List<InviteCode>?> {
            @SuppressLint("StringFormatInvalid")
            override fun onResponse(
                call3: Call<List<InviteCode>?>,
                response: Response<List<InviteCode>?>
            ) {
                Log.w("DisplayPromo", "onResponse: $response")
                if (response.isSuccessful) {
                    if(response.body() != null) {

                        val codeType = response.body()!!.get(0).code_type
                        Log.d("CodeType", codeType.toString())
                        if(response.body()!!.isNotEmpty() ) {
                            val ownerId = response.body()!!.get(0).owner_id
                            val invitationCode = response.body()!!.get(0).code

                            Log.d("ownerId", ownerId.toString())
                            Log.d("Code", invitationCode.toString())
                            binding.gutscheinCode.text = getString(R.string.gutschein_code, invitationCode)
                            binding.anzahlTxt.text = getString(R.string.anzahl, "10")
                            binding.rabattTxt.text = getString(R.string.rabatt, "15")
                        }

                    } else {
                        Toast.makeText(applicationContext, "Die Anfrage an der Server ist falschgeschlagen", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call3: Call<List<InviteCode>?>, t: Throwable) {
                Log.w("DisplayPromo", "onFailure: " + t.message)
            }
        })
    }

    private fun storePromoCode(ownerId: String, promoCode: String, type: String, limit: Int, act: Int, amount: Int){
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)
        call3 = service!!.storeCode(ownerId, promoCode, type, limit, act, amount)
        call3!!.enqueue(object : Callback<List<InviteCode>?> {
            override fun onResponse(
                call3: Call<List<InviteCode>?>,
                response: Response<List<InviteCode>?>,
            ) {
                Log.w("StorePromo", "onResponse: $response")
                if (response.isSuccessful) {
                    Log.d("castele", response.body().toString())
                }
            }
            override fun onFailure(call3: Call<List<InviteCode>?>, t: Throwable) {
                Log.w("StorePromo", "onFailure: " + t.message)
            }
        })
    }

}