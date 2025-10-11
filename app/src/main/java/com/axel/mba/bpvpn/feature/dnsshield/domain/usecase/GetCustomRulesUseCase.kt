package com.axel.mba.bpvpn.feature.dnsshield.domain.usecase

import com.axel.mba.bpvpn.feature.dnsshield.domain.repository.DnsShieldRepository
import com.axel.mba.bpvpn.feature.dnsshield.model.CustomDomainRule

/**
 * UseCase to list user custom domain rules.
 * Created by: Axel & M.B.A
 */
class GetCustomRulesUseCase(private val repository: DnsShieldRepository) {
    suspend operator fun invoke(): List<CustomDomainRule> {
        return repository.getCustomRules()
    }
}
