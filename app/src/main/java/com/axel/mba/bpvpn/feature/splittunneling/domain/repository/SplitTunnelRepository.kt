package com.axel.mba.bpvpn.feature.splittunneling.domain.repository

import com.axel.mba.bpvpn.feature.splittunneling.model.BypassAppInfo
import com.axel.mba.bpvpn.feature.splittunneling.model.SplitTunnelPolicy
import kotlinx.coroutines.flow.Flow

/**
 * Contract for managing Split Tunneling configurations.
 * Created by: Axel & M.B.A
 */
interface SplitTunnelRepository {
    fun getSplitTunnelPolicy(): SplitTunnelPolicy
    fun setSplitTunnelPolicy(policy: SplitTunnelPolicy)
    suspend fun getInstalledApps(): List<BypassAppInfo>
    suspend fun setAppBypassed(packageName: String, isBypassed: Boolean)
    suspend fun applyBankingPreset(): Int
    suspend fun getBypassedPackages(): Set<String>
    fun observePolicy(): Flow<SplitTunnelPolicy>
}
