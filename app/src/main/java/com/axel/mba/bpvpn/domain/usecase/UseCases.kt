package com.axel.mba.bpvpn.domain.usecase

import com.axel.mba.bpvpn.core.model.AppNetworkInfo
import com.axel.mba.bpvpn.core.model.CustomBlockRule
import com.axel.mba.bpvpn.core.model.DnsLogRecord
import com.axel.mba.bpvpn.core.model.IpDetails
import com.axel.mba.bpvpn.core.model.ProtectionMode
import com.axel.mba.bpvpn.core.model.TrafficMetrics
import com.axel.mba.bpvpn.core.model.VpnState
import com.axel.mba.bpvpn.domain.repository.AppManagerRepository
import com.axel.mba.bpvpn.domain.repository.IpDiagnosticsRepository
import com.axel.mba.bpvpn.domain.repository.SettingsRepository
import com.axel.mba.bpvpn.domain.repository.ThreatLogRepository
import com.axel.mba.bpvpn.domain.repository.VpnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class StartVpnUseCase(private val vpnRepository: VpnRepository) {
    suspend operator fun invoke(mode: ProtectionMode) = vpnRepository.startVpn(mode)
}

class StopVpnUseCase(private val vpnRepository: VpnRepository) {
    suspend operator fun invoke() = vpnRepository.stopVpn()
}

class GetVpnStateUseCase(private val vpnRepository: VpnRepository) {
    operator fun invoke(): StateFlow<VpnState> = vpnRepository.vpnState
}

class FetchRealPublicIpUseCase(private val ipRepository: IpDiagnosticsRepository) {
    suspend operator fun invoke(): IpDetails = ipRepository.getPublicIpDetails()
}

class GetInstalledAppsUseCase(private val appRepository: AppManagerRepository) {
    suspend operator fun invoke(): List<AppNetworkInfo> = appRepository.getInstalledApps()
}

class SetAppInternetAccessUseCase(private val appRepository: AppManagerRepository) {
    suspend operator fun invoke(packageName: String, isBlocked: Boolean) =
        appRepository.setAppInternetBlocked(packageName, isBlocked)
}

class GetLiveDnsLogsUseCase(private val vpnRepository: VpnRepository) {
    operator fun invoke(): Flow<DnsLogRecord> = vpnRepository.liveDnsLogs
}

class GetThreatStatsUseCase(private val threatRepository: ThreatLogRepository) {
    suspend operator fun invoke(): TrafficMetrics = threatRepository.getMetrics()
}

class AddCustomBlockRuleUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(domain: String, isBlocked: Boolean) =
        settingsRepository.addCustomRule(domain, isBlocked)
}

class ChangeProtectionModeUseCase(
    private val settingsRepository: SettingsRepository,
    private val vpnRepository: VpnRepository
) {
    suspend operator fun invoke(mode: ProtectionMode, isVpnActive: Boolean) {
        settingsRepository.setProtectionMode(mode)
        if (isVpnActive) {
            vpnRepository.switchMode(mode)
        }
    }
}

class ChangeServerLocationUseCase(
    private val settingsRepository: SettingsRepository,
    private val vpnRepository: VpnRepository
) {
    suspend operator fun invoke(location: com.axel.mba.bpvpn.core.model.ServerLocation) {
        settingsRepository.setSelectedServerLocation(location)
        vpnRepository.changeServerLocation(location)
    }
}

class GetSelectedServerLocationUseCase(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): com.axel.mba.bpvpn.core.model.ServerLocation =
        settingsRepository.getSelectedServerLocation()
}

