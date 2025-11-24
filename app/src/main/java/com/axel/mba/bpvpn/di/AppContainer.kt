package com.axel.mba.bpvpn.di

import android.content.Context
import com.axel.mba.bpvpn.core.common.DefaultDispatcherProvider
import com.axel.mba.bpvpn.core.common.DispatcherProvider
import com.axel.mba.bpvpn.core.network.dns.DnsTrieMatcher
import com.axel.mba.bpvpn.data.local.blocklist.BlocklistManager
import com.axel.mba.bpvpn.data.local.db.BPSQLiteHelper
import com.axel.mba.bpvpn.data.local.preferences.BPPreferences
import com.axel.mba.bpvpn.data.remote.api.IpLookupApi
import com.axel.mba.bpvpn.data.remote.client.NetworkMonitor
import com.axel.mba.bpvpn.data.repository.AppManagerRepositoryImpl
import com.axel.mba.bpvpn.data.repository.IpDiagnosticsRepositoryImpl
import com.axel.mba.bpvpn.data.repository.SettingsRepositoryImpl
import com.axel.mba.bpvpn.data.repository.ThreatLogRepositoryImpl
import com.axel.mba.bpvpn.data.repository.VpnRepositoryImpl
import com.axel.mba.bpvpn.domain.repository.AppManagerRepository
import com.axel.mba.bpvpn.domain.repository.IpDiagnosticsRepository
import com.axel.mba.bpvpn.domain.repository.SettingsRepository
import com.axel.mba.bpvpn.domain.repository.ThreatLogRepository
import com.axel.mba.bpvpn.domain.repository.VpnRepository
import com.axel.mba.bpvpn.domain.usecase.AddCustomBlockRuleUseCase
import com.axel.mba.bpvpn.domain.usecase.ChangeProtectionModeUseCase
import com.axel.mba.bpvpn.domain.usecase.FetchRealPublicIpUseCase
import com.axel.mba.bpvpn.domain.usecase.GetInstalledAppsUseCase
import com.axel.mba.bpvpn.domain.usecase.GetLiveDnsLogsUseCase
import com.axel.mba.bpvpn.domain.usecase.GetThreatStatsUseCase
import com.axel.mba.bpvpn.domain.usecase.GetVpnStateUseCase
import com.axel.mba.bpvpn.domain.usecase.SetAppInternetAccessUseCase
import com.axel.mba.bpvpn.domain.usecase.StartVpnUseCase
import com.axel.mba.bpvpn.domain.usecase.StopVpnUseCase
import com.axel.mba.bpvpn.vpn.dns.DnsSinkholeEngine
import com.axel.mba.bpvpn.vpn.firewall.PerAppFirewallManager

/**
 * 100% In-House Dependency Injection & Service Locator Container.
 * Created by: Axel & M.B.A
 */
class AppContainer(val context: Context) {

    val dispatchers: DispatcherProvider = DefaultDispatcherProvider()

    // Database & Preferences
    val dbHelper: BPSQLiteHelper by lazy { BPSQLiteHelper(context) }
    val preferences: BPPreferences by lazy { BPPreferences(context) }

    // Core In-Memory Engines
    val dnsTrieMatcher: DnsTrieMatcher by lazy { DnsTrieMatcher() }
    val dnsSinkholeEngine: DnsSinkholeEngine by lazy { DnsSinkholeEngine(dnsTrieMatcher) }
    val perAppFirewallManager: PerAppFirewallManager by lazy { PerAppFirewallManager() }
    val blocklistManager: BlocklistManager by lazy { BlocklistManager(dnsTrieMatcher, dbHelper) }

    // Remote APIs & Tunnel Engines
    val ipLookupApi: IpLookupApi by lazy { IpLookupApi() }
    val networkMonitor: NetworkMonitor by lazy { NetworkMonitor(context) }
    val secureStorage: com.axel.mba.bpvpn.core.security.SecureStorage by lazy {
        com.axel.mba.bpvpn.core.security.SecureStorage(context)
    }
    val warpRegistrationApi: com.axel.mba.bpvpn.data.remote.api.WarpRegistrationApi by lazy {
        com.axel.mba.bpvpn.data.remote.api.WarpRegistrationApi()
    }
    val wireGuardTunnelManager: com.axel.mba.bpvpn.vpn.tunnel.wireguard.WireGuardTunnelManager by lazy {
        com.axel.mba.bpvpn.vpn.tunnel.wireguard.WireGuardTunnelManager(context, warpRegistrationApi, secureStorage)
    }

