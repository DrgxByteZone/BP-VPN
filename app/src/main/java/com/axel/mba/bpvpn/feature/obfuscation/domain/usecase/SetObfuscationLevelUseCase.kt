package com.axel.mba.bpvpn.feature.obfuscation.domain.usecase

import com.axel.mba.bpvpn.feature.obfuscation.domain.repository.ObfuscationRepository
import com.axel.mba.bpvpn.feature.obfuscation.model.ObfuscationLevel

/**
 * UseCase to update DPI evasion obfuscation level.
 * Created by: Axel & M.B.A
 */
class SetObfuscationLevelUseCase(private val repository: ObfuscationRepository) {
    operator fun invoke(level: ObfuscationLevel) {
        repository.setObfuscationLevel(level)
    }
}
