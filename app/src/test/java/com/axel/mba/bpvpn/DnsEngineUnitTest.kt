package com.axel.mba.bpvpn

import com.axel.mba.bpvpn.core.model.ThreatCategory
import com.axel.mba.bpvpn.core.network.dns.DnsTrieMatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DnsEngineUnitTest {

    @Test
    fun testTrieMatcherBlocksAdDomains() {
        val matcher = DnsTrieMatcher()
        matcher.insert("doubleclick.net", ThreatCategory.ADVERTISING)
        matcher.insert("adservice.google.com", ThreatCategory.ADVERTISING)
        matcher.insert("telemetry.facebook.com", ThreatCategory.TRACKER)

        // Subdomain matching & exact matching
        val adMatch = matcher.matches("pagead.doubleclick.net")
        assertNotNull(adMatch)
        assertEquals(ThreatCategory.ADVERTISING, adMatch)

        val trackerMatch = matcher.matches("telemetry.facebook.com")
        assertNotNull(trackerMatch)
        assertEquals(ThreatCategory.TRACKER, trackerMatch)

        // Whitelist test
        matcher.addWhitelist("pagead.doubleclick.net")
        val whitelistedMatch = matcher.matches("pagead.doubleclick.net")
        assertNull(whitelistedMatch)

        // Allowed benign domain test
        val cleanMatch = matcher.matches("github.com")
        assertNull(cleanMatch)
    }
}
