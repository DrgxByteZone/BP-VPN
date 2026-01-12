package com.axel.mba.bpvpn.feature.watchdog.engine

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.axel.mba.bpvpn.feature.watchdog.model.NetworkType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * System Connectivity observer using Android NetworkCallback.
 * Created by: Axel & M.B.A
 */
class NetworkStateObserver(context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _networkType = MutableStateFlow(NetworkType.NONE)
    val networkType: StateFlow<NetworkType> = _networkType.asStateFlow()

    private val _hasInternet = MutableStateFlow(false)
    val hasInternet: StateFlow<Boolean> = _hasInternet.asStateFlow()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateStatus(network)
        }

        override fun onLost(network: Network) {
            _networkType.value = NetworkType.NONE
            _hasInternet.value = false
        }

        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            updateStatus(network)
        }
    }

    init {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        try {
            connectivityManager.registerNetworkCallback(request, callback)
        } catch (_: Exception) {}
    }

    private fun updateStatus(network: Network) {
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return
        val hasNet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        _hasInternet.value = hasNet

        _networkType.value = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.NONE
        }
    }

    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(callback)
        } catch (_: Exception) {}
    }
}