    // Repositories
    val vpnRepository: VpnRepository by lazy {
        VpnRepositoryImpl(context, dnsSinkholeEngine, wireGuardTunnelManager, preferences)
    }
    val ipDiagnosticsRepository: IpDiagnosticsRepository by lazy { IpDiagnosticsRepositoryImpl(ipLookupApi) }
    val threatLogRepository: ThreatLogRepository by lazy { ThreatLogRepositoryImpl(dbHelper, dnsSinkholeEngine) }
    val appManagerRepository: AppManagerRepository by lazy { AppManagerRepositoryImpl(context, dbHelper, perAppFirewallManager) }
    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(preferences, dbHelper, blocklistManager) }

    // Advance Subsystems DAOs & Repositories
    val splitTunnelDao by lazy { com.axel.mba.bpvpn.feature.splittunneling.data.db.SplitTunnelDao(dbHelper) }
    val splitTunnelRepository by lazy {
        com.axel.mba.bpvpn.feature.splittunneling.data.repository.SplitTunnelRepositoryImpl(context, splitTunnelDao, preferences)
    }

    val customDomainRuleDao by lazy { com.axel.mba.bpvpn.feature.dnsshield.data.db.CustomDomainRuleDao(dbHelper) }
    val dnsShieldRepository by lazy {
        com.axel.mba.bpvpn.feature.dnsshield.data.repository.DnsShieldRepositoryImpl(preferences, customDomainRuleDao)
    }

    val securityAuditDao by lazy { com.axel.mba.bpvpn.feature.securityaudit.data.db.SecurityAuditDao(dbHelper) }
    val securityAuditRepository by lazy {
        com.axel.mba.bpvpn.feature.securityaudit.data.repository.SecurityAuditRepositoryImpl(securityAuditDao)
    }

    val watchdogPreferences by lazy { com.axel.mba.bpvpn.feature.watchdog.data.preferences.WatchdogPreferences(preferences) }
    val watchdogRepository by lazy {
        com.axel.mba.bpvpn.feature.watchdog.data.repository.WatchdogRepositoryImpl(
            watchdogPreferences,
            com.axel.mba.bpvpn.feature.watchdog.engine.NetworkStateObserver(context),
            com.axel.mba.bpvpn.feature.watchdog.engine.KillSwitchManager(),
            com.axel.mba.bpvpn.feature.watchdog.engine.BatteryOptimizationAdvisor(context)
        )
    }

    val obfuscationPreferences by lazy { com.axel.mba.bpvpn.feature.obfuscation.data.preferences.ObfuscationPreferences(preferences) }
    val obfuscationRepository by lazy {
        com.axel.mba.bpvpn.feature.obfuscation.data.repository.ObfuscationRepositoryImpl(obfuscationPreferences)
    }

    // Use Cases
    val startVpnUseCase by lazy { StartVpnUseCase(vpnRepository) }
    val stopVpnUseCase by lazy { StopVpnUseCase(vpnRepository) }
    val getVpnStateUseCase by lazy { GetVpnStateUseCase(vpnRepository) }
    val fetchRealPublicIpUseCase by lazy { FetchRealPublicIpUseCase(ipDiagnosticsRepository) }
    val getInstalledAppsUseCase by lazy { GetInstalledAppsUseCase(appManagerRepository) }
    val setAppInternetAccessUseCase by lazy { SetAppInternetAccessUseCase(appManagerRepository) }
    val getLiveDnsLogsUseCase by lazy { GetLiveDnsLogsUseCase(vpnRepository) }
    val getThreatStatsUseCase by lazy { GetThreatStatsUseCase(threatLogRepository) }
    val addCustomBlockRuleUseCase by lazy { AddCustomBlockRuleUseCase(settingsRepository) }
    val changeProtectionModeUseCase by lazy { ChangeProtectionModeUseCase(settingsRepository, vpnRepository) }
    val changeServerLocationUseCase by lazy { com.axel.mba.bpvpn.domain.usecase.ChangeServerLocationUseCase(settingsRepository, vpnRepository) }
    val getSelectedServerLocationUseCase by lazy { com.axel.mba.bpvpn.domain.usecase.GetSelectedServerLocationUseCase(settingsRepository) }
}
