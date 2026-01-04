package com.axel.mba.bpvpn.feature.watchdog.data.preferences

import com.axel.mba.bpvpn.data.local.preferences.BPPreferences
import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy
import com.axel.mba.bpvpn.feature.watchdog.model.ReconnectStrategy

/**
 * Preferences manager for Kill Switch and Watchdog settings.
 * Created by: Axel & M.B.A
 */
class WatchdogPreferences(private val preferences: BPPreferences) {

    fun getKillSwitchPolicy(): KillSwitchPolicy {
        val raw = preferences.getString(KEY_KILL_SWITCH, KillSwitchPolicy.DISABLED.name)
        return try {
            KillSwitchPolicy.valueOf(raw)
        } catch (_: Exception) {
            KillSwitchPolicy.DISABLED
        }
    }

    fun setKillSwitchPolicy(policy: KillSwitchPolicy) {
        preferences.putString(KEY_KILL_SWITCH, policy.name)
    }

    fun isAutoReconnectEnabled(): Boolean {
        return preferences.getBoolean(KEY_AUTO_RECONNECT, true)
    }

    fun setAutoReconnectEnabled(enabled: Boolean) {
        preferences.putBoolean(KEY_AUTO_RECONNECT, enabled)
    }

    fun getReconnectStrategy(): ReconnectStrategy {
        val raw = preferences.getString(KEY_RECONNECT_STRATEGY, ReconnectStrategy.EXPONENTIAL_BACKOFF.name)
        return try {
            ReconnectStrategy.valueOf(raw)
        } catch (_: Exception) {
            ReconnectStrategy.EXPONENTIAL_BACKOFF
        }
    }

    fun setReconnectStrategy(strategy: ReconnectStrategy) {
        preferences.putString(KEY_RECONNECT_STRATEGY, strategy.name)
    }

    companion object {
        private const val KEY_KILL_SWITCH = "pref_kill_switch_policy"
        private const val KEY_AUTO_RECONNECT = "pref_auto_reconnect_enabled"
        private const val KEY_RECONNECT_STRATEGY = "pref_reconnect_strategy"
    }
}
