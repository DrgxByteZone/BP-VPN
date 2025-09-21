package com.axel.mba.bpvpn.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.axel.mba.bpvpn.core.common.Constants
import com.axel.mba.bpvpn.core.model.AppNetworkInfo
import com.axel.mba.bpvpn.core.model.CustomBlockRule
import com.axel.mba.bpvpn.core.model.DnsLogRecord
import com.axel.mba.bpvpn.core.model.IpDetails
import com.axel.mba.bpvpn.core.model.ProtectionMode
import com.axel.mba.bpvpn.core.model.ThreatCategory
import com.axel.mba.bpvpn.core.model.TrafficMetrics
import com.axel.mba.bpvpn.core.model.VpnState
import com.axel.mba.bpvpn.data.local.blocklist.BlocklistManager
import com.axel.mba.bpvpn.data.local.db.BPSQLiteHelper
import com.axel.mba.bpvpn.data.local.db.entity.CustomRuleEntity
import com.axel.mba.bpvpn.data.local.db.entity.DnsLogEntity
import com.axel.mba.bpvpn.data.local.db.entity.FirewallRuleEntity
import com.axel.mba.bpvpn.data.local.preferences.BPPreferences
import com.axel.mba.bpvpn.data.remote.api.IpLookupApi
import com.axel.mba.bpvpn.domain.repository.AppManagerRepository
import com.axel.mba.bpvpn.domain.repository.IpDiagnosticsRepository
import com.axel.mba.bpvpn.domain.repository.SettingsRepository
import com.axel.mba.bpvpn.domain.repository.ThreatLogRepository
import com.axel.mba.bpvpn.domain.repository.VpnRepository
import com.axel.mba.bpvpn.vpn.dns.DnsSinkholeEngine
import com.axel.mba.bpvpn.vpn.firewall.PerAppFirewallManager
import com.axel.mba.bpvpn.vpn.service.BPVpnService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VpnRepositoryImpl(
    private val context: Context,
    private val dnsSinkholeEngine: DnsSinkholeEngine,
    private val wireGuardTunnelManager: com.axel.mba.bpvpn.vpn.tunnel.wireguard.WireGuardTunnelManager,
    private val bpPreferences: BPPreferences
) : VpnRepository {

    private val _compositeVpnState = MutableStateFlow<VpnState>(VpnState.Disconnected)
    override val vpnState: StateFlow<VpnState> = _compositeVpnState.asStateFlow()
    override val liveDnsLogs: Flow<DnsLogRecord> = dnsSinkholeEngine.dnsLogsFlow

    private val _selectedServerLocation = MutableStateFlow(bpPreferences.selectedServerLocation)
    override val selectedServerLocation: StateFlow<com.axel.mba.bpvpn.core.model.ServerLocation> =
        _selectedServerLocation.asStateFlow()

    init {
        // Observe WireGuard state
        CoroutineScope(Dispatchers.Main).launch {
            wireGuardTunnelManager.tunnelState.collect { wgState ->
                if (wgState == com.wireguard.android.backend.Tunnel.State.UP) {
                    val loc = _selectedServerLocation.value
                    _compositeVpnState.value = VpnState.Connected(
                        connectedSince = System.currentTimeMillis(),
                        assignedIp = "172.16.0.2",
                        maskedIp = "${loc.flagEmoji} ${loc.countryCode} (${loc.cityName})",
                        mode = ProtectionMode.FULL_STEALTH,
                        serverLocation = loc
                    )
                } else if (!BPVpnService.isRunning) {
                    _compositeVpnState.value = VpnState.Disconnected
                }
            }
        }

        // Observe Local Shield state
        CoroutineScope(Dispatchers.Main).launch {
            BPVpnService.vpnStateFlow.collect { localState ->
                if (wireGuardTunnelManager.tunnelState.value != com.wireguard.android.backend.Tunnel.State.UP) {
                    _compositeVpnState.value = localState
                }
            }
        }
    }

    override suspend fun startVpn(mode: ProtectionMode) {
        if (mode == ProtectionMode.FULL_STEALTH) {
            // Stop local service if running
            val stopLocal = Intent(context, BPVpnService::class.java).apply {
                action = Constants.ACTION_STOP_VPN
            }
            context.startService(stopLocal)

            // Start WireGuard Anycast Tunnel for 100% Real IP Masking
            _compositeVpnState.value = VpnState.Connecting
            val ok = wireGuardTunnelManager.startTunnel(_selectedServerLocation.value)
            if (!ok) {
                // Fallback to local shield if edge network temporarily unreachable
                startLocalVpn(ProtectionMode.LOCAL_SHIELD)
            }
        } else {
            wireGuardTunnelManager.stopTunnel()
            startLocalVpn(mode)
        }
    }

    override suspend fun changeServerLocation(location: com.axel.mba.bpvpn.core.model.ServerLocation) {
        _selectedServerLocation.value = location
        bpPreferences.selectedServerLocation = location
        if (wireGuardTunnelManager.tunnelState.value == com.wireguard.android.backend.Tunnel.State.UP) {
            _compositeVpnState.value = VpnState.Connecting
            wireGuardTunnelManager.stopTunnel()
            wireGuardTunnelManager.startTunnel(location)
        }
    }

    private fun startLocalVpn(mode: ProtectionMode) {
        val intent = Intent(context, BPVpnService::class.java).apply {
            action = Constants.ACTION_START_VPN
            putExtra("mode", mode.ordinal)
        }
        context.startService(intent)
    }

    override suspend fun stopVpn() {
        wireGuardTunnelManager.stopTunnel()
        val intent = Intent(context, BPVpnService::class.java).apply {
            action = Constants.ACTION_STOP_VPN
        }
        context.startService(intent)
        _compositeVpnState.value = VpnState.Disconnected
    }

    override suspend fun switchMode(mode: ProtectionMode) {
        startVpn(mode)
    }
}

