package com.example.solubox

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View.*
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.solubox.RegisterLogin.LoginActivity
import com.example.solubox.Utils.TokenManager
import com.example.solubox.adapters.ShopListAdapter
import com.example.solubox.adapters.ShopListAdapter.ShopListClickListener
import com.example.solubox.databinding.ActivityMainBinding
import com.example.solubox.entities.AccessToken
import com.example.solubox.fragment.BottomNavigationDrawerFragment
import com.example.solubox.fragment.ProfilDialogFragment
import com.example.solubox.model.InviteCode
import com.example.solubox.model.ShopModel
import com.example.solubox.model.UserResponse
import com.example.solubox.network.ApiService
import com.example.solubox.network.NetworkConnectivityChecker
import com.example.solubox.network.RetrofitBuilder
import com.example.solubox.order.ShopItemsActivity
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.flow.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity(), ShopListClickListener {

    companion object { private const val TAG = "update" }


    private var call1: Call<AccessToken?>? = null
    private var call2: Call<UserResponse?>? = null
    private var call: Call<AccessToken?>? = null
    private var service: ApiService? = null
    private var tokenManager: TokenManager? = null
    private var drawer: DrawerLayout? = null
    private lateinit var binding: ActivityMainBinding
    private var shop: List<ShopModel> = listOf()
    private var matchedShop: List<ShopModel> = listOf()
    private var shopListAdapter: ShopListAdapter = ShopListAdapter(shop, this )
    private var locationPermissionGranted = false
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.bottomAppBar2)
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)

        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val name = sh.getString("name", "")
        val lastname = sh.getString("lastname", "")
        binding.welcomeMsg.text =  getString(R.string.welcome_messages, name, lastname)


        getUserDetails()
        displayShops()
        getLocationPermission()

        binding.fab.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }


    override fun onResume(){
        super.onResume()
        setInternetConnectivityObserver()
        NetworkConnectivityChecker.checkForConnection()
    }

    /**
     * das ServiceBuilder-Objekt holen
     * Erstellen eines Aufrufs unter Verwendung des Retrofit-Objekts und Aufrufen der login()-Methode
     * Senden der Anfrage an das Netz unter Verwendung der Call.enqueue-Methode von Retrofit.
     * Darin wird ein Callback übergeben, der eine Antwort in Form eines AccessToken-Objekts liefert.
    Dann werden die beiden Methoden implementiert: onResponse und onFailure.
     * In onResponse wird die RecyclerView initialisiert und das Antwortobjekt darin angezeigt
     */
   fun displayShops(){
       val sharedPreferences = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
       val myEdit = sharedPreferences.edit()
       val call3 = RetrofitBuilder.createService(ApiService::class.java).getShop()
       call3.enqueue(object : Callback<List<ShopModel>> {
           override fun onResponse(
               call3: Call<List<ShopModel>>?,
               response: Response<List<ShopModel>>?,
           ) {
               Log.w("ShopDetails", "onResponse: $response")
               if (response!!.isSuccessful) {
                   if(response.body() != null) {

                        for (i in  response.body()!!.indices) {
                            shop = response.body()!!
                            myEdit.putInt("id_shop", response.body()!![i].id)
                            Log.d("ShopShopList", response.body()!![i].id.toString())
                        }

                       binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                       ShopListAdapter(shop,shopListAdapter.clickListener).also {
                           binding.recyclerView.adapter = it
                           binding.recyclerView.adapter!!.notifyDataSetChanged()
                       }
                       myEdit.apply()
                   } else {
                       Toast.makeText(applicationContext, "REQUEST IS EMPTY!!", Toast.LENGTH_SHORT)
                           .show()
                   }
               } else {
                   Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT)
                       .show()
               }
           }
           override fun onFailure(call3: Call<List<ShopModel>>, t: Throwable) {
               Log.w("ShopDetails", "onFailure: " + t.message)
           }
       })

       binding.searchView.isSubmitButtonEnabled = true
    }

    override fun onItemClick(shopModel: ShopModel?) {
        if (connectivityCheck()) {
            val intent = Intent(this@MainActivity, ShopItemsActivity::class.java)
            intent.putExtra("ShopModel", shopModel)
            startActivity(intent)
        }else {
            Toast.makeText(applicationContext, "Bitte überprüfe deine Internetverbindung", Toast.LENGTH_SHORT).show()
        }
    }

    // InternetVerbindung überprüfen
    fun connectivityCheck(): Boolean {
        val mobileNwInfo: Boolean
        val conxMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        mobileNwInfo = try {
            conxMgr.activeNetworkInfo!!.isConnected
        } catch (e: NullPointerException) {
            false
        }

        return mobileNwInfo
    }


    //AlertDialog to confirm or cancel logging out
    /**
     * Erstellen eines Aufrufs unter Verwendung des Retrofit-Objekts und Aufrufen der logout()-Methode
     * Senden der Anfrage an das Netz unter Verwendung der Call.enqueue-Methode von Retrofit.
     * Darin wird ein Callback übergeben, der eine Antwort in Form eines AccessToken-Objekts liefert.
    Dann werden die beiden Methoden implementiert: onResponse und onFailure.
     * Bei erfolgreicher Abmeldung wird das accessToken gelöscht. Wenn nicht, wird eine Toast-Meldung angezeigt
     */
    @SuppressLint("RestrictedApi")
    fun logout() {

        val builder = AlertDialog.Builder(this)
        val title = SpannableString("Abmelden")
        val message = SpannableString("Wollen Sie sich wirklich abmelden?")

        // alert dialog title align center
        // alert dialog message align center

        title.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0,
            title.length,
            0
        )
        message.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0,
            title.length,
            0
        )

        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("JA") { _, _ ->
            tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
            call1 = service!!.logout(tokenManager!!.token.accessToken)
            call1!!.enqueue(object : Callback<AccessToken?> {
                override fun onResponse(
                    call1: Call<AccessToken?>,
                    response: Response<AccessToken?>,
                ) {
                    Log.w("Logged out", "onResponse: $response")
                    getSharedPreferences("prefs", MODE_PRIVATE).edit().clear().apply()
                    getSharedPreferences("MySharedPrefs", MODE_PRIVATE).edit().remove("invitCode").apply()
                    getSharedPreferences("MySharedPrefs", MODE_PRIVATE).edit().remove("id").apply()

                    tokenManager!!.deleteToken()
                    if (response.isSuccessful) {
                        Log.i("tokenLog", tokenManager?.token?.accessToken.toString())
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        Toast.makeText(applicationContext, "User Logged out successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        if (response.code() == 401) {
                            Toast.makeText(this@MainActivity, "User can't be logged out", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                override fun onFailure(call1: Call<AccessToken?>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "bad request", Toast.LENGTH_LONG).show()
                }
            })
        }
        builder.setNegativeButton("Abbrechen") { _, _ ->
            // User cancelled the dialog
        }
        val dialog = builder.create()
        if (!isFinishing) {
            dialog.show()
        }
        //dialog.show()
        drawer?.closeDrawer(GravityCompat.START)
    }

    /**
     * Erstellen eines Aufrufs unter Verwendung des Retrofit-Objekts und Aufrufen der getUser()-Methode
     * Senden der Anfrage an das Netz unter Verwendung der Call.enqueue-Methode von Retrofit.
     * Darin wird ein Callback übergeben, der eine Antwort in Form  BenutzerDetails-Objekten liefert.
    Dann werden die beiden Methoden implementiert: onResponse und onFailure.
     * Die Retrofit response Objekte werden in den Sahred preferences gespeichert.
     */
    private fun getUserDetails(){
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)
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
                    Log.w("Namenull", vorname)

                    if (vorname != "null" && nachname != "null") {
                        binding.welcomeMsg.text =
                            getString(R.string.welcome_messages, vorname, nachname)
                    } else{
                        binding.welcomeMsg.text =
                            getString(R.string.welcome_messages, "", "")
                    }
                    val sharedPreferences = getSharedPreferences("MySharedPrefs",
                       MODE_PRIVATE
                    )
                    val myEdit = sharedPreferences.edit()
                    myEdit.putString("id", response.body()!!.data!![0].id.toString())
                    myEdit.putString("name", response.body()!!.data!![0].name.toString())
                    myEdit.putString("lastname", response.body()!!.data!![0].lastname.toString())
                    myEdit.putString("email", response.body()!!.data!![0].email.toString())
                    myEdit.putString("phonenumber", response.body()!!.data!![0].phonenumber.toString())
                    myEdit.putString("address", response.body()!!.data!![0].address.toString())
                    myEdit.putString("post_code", response.body()!!.data!![0].post_code.toString())
                    myEdit.putString("city", response.body()!!.data!![0].city.toString())
                    myEdit.putString("invit_promo", response.body()!!.data!![0].invit_promo.toString())

                    myEdit.apply()
                    //Log.i("Test", binding.textView.text as String)

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



    /**
     * Erstellen eines Aufrufs unter Verwendung des Retrofit-Objekts und Aufrufen der getUser()-Methode
     * Senden der Anfrage an das Netz unter Verwendung der Call.enqueue-Methode von Retrofit.
     * Darin wird ein Callback übergeben, der eine Antwort in Form BenutzerDetails-Objekten liefert.
    Dann werden die beiden Methoden implementiert: onResponse und onFailure.

     */
    fun update() {
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs",
            MODE_PRIVATE
        ))
        service = RetrofitBuilder.createServiceWithAuth(ApiService::class.java, tokenManager!!)

        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val name = sh.getString("name", "")
        val lastname = sh.getString("lastname", "")
        val email = sh.getString("email", "")
        val phonenumber = sh.getString("phonenumber", "")
        val address = sh.getString("address", "")
        val post_code = sh.getString("post_code", "")
        val city = sh.getString("city", "")

        call = service!!.update(name, lastname, email, phonenumber, address, post_code, city)
        call!!.enqueue(object : Callback<AccessToken?> {
            override fun onResponse(call: Call<AccessToken?>, response: Response<AccessToken?>) {
                Log.w("update", "onResponse: $response")

                if (response.isSuccessful) {
                    Log.w("update", "onResponse: " + response.body())
                    binding.welcomeMsg.text =  getString(R.string.welcome_messages, name, lastname)
                } else {
                    Toast.makeText(applicationContext, "User Data not updated", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<AccessToken?>, t: Throwable) {
                Log.w("updateUserDetails", "onFailure: " + t.message)
            }
        })
    }

    //implementing the left icon in the bottom navigation bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                val bottomNavDrawerFragment = BottomNavigationDrawerFragment()
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
            }
            R.id.search -> {
                when (binding.searchView.visibility){
                    INVISIBLE -> {
                        binding.searchView.visibility = VISIBLE
                    }
                    VISIBLE -> {
                        binding.searchView.visibility = INVISIBLE
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * das untere Menü aufblasen
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * die Profilfelder anzeigen
     */
    fun showProfile(){
        val profileFragment = ProfilDialogFragment()
        profileFragment.show(supportFragmentManager, profileFragment.tag)
    }


    private fun setInternetConnectivityObserver() {
        NetworkConnectivityChecker.observe(this, liveDataObserver)
    }

    private val liveDataObserver: Observer<Boolean?> = Observer { isConnected ->
        if (!isConnected!!) {
            //Can use your own logic later -- Using snackbar as default. Build your own listener to create custom view
            binding.mainLayout.let {
                Toast.makeText(applicationContext,"Bitte deine Internetverbindung überprüfen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Erlaubnis für den Standort anfordern, damit wir den Standort des
      Gerätes abrufen können. Das Ergebnis der Erlaubnisanfrage wird von einem Callback verarbeitet,
      onRequestPermissionsResult.
     */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }


    /**
     *  die Anwendung in den Hintergrund zu schicken,
     wenn man auf die Rückwärtstaste des Geräts klickt
     */
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (call1 != null) {
            call1!!.cancel()
            call1 = null
        }
    }


}