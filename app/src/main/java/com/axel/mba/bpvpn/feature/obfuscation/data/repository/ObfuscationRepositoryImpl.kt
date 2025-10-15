package com.axel.mba.bpvpn.feature.obfuscation.data.repository

import com.axel.mba.bpvpn.feature.obfuscation.data.preferences.ObfuscationPreferences
import com.axel.mba.bpvpn.feature.obfuscation.domain.repository.ObfuscationRepository
import com.axel.mba.bpvpn.feature.obfuscation.engine.MtuOptimizer
import com.axel.mba.bpvpn.feature.obfuscation.model.MssClampingConfig
import com.axel.mba.bpvpn.feature.obfuscation.model.ObfuscationLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementation of ObfuscationRepository.
 * Created by: Axel & M.B.A
 */
class ObfuscationRepositoryImpl(
    private val preferences: ObfuscationPreferences
) : ObfuscationRepository {

    private val levelFlow = MutableStateFlow(preferences.getObfuscationLevel())

    override fun getObfuscationLevel(): ObfuscationLevel = preferences.getObfuscationLevel()

    override fun setObfuscationLevel(level: ObfuscationLevel) {
        preferences.setObfuscationLevel(level)
        levelFlow.value = level
    }

    override fun observeObfuscationLevel(): Flow<ObfuscationLevel> = levelFlow.asStateFlow()

    override fun getMssConfig(isCellular: Boolean): MssClampingConfig {
        return MtuOptimizer.calculateOptimalConfig(isCellular)
    }
}
