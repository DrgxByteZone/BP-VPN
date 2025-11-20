package com.axel.mba.bpvpn.feature.watchdog.domain.usecase

import com.axel.mba.bpvpn.feature.watchdog.domain.repository.WatchdogRepository
import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy

/**
 * UseCase to configure Kill Switch policy.
 * Created by: Axel & M.B.A
 */
class SetKillSwitchEnabledUseCase(private val repository: WatchdogRepository) {
    operator fun invoke(policy: KillSwitchPolicy) {
        repository.setKillSwitchPolicy(policy)
    }
}
