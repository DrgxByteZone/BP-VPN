package com.axel.mba.bpvpn.vpn.firewall

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import java.net.InetSocketAddress

class UidResolver(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    private val packageManager = context.packageManager

    fun resolveUid(protocol: Int, srcIp: String, srcPort: Int, dstIp: String, dstPort: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && connectivityManager != null) {
            try {
                val localAddress = InetSocketAddress(srcIp, srcPort)
                val remoteAddress = InetSocketAddress(dstIp, dstPort)
                return connectivityManager.getConnectionOwnerUid(protocol, localAddress, remoteAddress)
            } catch (e: Exception) {
                // Fallback or permission check
            }
        }
        return -1
    }

    fun getPackageNameForUid(uid: Int): String? {
        if (uid <= 0) return null
        return try {
            packageManager.getNameForUid(uid)
        } catch (e: Exception) {
            null
        }
    }
}
