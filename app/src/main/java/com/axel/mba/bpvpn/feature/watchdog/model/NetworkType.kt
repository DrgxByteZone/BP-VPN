package com.axel.mba.bpvpn.feature.watchdog.model

/**
 * Underlying network connectivity transport type.
 * Created by: Axel & M.B.A
 */
enum class NetworkType(val displayName: String) {
    WIFI("Wi-Fi"),
    CELLULAR("Jaringan Seluler (LTE/5G)"),
    ETHERNET("Ethernet"),
    NONE("Tidak Ada Jaringan")
}
