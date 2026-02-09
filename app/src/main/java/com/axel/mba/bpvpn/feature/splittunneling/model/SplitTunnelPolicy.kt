package com.axel.mba.bpvpn.feature.splittunneling.model

/**
 * Split Tunneling policy mode.
 * Created by: Axel & M.B.A
 */
enum class SplitTunnelPolicy(val id: String, val title: String, val description: String) {
    ALL_APPS(
        id = "ALL",
        title = "Semua Aplikasi",
        description = "Seluruh lalu lintas aplikasi diarahkan melalui terowongan VPN"
    ),
    BYPASS_SELECTED(
        id = "BYPASS",
        title = "Bypass Aplikasi Terpilih",
        description = "Aplikasi terpilih (misal: perbankan) langsung terhubung ke internet lokal tanpa VPN"
    ),
    ONLY_SELECTED(
        id = "ONLY",
        title = "Hanya Aplikasi Terpilih",
        description = "Hanya aplikasi yang dipilih yang menggunakan koneksi aman VPN"
    );

    companion object {
        fun fromId(id: String?): SplitTunnelPolicy {
            return values().firstOrNull { it.id == id } ?: ALL_APPS
        }
    }
}
