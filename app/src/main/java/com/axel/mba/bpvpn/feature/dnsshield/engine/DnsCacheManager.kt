package com.axel.mba.bpvpn.feature.dnsshield.engine

import com.axel.mba.bpvpn.feature.dnsshield.model.DnsCacheEntry
import java.util.Collections
import java.util.LinkedHashMap

/**
 * Thread-safe LRU in-memory DNS cache with TTL expiration.
 * Caps RAM usage to max 2048 entries to protect low-spec 3.7GB systems.
 * Created by: Axel & M.B.A
 */
class DnsCacheManager(private val maxEntries: Int = 2048) {

    private val cache = Collections.synchronizedMap(
        object : LinkedHashMap<String, DnsCacheEntry>(128, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, DnsCacheEntry>?): Boolean {
                return size > maxEntries
            }
        }
    )

    fun get(domain: String): List<String>? {
        val entry = cache[domain.lowercase()] ?: return null
        if (entry.isExpired) {
            cache.remove(domain.lowercase())
            return null
        }
        return entry.resolvedIps
    }

    fun put(domain: String, ips: List<String>, ttlSeconds: Long = 300L) {
        if (ips.isEmpty()) return
        val sanitizedTtl = ttlSeconds.coerceIn(10L, 86400L)
        cache[domain.lowercase()] = DnsCacheEntry(
            domain = domain.lowercase(),
            resolvedIps = ips,
            ttlSeconds = sanitizedTtl,
            cachedAt = System.currentTimeMillis()
        )
    }

    fun evict(domain: String) {
        cache.remove(domain.lowercase())
    }

    fun clear() {
        cache.clear()
    }

    fun size(): Int = cache.size
}
