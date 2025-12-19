package com.axel.mba.bpvpn.feature.dnsshield.model

/**
 * High-speed in-memory DNS cache entry with TTL.
 * Created by: Axel & M.B.A
 */
data class DnsCacheEntry(
    val domain: String,
    val resolvedIps: List<String>,
    val ttlSeconds: Long,
    val cachedAt: Long = System.currentTimeMillis()
) {
    val isExpired: Boolean
        get() = (System.currentTimeMillis() - cachedAt) > (ttlSeconds * 1000L)
}
