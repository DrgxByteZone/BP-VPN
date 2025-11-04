package com.axel.mba.bpvpn.feature.splittunneling.domain.usecase

import com.axel.mba.bpvpn.feature.splittunneling.domain.repository.SplitTunnelRepository
import com.axel.mba.bpvpn.feature.splittunneling.model.BypassAppInfo

/**
 * UseCase to retrieve installed apps and their bypass status.
 * Created by: Axel & M.B.A
 */
class GetBypassAppsUseCase(private val repository: SplitTunnelRepository) {
    suspend operator fun invoke(): List<BypassAppInfo> {
        return repository.getInstalledApps()
    }
}
