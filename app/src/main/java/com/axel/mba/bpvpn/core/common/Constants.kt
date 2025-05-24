package com.axel.mba.bpvpn.core.common

object Constants {
    const val APP_NAME = "BP VPN"
    const val APP_FULL_NAME = "Black Panther VPN"
    const val CREDITS_AUTHORS = "Created by: Axel & M.B.A"

    // Network & VPN Parameters
    const val VPN_INTERFACE_NAME = "bp0"
    const val VPN_IPV4_ADDRESS = "10.240.0.2"
    const val VPN_IPV4_SUBNET_PREFIX = 32
    const val VPN_MTU = 1500

    // High-speed encrypted upstream DNS resolvers
    const val DNS_CLOUDFLARE_V4_PRIMARY = "1.1.1.1"
    const val DNS_CLOUDFLARE_V4_SECONDARY = "1.0.0.1"
    const val DNS_QUAD9_V4 = "9.9.9.9"
    const val DOH_CLOUDFLARE_URL = "https://cloudflare-dns.com/dns-query"

    // Real Public IP Lookup Endpoints
    const val IP_LOOKUP_IPIFY_URL = "https://api.ipify.org?format=json"
    const val IP_LOOKUP_IPINFO_URL = "https://ipinfo.io/json"

    // Cloudflare Edge Registration Endpoint for Ephemeral WARP Tunnel
    const val WARP_API_BASE_URL = "https://api.cloudflareclient.com/v0a2158"

    // Notification IDs
    const val NOTIFICATION_CHANNEL_ID = "bp_vpn_active_guard"
    const val NOTIFICATION_ID = 1001

    // Actions
    const val ACTION_START_VPN = "com.axel.mba.bpvpn.action.START"
    const val ACTION_STOP_VPN = "com.axel.mba.bpvpn.action.STOP"
    const val ACTION_UPDATE_CONFIG = "com.axel.mba.bpvpn.action.UPDATE_CONFIG"

    // Sinkhole Synthetic IP
    const val SINKHOLE_BLOCKED_IPV4 = "0.0.0.0"
}
