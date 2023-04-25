package com.example.solubox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.example.solubox.Utils.TokenManager
import com.example.solubox.databinding.ActivityRedirectBinding
import com.example.solubox.entities.AccessToken
import com.example.solubox.model.InviteCode
import com.example.solubox.model.UserResponse
import com.example.solubox.network.ApiService
import com.example.solubox.network.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRedirectBinding
    private var call: Call<AccessToken?>? = null
    var call3: Call<List<InviteCode>?>? = null
    private var call2: Call<UserResponse?>? = null
    var tokenManager: TokenManager? = null
    var service: ApiService? = null
    var validator: AwesomeValidation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedirectBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
        validator = AwesomeValidation(ValidationStyle.COLORATION)

        getUserDetails()

        binding.nextBtn.setOnClickListener {
            fillForm()
            storePromoCode()
        }
    }


    private fun storePromoCode(){
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",
            MODE_PRIVATE
        ))
        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)

        val code: String = sh.getString("invitCode", "").toString()
        val owner_id = sh.getString("id", "")
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)
        call3 = service!!.storeCode(owner_id!!, code, "invitation", 10, 1, 15)
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

    private fun getUserDetails(){
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)
        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        call2 = service!!.getUser()
        call2!!.enqueue(object : Callback<UserResponse?> {
            override fun onResponse(
                call2: Call<UserResponse?>,
                response: Response<UserResponse?>,
            ) {
                Log.w("UserDetails", "onResponse: $response")
                if (response.isSuccessful) {
                    val vorname: String = response.body()!!.data!![0].name.toString()
                    val nachname: String = response.body()!!.data!![0].lastname.toString()
                    val id: String = response.body()!!.data!![0].id.toString()
                    Log.d("SavedId", id)

                    val userList: List<UserResponse> = listOf(response.body()!!)
                    val ids = userList[0].data!!.get(0).id.toString()

                    myEdit.putString("id", ids)
                    myEdit.putString("name", vorname)
                    myEdit.putString("lastname", nachname)
                    myEdit.putString("email", response.body()!!.data!![0].email.toString())
                    myEdit.putString("phonenumber", response.body()!!.data!![0].phonenumber.toString())
                    myEdit.putString("address", response.body()!!.data!![0].address.toString())
                    myEdit.putString("post_code", response.body()!!.data!![0].post_code.toString())
                    myEdit.putString("city", response.body()!!.data!![0].city.toString())
                    myEdit.apply()

                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call2: Call<UserResponse?>, t: Throwable) {
                Log.w("userDetails", "onFailure: " + t.message)
            }
        })
    }



    fun fillForm() {

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",
            MODE_PRIVATE
        ))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)

        val name = binding.vornameEdt.text.toString()
        val lastname = binding.nachnameEdt.text.toString()
        val phonenumber = binding.phoneEdt.text.toString()
        val address = binding.addressEdt.text.toString()
        val post_code = binding.postcodeEdt.text.toString()
        val city = binding.cityEdt.text.toString()
            showLoading()
            call = service!!.fillForm(name, lastname, phonenumber, address, post_code, city)
            call!!.enqueue(object : Callback<AccessToken?> {
                override fun onResponse(
                    call: Call<AccessToken?>,
                    response: Response<AccessToken?>
                ) {
                    Log.w("fillForm", "onResponse: $response")

                    if (response.isSuccessful) {
                        Log.w("fillForm", "onResponse: " + response.body())
                        startActivity(Intent(this@UserDataActivity, MainActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(applicationContext,
                            "Data not saved",
                            Toast.LENGTH_SHORT).show()
                        showForm()
                    }
                }

                override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                    Log.w("fillForm", "onFailure: " + t.message)
                    showForm()
                }
            })

    }

    private fun showLoading() {
        TransitionManager.beginDelayedTransition(binding.redirectLayout)
        binding.formContainer.visibility = View.GONE
        binding.nextBtn.visibility = View.GONE
        binding.loader.visibility = View.VISIBLE
    }

    private fun showForm() {
        TransitionManager.beginDelayedTransition(binding.redirectLayout)
        binding.formContainer.visibility = View.VISIBLE
        binding.nextBtn.visibility = View.VISIBLE
        binding.loader.visibility = View.GONE
    }
}