package com.axel.mba.bpvpn.feature.obfuscation.domain.usecase

import com.axel.mba.bpvpn.feature.obfuscation.domain.repository.ObfuscationRepository
import com.axel.mba.bpvpn.feature.obfuscation.model.ObfuscationLevel
import kotlinx.coroutines.flow.Flow

/**
 * UseCase to get and observe DPI evasion obfuscation level.
 * Created by: Axel & M.B.A
 */
class GetObfuscationSettingsUseCase(private val repository: ObfuscationRepository) {
    operator fun invoke(): ObfuscationLevel = repository.getObfuscationLevel()
    fun observe(): Flow<ObfuscationLevel> = repository.observeObfuscationLevel()
}
