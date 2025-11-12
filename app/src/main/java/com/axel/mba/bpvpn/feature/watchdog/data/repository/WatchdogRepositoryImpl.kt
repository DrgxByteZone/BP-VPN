package com.axel.mba.bpvpn.feature.watchdog.data.repository

import com.axel.mba.bpvpn.feature.watchdog.data.preferences.WatchdogPreferences
import com.axel.mba.bpvpn.feature.watchdog.domain.repository.WatchdogRepository
import com.axel.mba.bpvpn.feature.watchdog.engine.BatteryOptimizationAdvisor
import com.axel.mba.bpvpn.feature.watchdog.engine.KillSwitchManager
import com.axel.mba.bpvpn.feature.watchdog.engine.NetworkStateObserver
import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy
import com.axel.mba.bpvpn.feature.watchdog.model.NetworkType
import com.axel.mba.bpvpn.feature.watchdog.model.ReconnectStrategy
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of WatchdogRepository.
 * Created by: Axel & M.B.A
 */
class WatchdogRepositoryImpl(
    private val preferences: WatchdogPreferences,
    private val networkObserver: NetworkStateObserver,
    private val killSwitchManager: KillSwitchManager,
    private val batteryAdvisor: BatteryOptimizationAdvisor
) : WatchdogRepository {

    override fun getKillSwitchPolicy(): KillSwitchPolicy = preferences.getKillSwitchPolicy()

    override fun setKillSwitchPolicy(policy: KillSwitchPolicy) {
        preferences.setKillSwitchPolicy(policy)
    }

    override fun isAutoReconnectEnabled(): Boolean = preferences.isAutoReconnectEnabled()

    override fun setAutoReconnectEnabled(enabled: Boolean) {
        preferences.setAutoReconnectEnabled(enabled)
    }

    override fun getReconnectStrategy(): ReconnectStrategy = preferences.getReconnectStrategy()

    override fun setReconnectStrategy(strategy: ReconnectStrategy) {
        preferences.setReconnectStrategy(strategy)
    }

    override fun observeNetworkType(): Flow<NetworkType> = networkObserver.networkType

    override fun observeKillSwitchLockdown(): Flow<Boolean> = killSwitchManager.isLockdownActive

    override fun isBatteryOptimizationIgnored(): Boolean = batteryAdvisor.isIgnoringBatteryOptimizations()
}
