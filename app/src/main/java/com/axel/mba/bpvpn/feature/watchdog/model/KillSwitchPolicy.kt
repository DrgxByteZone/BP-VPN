package com.axel.mba.bpvpn.feature.watchdog.model

/**
 * Kill switch behavior policy.
 * Created by: Axel & M.B.A
 */
enum class KillSwitchPolicy(val title: String, val description: String) {
    DISABLED("Nonaktif", "Koneksi internet biasa tetap diizinkan bila VPN terputus"),
    ON_UNEXPECTED_DROP("Aktif Saat Putus Mendadak", "Blokir internet seketika jika VPN terputus secara tidak sengaja"),
    STRICT_LOCKDOWN("Lockdown Ketat", "Blokir total semua koneksi tanpa VPN kapan pun")
}
