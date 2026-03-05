package com.axel.mba.bpvpn.feature.obfuscation.model

/**
 * Obfuscation level for bypassing deep packet inspection (DPI) & cellular throttling.
 * Created by: Axel & M.B.A
 */
enum class ObfuscationLevel(val title: String, val description: String) {
    DISABLED("Standar WireGuard (Tanpa Obfuscation)", "Protokol WireGuard standar tanpa modifikasi paket"),
    LOW_PADDING("Padding Acak Ringan", "Menambahkan padding acak 16-64 byte untuk mengaburkan ukuran paket"),
    JUNK_HEADER("Header Morphing", "Mengaburkan signature handshake awal agar lolos inspeksi DPI operator"),
    FULL_STEALTH("Full Stealth Obfuscation", "Menggabungkan padding dinamis, MSS clamping, dan jitter transmisi")
}
