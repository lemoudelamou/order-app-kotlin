package com.example.solubox.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi


object NetworkConnectivityChecker : SingleLiveEvent<Boolean>() {

    lateinit var networkConnectivityUtil: NetworkConnectivityUtil
    private lateinit var connectivityManager: ConnectivityManager

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActive() {
        registerCallback()
        Log.d("DG", "Network Connectivity Registered")
        super.onActive()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInactive() {
        removeCallback()
        Log.d("DG", "Network Connectivity Unregistered")
        super.onInactive()
    }

    fun checkForConnection() {
        value = networkConnectivityUtil.isConnected()
    }

    fun hasConnection():Boolean = networkConnectivityUtil.isConnected()

    //Should be injected in Application class : )
    fun init(context: Context) {
        networkConnectivityUtil = NetworkConnectivityUtil(context)
        connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }


    private fun notifyObservers(connectionStatus: Boolean) {
        postValue(connectionStatus)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun registerCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun removeCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            notifyObservers(true)
            super.onAvailable(network)
        }

        override fun onLost(network: Network) {
            notifyObservers(false)
            super.onLost(network)
        }
    }
}