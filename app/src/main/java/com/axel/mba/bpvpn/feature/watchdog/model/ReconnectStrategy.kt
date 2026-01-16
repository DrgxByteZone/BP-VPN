package com.axel.mba.bpvpn.feature.watchdog.model

/**
 * Strategy for automatic VPN reconnection.
 * Created by: Axel & M.B.A
 */
enum class ReconnectStrategy(val title: String, val maxRetries: Int) {
    IMMEDIATE("Segera (Instan)", 3),
    EXPONENTIAL_BACKOFF("Cerdas (Exponential Backoff)", 5),
    PERSISTENT("Pantang Menyerah (Tak Terbatas)", 999)
}
