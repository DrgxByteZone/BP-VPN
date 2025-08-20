package com.axel.mba.bpvpn.core.network.dns

import com.axel.mba.bpvpn.core.model.ThreatCategory
import java.util.concurrent.ConcurrentHashMap

/**
 * High-speed in-memory Radix Trie matcher for domain blocking.
 * Stores domains in reverse label order (e.g., com -> doubleclick -> ad)
 * for sub-microsecond prefix/suffix matching.
 */
class DnsTrieMatcher {

    private class Node {
        val children = ConcurrentHashMap<String, Node>()
        var isTerminal: Boolean = false
        var category: ThreatCategory? = null
    }

    private val root = Node()
    private val exactWhitelist = ConcurrentHashMap.newKeySet<String>()

    fun insert(domain: String, category: ThreatCategory = ThreatCategory.ADVERTISING) {
        val cleanDomain = domain.trim().lowercase().removePrefix("www.")
        if (cleanDomain.isEmpty()) return

        val parts = cleanDomain.split('.').reversed()
        var current = root
        for (part in parts) {
            current = current.children.computeIfAbsent(part) { Node() }
        }
        current.isTerminal = true
        current.category = category
    }

    fun addWhitelist(domain: String) {
        exactWhitelist.add(domain.trim().lowercase())
    }

    fun removeWhitelist(domain: String) {
        exactWhitelist.remove(domain.trim().lowercase())
    }

    /**
     * Checks if a domain matches any blocked rule or wildcard.
     * Returns the matched [ThreatCategory] or null if allowed.
     */
    fun matches(domain: String): ThreatCategory? {
        val cleanDomain = domain.trim().lowercase()
        if (exactWhitelist.contains(cleanDomain)) return null

        val parts = cleanDomain.split('.').reversed()
        var current = root

        for (part in parts) {
            val next = current.children[part] ?: return null
            current = next
            if (current.isTerminal) {
                return current.category ?: ThreatCategory.ADVERTISING
            }
        }
        return if (current.isTerminal) current.category else null
    }

    fun clear() {
        root.children.clear()
        exactWhitelist.clear()
    }
}
