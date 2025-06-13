package com.axel.mba.bpvpn.core.model

/**
 * Pilihan lokasi server untuk Black Panther VPN.
 * Menghubungkan ke jaringan edge Cloudflare Anycast berkecepatan tinggi.
 */
enum class ServerLocation(
    val id: String,
    val countryName: String,
    val cityName: String,
    val countryCode: String,
    val flagEmoji: String = "",
    val endpointHost: String,
    val endpointPort: Int,
    val peerPublicKey: String,
    val isAuto: Boolean = false,
    val description: String = "",
    val iconResId: Int = com.axel.mba.bpvpn.R.drawable.ic_server
) {
    AUTO(
        id = "auto",
        countryName = "Otomatis (Terdekat)",
        cityName = "Cloudflare Edge",
        countryCode = "AUTO",
        flagEmoji = "",
        endpointHost = "162.159.192.1",
        endpointPort = 2408,
        peerPublicKey = "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo=",
        isAuto = true,
        description = "Pilihan otomatis dengan latensi terendah",
        iconResId = com.axel.mba.bpvpn.R.drawable.ic_server
    ),
    CLOUDFLARE_PRIMARY(
        id = "cf_primary",
        countryName = "Cloudflare Gateway 1",
        cityName = "Port 2408 (UDP)",
        countryCode = "CF",
        flagEmoji = "",
        endpointHost = "162.159.192.1",
        endpointPort = 2408,
        peerPublicKey = "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo=",
        isAuto = false,
        description = "Rute default protokol WireGuard",
        iconResId = com.axel.mba.bpvpn.R.drawable.ic_server
    ),
    CLOUDFLARE_ALT(
        id = "cf_alt",
        countryName = "Cloudflare Gateway 2",
        cityName = "Port 500 (UDP)",
        countryCode = "CF",
        flagEmoji = "",
        endpointHost = "162.159.192.1",
        endpointPort = 500,
        peerPublicKey = "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo=",
        isAuto = false,
        description = "Rute alternatif port ISAKMP/IKE",
        iconResId = com.axel.mba.bpvpn.R.drawable.ic_server
    );

    companion object {
        fun fromId(id: String?): ServerLocation {
            return values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: AUTO
        }
    }
}
