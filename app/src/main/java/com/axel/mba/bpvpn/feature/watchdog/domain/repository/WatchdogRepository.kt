package com.axel.mba.bpvpn.feature.watchdog.domain.repository

import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy
import com.axel.mba.bpvpn.feature.watchdog.model.NetworkType
import com.axel.mba.bpvpn.feature.watchdog.model.ReconnectStrategy
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Watchdog and Kill Switch features.
 * Created by: Axel & M.B.A
 */
interface WatchdogRepository {
    fun getKillSwitchPolicy(): KillSwitchPolicy
    fun setKillSwitchPolicy(policy: KillSwitchPolicy)
    fun isAutoReconnectEnabled(): Boolean
    fun setAutoReconnectEnabled(enabled: Boolean)
    fun getReconnectStrategy(): ReconnectStrategy
    fun setReconnectStrategy(strategy: ReconnectStrategy)
    fun observeNetworkType(): Flow<NetworkType>
    fun observeKillSwitchLockdown(): Flow<Boolean>
    fun isBatteryOptimizationIgnored(): Boolean
}
