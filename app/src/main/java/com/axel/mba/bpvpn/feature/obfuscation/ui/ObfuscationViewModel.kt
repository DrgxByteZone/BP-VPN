package com.axel.mba.bpvpn.feature.obfuscation.ui

import androidx.lifecycle.ViewModel
import com.axel.mba.bpvpn.feature.obfuscation.domain.repository.ObfuscationRepository
import com.axel.mba.bpvpn.feature.obfuscation.model.ObfuscationLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for DPI Evasion & Obfuscation settings.
 * Created by: Axel & M.B.A
 */
class ObfuscationViewModel(
    private val repository: ObfuscationRepository
) : ViewModel() {

    private val _level = MutableStateFlow(repository.getObfuscationLevel())
    val level: StateFlow<ObfuscationLevel> = _level.asStateFlow()

    fun setLevel(newLevel: ObfuscationLevel) {
        repository.setObfuscationLevel(newLevel)
        _level.value = newLevel
    }
}
