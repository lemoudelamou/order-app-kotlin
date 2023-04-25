@file:Suppress("DEPRECATION")

package com.example.solubox.order

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.solubox.R
import com.example.solubox.Utils.TokenManager
import com.example.solubox.adapters.PlaceYourOrderAdapter
import com.example.solubox.databinding.ActivityPlaceYourOrderBinding
import com.example.solubox.entities.AccessToken
import com.example.solubox.model.ShopModel
import com.example.solubox.network.ApiService
import com.example.solubox.network.RetrofitBuilder

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaceYourOrderActivity : AppCompatActivity() {
    private var inputName: EditText? = null
    private var inputAddress: EditText? = null
    private var inputCity: EditText? = null
    private var inputState: EditText? = null
    private var inputZip: EditText? = null
    private var inputCardNumber: EditText? = null
    private var inputCardExpiry: EditText? = null
    private var inputCardPin: EditText? = null
    private var cartItemsRecyclerView: RecyclerView? = null
    private var tvSubtotalAmount: TextView? = null
    private var tvDeliveryChargeAmount: TextView? = null
    private var tvDeliveryCharge: TextView? = null
    private var tvTotalAmount: TextView? = null
    private var tvCardDetails: TextView? = null
    private var buttonPlaceYourOrder: TextView? = null
    private var switchPayment: SwitchCompat? = null
    private var isDeliveryOn = false
    private var placeYourOrderAdapter: PlaceYourOrderAdapter? = null
    private lateinit var binding: ActivityPlaceYourOrderBinding
    private var call1: Call<AccessToken?>? = null
    private var service: ApiService? = null
    private var tokenManager: TokenManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlaceYourOrderBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
         val shopModel: ShopModel? = intent.getParcelableExtra("ShopModel")
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)

        inputName = binding.inputName
        inputAddress =  binding.inputAddress
        inputCity =  binding.inputCity
        inputState =  binding.inputState
        inputZip =  binding.inputZip
        inputCardNumber =  binding.inputCardNumber
        inputCardExpiry =  binding.inputCardExpiry
        inputCardPin =  binding.inputCardPin
        tvSubtotalAmount =  binding.tvSubtotalAmount
        tvDeliveryChargeAmount =  binding.tvDeliveryChargeAmount
        tvDeliveryCharge =  binding.tvDeliveryCharge
        tvTotalAmount =  binding.tvTotalAmount
        tvCardDetails = binding.tvCardDetails
        buttonPlaceYourOrder =  binding.buttonPlaceYourOrder
        switchPayment =  binding.switchDelivery
        cartItemsRecyclerView =  binding.cartItemsRecyclerView
        buttonPlaceYourOrder!!.setOnClickListener {
            onPlaceOrderButtonClick(
                shopModel
            )
            orderDetails()
        }
        tvCardDetails!!.visibility = View.GONE
        inputCardNumber!!.visibility = View.GONE
        inputCardExpiry!!.visibility = View.GONE
        inputCardPin!!.visibility = View.GONE

        switchPayment!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                inputAddress!!.visibility = View.VISIBLE
                inputCity!!.visibility = View.VISIBLE
                inputState!!.visibility = View.VISIBLE
                inputZip!!.visibility = View.VISIBLE
                tvDeliveryChargeAmount!!.visibility = View.VISIBLE
                tvDeliveryCharge!!.visibility = View.VISIBLE
                tvCardDetails!!.visibility = View.VISIBLE
                inputCardNumber!!.visibility = View.VISIBLE
                inputCardExpiry!!.visibility = View.VISIBLE
                inputCardPin!!.visibility = View.VISIBLE
                isDeliveryOn = true
                calculateTotalAmount(shopModel)
            } else {
                inputAddress!!.visibility = View.GONE
                inputCity!!.visibility = View.GONE
                inputState!!.visibility = View.GONE
                inputZip!!.visibility = View.GONE
                tvCardDetails!!.visibility = View.GONE
                inputCardNumber!!.visibility = View.GONE
                inputCardExpiry!!.visibility = View.GONE
                inputCardPin!!.visibility = View.GONE
                isDeliveryOn = false
                calculateTotalAmount(shopModel)
            }
        }
        initRecyclerView(shopModel)
        calculateTotalAmount(shopModel)
    }

    /**
     * Berechnen des Gesamtbetrags des Kaufs
     * Es werden nur zwei Zahlen nach dem Komma angezeigt.
     */
    private fun calculateTotalAmount(shopModel: ShopModel?) {
        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        var subTotalAmount = 0.0f
        for (m in shopModel!!.menus!!) {
            subTotalAmount += m!!.price * m.totalInCart
            myEdit.putString("quantity",m.totalInCart.toString())
        }
        val subTotalAmountRounded = String.format("%.2f", subTotalAmount)
        tvSubtotalAmount!!.text = getString(R.string.euro, subTotalAmountRounded.toString())
        val deliveryCharge = ((subTotalAmount * 20) / 100)
        val DeliveryChargeRounded = String.format("%.2f", deliveryCharge)
        tvDeliveryChargeAmount!!.text = getString(R.string.euro, DeliveryChargeRounded)
        val totalAmount = deliveryCharge + subTotalAmount

        val totalAmountRounded = String.format("%.2f", totalAmount)
        tvTotalAmount!!.text = getString(R.string.euro, totalAmountRounded)
        myEdit.apply()
    }

    /**
     * Prüfen, ob die Felder nicht leer sind
     */
    private fun onPlaceOrderButtonClick(shopModel: ShopModel?) {

        if (TextUtils.isEmpty(inputName!!.text.toString())) {
            inputName!!.error = getString(R.string.bitte_name_eingeben)
            return
        } else if (isDeliveryOn && TextUtils.isEmpty(inputAddress!!.text.toString())) {
            inputAddress!!.error = getString(R.string.bitte_adresse_eingeben)
            return
        } else if (isDeliveryOn && TextUtils.isEmpty(inputCity!!.text.toString())) {
            inputCity!!.error = getString(R.string.bitte_stadt_eingeben)
            return
        } else if (isDeliveryOn && TextUtils.isEmpty(inputState!!.text.toString())) {
            inputState!!.error = getString(R.string.bitte_postleitzahl_eingeben)
            return
        } else if (isDeliveryOn && TextUtils.isEmpty(inputCardNumber!!.text.toString())) {
            inputCardNumber!!.error = getString(R.string.bitte_kartennummer_eingeben)
            return
        } else if (isDeliveryOn && TextUtils.isEmpty(inputCardExpiry!!.text.toString())) {
            inputCardExpiry!!.error = getString(R.string.bitte_karten_ablaufdatum_eingeben_mm_yyyy)
            return
        } else if (isDeliveryOn && TextUtils.isEmpty(inputCardPin!!.text.toString())) {
            inputCardPin!!.error = getString(R.string.bitte_karten_geheimzahl_eingeben_3_digits)
            return
        }
        //start success activity..
        val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        val orderID = orderID()
        myEdit.putString("orderID", orderID)
        myEdit.apply()
        orderDetails()
        val i = Intent(this@PlaceYourOrderActivity, TrackActivity::class.java)
        startActivityForResult(i, 1000)
    }

    /**
     * die RecyclerView initialisieren
     */
    private fun initRecyclerView(shopModel: ShopModel?) {
        cartItemsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        placeYourOrderAdapter = PlaceYourOrderAdapter(shopModel!!.menus)
        cartItemsRecyclerView!!.adapter = placeYourOrderAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000) {
            setResult(RESULT_OK)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * Erstellen eines Aufrufs unter Verwendung des Retrofit-Objekts und Aufrufen der orderDetails()-Methode
     * Senden der Anfrage an das Netz unter Verwendung der Call.enqueue-Methode von Retrofit.
     * Darin wird ein Callback übergeben, der eine Antwort in Form eines AccessToken-Objekts liefert.
    Dann werden die beiden Methoden implementiert: onResponse und onFailure.
     */
    fun orderDetails() {
        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val user_id = sh.getString("id", "")
        val quantity = sh.getString("totalItems", "")
        val productN = sh.getString("itemName", "")

        call1 = service!!.orderDetails(user_id,productN, quantity, tvSubtotalAmount!!.text.toString() ,tvDeliveryChargeAmount!!.text.toString(), tvTotalAmount!!.text.toString())
        call1!!.enqueue(object : Callback<AccessToken?> {
            @SuppressLint("StringFormatInvalid")
            override fun onResponse(call1: Call<AccessToken?>, response: Response<AccessToken?>) {
                Log.w("orderDetail", "onResponse: $response")

                if (response.isSuccessful) {
                    Log.w("OrderDetails", "onResponse: " + response.body())
                } else {
                    Toast.makeText(applicationContext, "OrderDetails can't be displayed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call1: Call<AccessToken?>, t: Throwable) {
                Log.w("userDetails", "onFailure: " + t.message)
            }
        })
    }

    /**
     * Erzeugen einer eindeutigen Bestellnummer mit 8 Zeichen
     */
    fun orderID(): String{
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString: String = List(8) { alphabet.random() }.joinToString("")

        return randomString
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        if (call1 != null) {
            call1!!.cancel()
            call1 = null
        }
    }
}