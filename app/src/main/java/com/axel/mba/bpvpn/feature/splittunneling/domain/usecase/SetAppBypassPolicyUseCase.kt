package com.axel.mba.bpvpn.feature.splittunneling.domain.usecase

import com.axel.mba.bpvpn.feature.splittunneling.domain.repository.SplitTunnelRepository
import com.axel.mba.bpvpn.feature.splittunneling.model.SplitTunnelPolicy

/**
 * UseCase to update app bypass status and overall policy.
 * Created by: Axel & M.B.A
 */
class SetAppBypassPolicyUseCase(private val repository: SplitTunnelRepository) {
    suspend fun setAppBypassed(packageName: String, isBypassed: Boolean) {
        repository.setAppBypassed(packageName, isBypassed)
    }

    fun setPolicy(policy: SplitTunnelPolicy) {
        repository.setSplitTunnelPolicy(policy)
    }
}
