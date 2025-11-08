package com.axel.mba.bpvpn.feature.splittunneling.domain.usecase

import com.axel.mba.bpvpn.feature.splittunneling.domain.repository.SplitTunnelRepository
import com.axel.mba.bpvpn.feature.splittunneling.model.SplitTunnelPolicy
import kotlinx.coroutines.flow.Flow

/**
 * UseCase to observe and retrieve current Split Tunnel policy.
 * Created by: Axel & M.B.A
 */
class GetSplitTunnelModeUseCase(private val repository: SplitTunnelRepository) {
    fun getPolicy(): SplitTunnelPolicy = repository.getSplitTunnelPolicy()
    fun observePolicy(): Flow<SplitTunnelPolicy> = repository.observePolicy()
}
