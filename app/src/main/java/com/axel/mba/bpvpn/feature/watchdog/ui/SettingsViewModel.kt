package com.axel.mba.bpvpn.feature.watchdog.ui

import androidx.lifecycle.ViewModel
import com.axel.mba.bpvpn.feature.watchdog.domain.repository.WatchdogRepository
import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy
import com.axel.mba.bpvpn.feature.watchdog.model.ReconnectStrategy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for Advanced Settings (Watchdog & Kill Switch).
 * Created by: Axel & M.B.A
 */
class SettingsViewModel(
    private val repository: WatchdogRepository
) : ViewModel() {

    private val _killSwitchPolicy = MutableStateFlow(repository.getKillSwitchPolicy())
    val killSwitchPolicy: StateFlow<KillSwitchPolicy> = _killSwitchPolicy.asStateFlow()

    private val _isAutoReconnect = MutableStateFlow(repository.isAutoReconnectEnabled())
    val isAutoReconnect: StateFlow<Boolean> = _isAutoReconnect.asStateFlow()

    private val _reconnectStrategy = MutableStateFlow(repository.getReconnectStrategy())
    val reconnectStrategy: StateFlow<ReconnectStrategy> = _reconnectStrategy.asStateFlow()

    fun setKillSwitchPolicy(policy: KillSwitchPolicy) {
        repository.setKillSwitchPolicy(policy)
        _killSwitchPolicy.value = policy
    }

    fun setAutoReconnect(enabled: Boolean) {
        repository.setAutoReconnectEnabled(enabled)
        _isAutoReconnect.value = enabled
    }

    fun setReconnectStrategy(strategy: ReconnectStrategy) {
        repository.setReconnectStrategy(strategy)
        _reconnectStrategy.value = strategy
    }
}
