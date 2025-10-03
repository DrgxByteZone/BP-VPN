package com.axel.mba.bpvpn.feature.dnsshield.domain.usecase

import com.axel.mba.bpvpn.feature.dnsshield.domain.repository.DnsShieldRepository
import com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile
import kotlinx.coroutines.flow.Flow

/**
 * UseCase to get and observe the active DoH profile.
 * Created by: Axel & M.B.A
 */
class GetActiveDnsProfileUseCase(private val repository: DnsShieldRepository) {
    operator fun invoke(): DnsProfile = repository.getActiveProfile()
    fun observe(): Flow<DnsProfile> = repository.observeActiveProfile()
}
