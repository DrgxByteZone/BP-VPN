package com.axel.mba.bpvpn.feature.dnsshield.domain.repository

import com.axel.mba.bpvpn.feature.dnsshield.model.CustomDomainRule
import com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing DoH profiles and custom filtering rules.
 * Created by: Axel & M.B.A
 */
interface DnsShieldRepository {
    fun getActiveProfile(): DnsProfile
    fun setActiveProfile(profile: DnsProfile)
    fun observeActiveProfile(): Flow<DnsProfile>
    suspend fun resolveDomain(domain: String): List<String>
    suspend fun getCustomRules(): List<CustomDomainRule>
    suspend fun addCustomRule(rule: CustomDomainRule)
    suspend fun deleteCustomRule(domain: String)
}
