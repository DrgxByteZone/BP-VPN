package com.axel.mba.bpvpn.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axel.mba.bpvpn.core.common.formatDataSize
import com.axel.mba.bpvpn.core.model.AppNetworkInfo
import com.axel.mba.bpvpn.core.model.DnsLogRecord
import com.axel.mba.bpvpn.core.model.IpDetails
import com.axel.mba.bpvpn.core.model.ProtectionMode
import com.axel.mba.bpvpn.core.model.TrafficMetrics
import com.axel.mba.bpvpn.core.model.VpnState
import com.axel.mba.bpvpn.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val container: AppContainer) : ViewModel() {

    val vpnState: StateFlow<VpnState> = container.getVpnStateUseCase()
    val liveDnsLogs = container.getLiveDnsLogsUseCase()

    private val _publicIpDetails = MutableStateFlow<IpDetails?>(null)
    val publicIpDetails: StateFlow<IpDetails?> = _publicIpDetails.asStateFlow()

    private val _installedApps = MutableStateFlow<List<AppNetworkInfo>>(emptyList())
    val installedApps: StateFlow<List<AppNetworkInfo>> = _installedApps.asStateFlow()

    private val _recentLogs = MutableStateFlow<List<DnsLogRecord>>(emptyList())
    val recentLogs: StateFlow<List<DnsLogRecord>> = _recentLogs.asStateFlow()

    private val _trafficMetrics = MutableStateFlow(TrafficMetrics())
    val trafficMetrics: StateFlow<TrafficMetrics> = _trafficMetrics.asStateFlow()

    private val _currentMode = MutableStateFlow(container.preferences.protectionMode)
    val currentMode: StateFlow<ProtectionMode> = _currentMode.asStateFlow()

    private val _selectedLocation = MutableStateFlow(container.preferences.selectedServerLocation)
    val selectedLocation: StateFlow<com.axel.mba.bpvpn.core.model.ServerLocation> = _selectedLocation.asStateFlow()

    init {
        refreshPublicIp()
        loadInstalledApps()
        loadRecentLogs()
        refreshMetrics()

        // Collect live logs and update metrics/state in real time
        viewModelScope.launch {
            liveDnsLogs.collect { log ->
                container.threatLogRepository.insertLog(log)
                refreshMetrics()
            }
        }
    }

    fun toggleVpn() {
        viewModelScope.launch {
            if (vpnState.value.isConnected) {
                container.stopVpnUseCase()
                refreshPublicIp()
            } else {
                container.startVpnUseCase(_currentMode.value)
                // Small delay to allow tunnel establishment then refresh IP
                kotlinx.coroutines.delay(1500)
                refreshPublicIp()
            }
        }
    }

    fun switchMode() {
        val newMode = if (_currentMode.value == ProtectionMode.FULL_STEALTH) {
            ProtectionMode.LOCAL_SHIELD
        } else {
            ProtectionMode.FULL_STEALTH
        }
        _currentMode.value = newMode
        viewModelScope.launch {
            container.changeProtectionModeUseCase(newMode, vpnState.value.isConnected)
            refreshPublicIp()
        }
    }

    fun selectServerLocation(location: com.axel.mba.bpvpn.core.model.ServerLocation) {
        _selectedLocation.value = location
        viewModelScope.launch {
            container.changeServerLocationUseCase(location)
            kotlinx.coroutines.delay(1500)
            refreshPublicIp()
        }
    }

    fun refreshPublicIp() {
        viewModelScope.launch {
            val details = container.fetchRealPublicIpUseCase()
            _publicIpDetails.value = details
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            val apps = container.getInstalledAppsUseCase()
            _installedApps.value = apps
        }
    }

    fun setAppBlocked(app: AppNetworkInfo, isBlocked: Boolean) {
        viewModelScope.launch {
            container.setAppInternetAccessUseCase(app.packageName, isBlocked)
            loadInstalledApps()
        }
    }

    fun loadRecentLogs(blockedOnly: Boolean? = null) {
        viewModelScope.launch {
            val logs = container.threatLogRepository.getRecentLogs(100, blockedOnly)
            _recentLogs.value = logs
        }
    }

    fun clearLogs() {
        viewModelScope.launch {
            container.threatLogRepository.clearLogs()
            _recentLogs.value = emptyList()
            refreshMetrics()
        }
    }

    fun refreshMetrics() {
        viewModelScope.launch {
            val metrics = container.getThreatStatsUseCase()
            _trafficMetrics.value = metrics
        }
    }
}
