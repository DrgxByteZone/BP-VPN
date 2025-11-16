package com.axel.mba.bpvpn.feature.watchdog.domain.usecase

import com.axel.mba.bpvpn.feature.watchdog.domain.repository.WatchdogRepository
import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy
import com.axel.mba.bpvpn.feature.watchdog.model.ReconnectStrategy

/**
 * UseCase to fetch watchdog configuration state.
 * Created by: Axel & M.B.A
 */
class GetWatchdogSettingsUseCase(private val repository: WatchdogRepository) {
    data class Settings(
        val killSwitchPolicy: KillSwitchPolicy,
        val isAutoReconnectEnabled: Boolean,
        val reconnectStrategy: ReconnectStrategy,
        val isBatteryOptimizedIgnored: Boolean
    )

    operator fun invoke(): Settings {
        return Settings(
            killSwitchPolicy = repository.getKillSwitchPolicy(),
            isAutoReconnectEnabled = repository.isAutoReconnectEnabled(),
            reconnectStrategy = repository.getReconnectStrategy(),
            isBatteryOptimizedIgnored = repository.isBatteryOptimizationIgnored()
        )
    }
}
