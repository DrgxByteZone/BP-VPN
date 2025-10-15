package com.axel.mba.bpvpn.feature.obfuscation.domain.repository

import com.axel.mba.bpvpn.feature.obfuscation.model.MssClampingConfig
import com.axel.mba.bpvpn.feature.obfuscation.model.ObfuscationLevel
import kotlinx.coroutines.flow.Flow

/**
 * Contract for Obfuscation & DPI Evasion settings.
 * Created by: Axel & M.B.A
 */
interface ObfuscationRepository {
    fun getObfuscationLevel(): ObfuscationLevel
    fun setObfuscationLevel(level: ObfuscationLevel)
    fun observeObfuscationLevel(): Flow<ObfuscationLevel>
    fun getMssConfig(isCellular: Boolean): MssClampingConfig
}
