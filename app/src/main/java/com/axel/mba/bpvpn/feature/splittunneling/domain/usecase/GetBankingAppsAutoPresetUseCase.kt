package com.axel.mba.bpvpn.feature.splittunneling.domain.usecase

import com.axel.mba.bpvpn.feature.splittunneling.domain.repository.SplitTunnelRepository

/**
 * UseCase to automatically detect and bypass all banking/financial apps.
 * Created by: Axel & M.B.A
 */
class GetBankingAppsAutoPresetUseCase(private val repository: SplitTunnelRepository) {
    suspend operator fun invoke(): Int {
        return repository.applyBankingPreset()
    }
}
