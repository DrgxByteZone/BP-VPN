package com.axel.mba.bpvpn.feature.watchdog.domain.usecase

import com.axel.mba.bpvpn.feature.watchdog.domain.repository.WatchdogRepository
import com.axel.mba.bpvpn.feature.watchdog.model.ReconnectStrategy

/**
 * UseCase to configure auto reconnection.
 * Created by: Axel & M.B.A
 */
class SetAutoReconnectEnabledUseCase(private val repository: WatchdogRepository) {
    operator fun invoke(enabled: Boolean, strategy: ReconnectStrategy = ReconnectStrategy.EXPONENTIAL_BACKOFF) {
        repository.setAutoReconnectEnabled(enabled)
        repository.setReconnectStrategy(strategy)
    }
}
