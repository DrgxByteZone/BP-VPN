package com.axel.mba.bpvpn.feature.obfuscation.data.preferences

import com.axel.mba.bpvpn.data.local.preferences.BPPreferences
import com.axel.mba.bpvpn.feature.obfuscation.model.ObfuscationLevel

/**
 * Preferences manager for DPI evasion and packet obfuscation settings.
 * Created by: Axel & M.B.A
 */
class ObfuscationPreferences(private val preferences: BPPreferences) {

    fun getObfuscationLevel(): ObfuscationLevel {
        val raw = preferences.getString(KEY_OBFUSCATION_LEVEL, ObfuscationLevel.DISABLED.name)
        return try {
            ObfuscationLevel.valueOf(raw)
        } catch (_: Exception) {
            ObfuscationLevel.DISABLED
        }
    }

    fun setObfuscationLevel(level: ObfuscationLevel) {
        preferences.putString(KEY_OBFUSCATION_LEVEL, level.name)
    }

    companion object {
        private const val KEY_OBFUSCATION_LEVEL = "pref_obfuscation_level"
    }
}
