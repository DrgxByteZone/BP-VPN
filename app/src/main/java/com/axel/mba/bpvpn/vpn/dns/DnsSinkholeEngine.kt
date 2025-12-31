package com.axel.mba.bpvpn.vpn.dns

import com.axel.mba.bpvpn.core.model.DnsLogRecord
import com.axel.mba.bpvpn.core.model.ThreatCategory
import com.axel.mba.bpvpn.core.network.dns.DnsTrieMatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DnsSinkholeEngine(
    private val matcher: DnsTrieMatcher
) {

    private val _dnsLogsFlow = MutableSharedFlow<DnsLogRecord>(extraBufferCapacity = 100)
    val dnsLogsFlow: SharedFlow<DnsLogRecord> = _dnsLogsFlow.asSharedFlow()

    private var threatsBlockedCount = 0L
    private var totalQueriesCount = 0L

    /**
     * Inspects a requested domain.
     * Returns true if the domain is blocked (ad/tracker/malware).
     */
    fun inspectAndRecord(domain: String, appPackage: String? = null): Boolean {
        totalQueriesCount++
        val threatCategory = matcher.matches(domain)
        val isBlocked = threatCategory != null

        if (isBlocked) {
            threatsBlockedCount++
        }

        val logRecord = DnsLogRecord(
            domain = domain,
            resolvedIp = if (isBlocked) "0.0.0.0" else "Passed",
            isBlocked = isBlocked,
            threatCategory = threatCategory,
            requestingAppPackage = appPackage
        )

        _dnsLogsFlow.tryEmit(logRecord)
        return isBlocked
    }

    fun getThreatsBlockedCount(): Long = threatsBlockedCount
    fun getTotalQueriesCount(): Long = totalQueriesCount

    fun resetCounters() {
        threatsBlockedCount = 0L
        totalQueriesCount = 0L
    }
}
