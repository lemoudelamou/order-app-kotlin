@file:Suppress("DEPRECATION")

package com.example.solubox.order

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.solubox.R
import com.example.solubox.databinding.ActivityTrackBinding

class TrackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val sh = getSharedPreferences("MySharedPrefs", MODE_PRIVATE)
        val orderID = sh.getString("orderID", "")
        binding.orderId.text= orderID


        val intent = intent
        val orderStatus = intent.getStringExtra("orderStatus")
        getOrderStatus(orderStatus)
    }

    private fun getOrderStatus(orderStatus: String?) {
        if (orderStatus == "0") {
            val alfa = 0.5.toFloat()
            setStatus(alfa)
        } else if (orderStatus == "1") {
            val alfa = 1.toFloat()
            setStatus1(alfa)
        } else if (orderStatus == "2") {
            val alfa = 1.toFloat()
            setStatus2(alfa)
        } else if (orderStatus == "3") {
            val alfa = 1.toFloat()
            setStatus3(alfa)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setStatus(alfa: Float) {
        val myf = 0.5.toFloat()
        binding.viewOrderPlaced.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.viewOrderConfirmed.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.orderprocessed.alpha = alfa
        binding.viewOrderProcessed.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.conDivider.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.placedDivider.alpha = alfa
        binding.imgOrderconfirmed.alpha = alfa
        binding.placedDivider.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.textConfirmed.alpha = alfa
        binding.textorderprocessed.alpha = alfa
        binding.viewOrderPickup.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.readyDivider.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.orderpickup.alpha = alfa
        binding.textorderpickup.alpha = myf
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setStatus1(alfa: Float) {
        val myf = 0.5.toFloat()
        binding.viewOrderPlaced.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.viewOrderConfirmed.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.orderprocessed.alpha = myf
        binding.viewOrderProcessed.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.conDivider.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.placedDivider.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.imgOrderconfirmed.alpha = alfa
        binding.textConfirmed.alpha = alfa
        binding.textorderprocessed.alpha = myf
        binding.viewOrderPickup.alpha = myf
        binding.readyDivider.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.orderpickup.alpha = myf
        binding.viewOrderPickup.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.textorderpickup.alpha = myf
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setStatus2(alfa: Float) {
        val myf = 0.5.toFloat()
        binding.viewOrderPlaced.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.viewOrderConfirmed.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.orderprocessed.alpha = alfa
        binding.viewOrderProcessed.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.conDivider.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.placedDivider.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.imgOrderconfirmed.alpha = alfa
        binding.textConfirmed.alpha = alfa
        binding.textorderprocessed.alpha = alfa
        binding.viewOrderPickup.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.readyDivider.background = resources.getDrawable(R.drawable.shape_status_current)
        binding.textorderpickup.alpha = myf
        binding.orderpickup.alpha = myf
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setStatus3(alfa: Float) {
        binding.viewOrderPlaced.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.viewOrderConfirmed.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.orderprocessed.alpha = alfa
        binding.viewOrderProcessed.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.conDivider.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.imgOrderconfirmed.alpha = alfa
        binding.placedDivider.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.textConfirmed.alpha = alfa
        binding.textorderprocessed.alpha = alfa
        binding.viewOrderPickup.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.readyDivider.background = resources.getDrawable(R.drawable.shape_status_completed)
        binding.textorderpickup.alpha = alfa
        binding.orderpickup.alpha = alfa
    }
}
