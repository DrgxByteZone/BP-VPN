package com.axel.mba.bpvpn.feature.dnsshield.domain.usecase

import com.axel.mba.bpvpn.feature.dnsshield.domain.repository.DnsShieldRepository
import com.axel.mba.bpvpn.feature.dnsshield.model.CustomDomainRule

/**
 * UseCase to add or delete a custom domain filtering rule.
 * Created by: Axel & M.B.A
 */
class AddCustomDomainRuleUseCase(private val repository: DnsShieldRepository) {
    suspend fun addRule(rule: CustomDomainRule) {
        repository.addCustomRule(rule)
    }

    suspend fun deleteRule(domain: String) {
        repository.deleteCustomRule(domain)
    }
}
