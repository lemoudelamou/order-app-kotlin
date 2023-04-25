package com.example.solubox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.solubox.databinding.ActivityMapsBinding
import com.example.solubox.model.ShopModel
import com.example.solubox.network.ApiService
import com.example.solubox.network.RetrofitBuilder
import com.google.android.gms.maps.model.CameraPosition
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getShopCoordinate()


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    fun getShopCoordinate(){
        val call3 = RetrofitBuilder.createService(ApiService::class.java).getShop()
        call3.enqueue(object : Callback<List<ShopModel>> {
            override fun onResponse(
                call3: Call<List<ShopModel>>?,
                response: Response<List<ShopModel>>?,
            ) {
                Log.w("CoordinateDetails", "onResponse: $response")
                if (response!!.isSuccessful) {
                    if(response.body() != null) {

                        val data: List<ShopModel> = response.body()!!
                        for(i in response.body()!!.indices) {
                            val lat: Double = data.get(i).lat
                            val lon: Double = data.get(i).lon
                            val title: String = data.get(i).shop_name.toString()

                            val ber = LatLng(lat, lon)
                            mMap.addMarker(MarkerOptions()
                                .position(ber)
                                .title(title))

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(ber))

                            Log.d("latitude", lat.toString())
                            Log.d("longitude", lon.toString())
                        }

                        // Add a marker in Berlin and move the camera
                        val berlin = LatLng(52.520008, 13.404954)

                        val cameraPosition = CameraPosition.Builder()
                            .target(berlin) // Sets the center of the map to Berlin View
                            .zoom(12f)            // Sets the zoom
                            .build()              // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    } else {
                        Toast.makeText(applicationContext, "Die Anfrage an der Server ist falschgeschlagen", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(applicationContext, "Unmöglich die Map Daten zu laden", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            override fun onFailure(call3: Call<List<ShopModel>>, t: Throwable) {
                Log.w("CoordinateDetails", "onFailure: " + t.message)
            }
        })
    }




}