class IpDiagnosticsRepositoryImpl(
    private val ipLookupApi: IpLookupApi
) : IpDiagnosticsRepository {
    override suspend fun getPublicIpDetails(): IpDetails {
        return ipLookupApi.fetchPublicIp()
    }
}

class ThreatLogRepositoryImpl(
    private val dbHelper: BPSQLiteHelper,
    private val dnsSinkholeEngine: DnsSinkholeEngine
) : ThreatLogRepository {

    override suspend fun insertLog(log: DnsLogRecord) = withContext(Dispatchers.IO) {
        val entity = DnsLogEntity(
            domain = log.domain,
            queryType = log.queryType,
            resolvedIp = log.resolvedIp,
            isBlocked = log.isBlocked,
            threatCategory = log.threatCategory?.name,
            requestingAppPackage = log.requestingAppPackage,
            timestamp = log.timestamp
        )
        dbHelper.insertDnsLog(entity)
        Unit
    }

    override suspend fun getRecentLogs(limit: Int, blockedOnly: Boolean?): List<DnsLogRecord> = withContext(Dispatchers.IO) {
        val entities = dbHelper.getRecentDnsLogs(limit, blockedOnly)
        entities.map { entity ->
            DnsLogRecord(
                id = entity.id,
                domain = entity.domain,
                queryType = entity.queryType,
                resolvedIp = entity.resolvedIp,
                isBlocked = entity.isBlocked,
                threatCategory = entity.threatCategory?.let {
                    try { ThreatCategory.valueOf(it) } catch (e: Exception) { null }
                },
                requestingAppPackage = entity.requestingAppPackage,
                timestamp = entity.timestamp
            )
        }
    }

    override suspend fun clearLogs() = withContext(Dispatchers.IO) {
        dbHelper.clearDnsLogs()
        dnsSinkholeEngine.resetCounters()
    }

    override suspend fun getMetrics(): TrafficMetrics = withContext(Dispatchers.IO) {
        val blocked = dnsSinkholeEngine.getThreatsBlockedCount()
        val total = dnsSinkholeEngine.getTotalQueriesCount()
        // Average web ad payload ~ 150KB saved per blocked request
        val bytesSaved = blocked * 153600L
        TrafficMetrics(
            totalQueries = total,
            blockedQueries = blocked,
            estimatedBytesSaved = bytesSaved
        )
    }
}

class AppManagerRepositoryImpl(
    private val context: Context,
    private val dbHelper: BPSQLiteHelper,
    private val firewallManager: PerAppFirewallManager
) : AppManagerRepository {

    override suspend fun getInstalledApps(): List<AppNetworkInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val savedRules = dbHelper.getAllFirewallRules().associateBy { it.packageName }

        val list = mutableListOf<AppNetworkInfo>()
        for (app in installed) {
            // Exclude our own package from being firewall blocked
            if (app.packageName == context.packageName) continue

            val rule = savedRules[app.packageName]
            val isSystem = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            val appName = pm.getApplicationLabel(app).toString()
            val icon = try { pm.getApplicationIcon(app) } catch (e: Exception) { null }

            list.add(
                AppNetworkInfo(
                    packageName = app.packageName,
                    appName = appName,
                    uid = app.uid,
                    icon = icon,
                    isInternetBlocked = rule?.isBlocked ?: false,
                    isBypassed = rule?.isBypassed ?: false,
                    isSystemApp = isSystem
                )
            )
        }
        list.sortedBy { it.appName.lowercase() }
    }

    override suspend fun setAppInternetBlocked(packageName: String, isBlocked: Boolean) = withContext(Dispatchers.IO) {
        firewallManager.setAppBlocked(packageName, isBlocked)
        dbHelper.saveFirewallRule(
            FirewallRuleEntity(
                packageName = packageName,
                isBlocked = isBlocked,
                isBypassed = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun setAppBypassed(packageName: String, isBypassed: Boolean) = withContext(Dispatchers.IO) {
        firewallManager.setAppBypassed(packageName, isBypassed)
        dbHelper.saveFirewallRule(
            FirewallRuleEntity(
                packageName = packageName,
                isBlocked = false,
                isBypassed = isBypassed,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}

class SettingsRepositoryImpl(
    private val prefs: BPPreferences,
    private val dbHelper: BPSQLiteHelper,
    private val blocklistManager: BlocklistManager
) : SettingsRepository {

    override fun getProtectionMode(): ProtectionMode = prefs.protectionMode

    override fun setProtectionMode(mode: ProtectionMode) {
        prefs.protectionMode = mode
    }

    override fun getSelectedServerLocation(): com.axel.mba.bpvpn.core.model.ServerLocation =
        prefs.selectedServerLocation

    override fun setSelectedServerLocation(location: com.axel.mba.bpvpn.core.model.ServerLocation) {
        prefs.selectedServerLocation = location
    }

    override suspend fun getCustomRules(): List<CustomBlockRule> = withContext(Dispatchers.IO) {
        dbHelper.getAllCustomRules().map {
            CustomBlockRule(id = it.id, domain = it.domain, isBlocked = it.isBlocked, createdAt = it.createdAt)
        }
    }

    override suspend fun addCustomRule(domain: String, isBlocked: Boolean) = withContext(Dispatchers.IO) {
        dbHelper.saveCustomRule(
            CustomRuleEntity(domain = domain, isBlocked = isBlocked, createdAt = System.currentTimeMillis())
        )
        blocklistManager.addCustomRule(domain, isBlocked)
    }

    override suspend fun deleteCustomRule(domain: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteCustomRule(domain)
        blocklistManager.removeCustomRule(domain)
    }
}
