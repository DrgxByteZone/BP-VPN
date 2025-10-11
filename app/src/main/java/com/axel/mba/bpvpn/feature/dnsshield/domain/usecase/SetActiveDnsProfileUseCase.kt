package com.axel.mba.bpvpn.feature.dnsshield.domain.usecase

import com.axel.mba.bpvpn.feature.dnsshield.domain.repository.DnsShieldRepository
import com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile

/**
 * UseCase to change active DoH profile.
 * Created by: Axel & M.B.A
 */
class SetActiveDnsProfileUseCase(private val repository: DnsShieldRepository) {
    operator fun invoke(profile: DnsProfile) {
        repository.setActiveProfile(profile)
    }
}
