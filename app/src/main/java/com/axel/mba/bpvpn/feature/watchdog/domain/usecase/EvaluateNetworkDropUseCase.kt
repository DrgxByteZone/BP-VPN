package com.axel.mba.bpvpn.feature.watchdog.domain.usecase

import com.axel.mba.bpvpn.feature.watchdog.domain.repository.WatchdogRepository
import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy
import com.axel.mba.bpvpn.feature.watchdog.model.WatchdogState

/**
 * UseCase to evaluate network drop events against the current watchdog policy.
 * Created by: Axel & M.B.A
 */
class EvaluateNetworkDropUseCase(private val repository: WatchdogRepository) {
    operator fun invoke(isExpectedDisconnect: Boolean): WatchdogState {
        val policy = repository.getKillSwitchPolicy()
        return when (policy) {
            KillSwitchPolicy.DISABLED -> WatchdogState.STANDBY
            KillSwitchPolicy.ON_UNEXPECTED_DROP -> {
                if (!isExpectedDisconnect) WatchdogState.TRIGGERED_KILL_SWITCH else WatchdogState.STANDBY
            }
            KillSwitchPolicy.STRICT_LOCKDOWN -> WatchdogState.TRIGGERED_KILL_SWITCH
        }
    }
}
