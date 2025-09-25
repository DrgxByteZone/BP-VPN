package com.axel.mba.bpvpn.domain.repository

import com.axel.mba.bpvpn.core.model.AppNetworkInfo
import com.axel.mba.bpvpn.core.model.CustomBlockRule
import com.axel.mba.bpvpn.core.model.DnsLogRecord
import com.axel.mba.bpvpn.core.model.IpDetails
import com.axel.mba.bpvpn.core.model.ProtectionMode
import com.axel.mba.bpvpn.core.model.TrafficMetrics
import com.axel.mba.bpvpn.core.model.VpnState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface VpnRepository {
    val vpnState: StateFlow<VpnState>
    val liveDnsLogs: Flow<DnsLogRecord>
    val selectedServerLocation: StateFlow<com.axel.mba.bpvpn.core.model.ServerLocation>
    suspend fun startVpn(mode: ProtectionMode)
    suspend fun stopVpn()
    suspend fun switchMode(mode: ProtectionMode)
    suspend fun changeServerLocation(location: com.axel.mba.bpvpn.core.model.ServerLocation)
}

interface IpDiagnosticsRepository {
    suspend fun getPublicIpDetails(): IpDetails
}

interface ThreatLogRepository {
    suspend fun insertLog(log: DnsLogRecord)
    suspend fun getRecentLogs(limit: Int = 100, blockedOnly: Boolean? = null): List<DnsLogRecord>
    suspend fun clearLogs()
    suspend fun getMetrics(): TrafficMetrics
}

interface AppManagerRepository {
    suspend fun getInstalledApps(): List<AppNetworkInfo>
    suspend fun setAppInternetBlocked(packageName: String, isBlocked: Boolean)
    suspend fun setAppBypassed(packageName: String, isBypassed: Boolean)
}

interface SettingsRepository {
    fun getProtectionMode(): ProtectionMode
    fun setProtectionMode(mode: ProtectionMode)
    fun getSelectedServerLocation(): com.axel.mba.bpvpn.core.model.ServerLocation
    fun setSelectedServerLocation(location: com.axel.mba.bpvpn.core.model.ServerLocation)
    suspend fun getCustomRules(): List<CustomBlockRule>
    suspend fun addCustomRule(domain: String, isBlocked: Boolean)
    suspend fun deleteCustomRule(domain: String)
}
