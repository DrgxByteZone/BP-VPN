package com.axel.mba.bpvpn.data.local.preferences

import android.content.Context
import android.content.SharedPreferences
import com.axel.mba.bpvpn.core.model.ProtectionMode

class BPPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bp_vpn_user_prefs", Context.MODE_PRIVATE)

    var selectedServerLocation: com.axel.mba.bpvpn.core.model.ServerLocation
        get() {
            val id = prefs.getString(KEY_SELECTED_SERVER, com.axel.mba.bpvpn.core.model.ServerLocation.AUTO.id)
            return com.axel.mba.bpvpn.core.model.ServerLocation.fromId(id)
        }
        set(value) {
            prefs.edit().putString(KEY_SELECTED_SERVER, value.id).apply()
        }

    var protectionMode: ProtectionMode
        get() {
            val ordinal = prefs.getInt(KEY_PROTECTION_MODE, ProtectionMode.FULL_STEALTH.ordinal)
            return ProtectionMode.values().getOrElse(ordinal) { ProtectionMode.FULL_STEALTH }
        }
        set(value) {
            prefs.edit().putInt(KEY_PROTECTION_MODE, value.ordinal).apply()
        }

    var isBlockAdsEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_ADS, true)
        set(value) = prefs.edit().putBoolean(KEY_BLOCK_ADS, value).apply()

    var isBlockTrackersEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_TRACKERS, true)
        set(value) = prefs.edit().putBoolean(KEY_BLOCK_TRACKERS, value).apply()

    var isBlockMalwareEnabled: Boolean
        get() = prefs.getBoolean(KEY_BLOCK_MALWARE, true)
        set(value) = prefs.edit().putBoolean(KEY_BLOCK_MALWARE, value).apply()

    var isAutoStartOnBoot: Boolean
        get() = prefs.getBoolean(KEY_AUTO_BOOT, false)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_BOOT, value).apply()

    fun getString(key: String, defaultValue: String): String =
        prefs.getString(key, defaultValue) ?: defaultValue

    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        prefs.getBoolean(key, defaultValue)

    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int =
        prefs.getInt(key, defaultValue)

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long): Long =
        prefs.getLong(key, defaultValue)

    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    companion object {
        private const val KEY_SELECTED_SERVER = "pref_selected_server_location"
        private const val KEY_PROTECTION_MODE = "pref_protection_mode"
        private const val KEY_BLOCK_ADS = "pref_block_ads"
        private const val KEY_BLOCK_TRACKERS = "pref_block_trackers"
        private const val KEY_BLOCK_MALWARE = "pref_block_malware"
        private const val KEY_AUTO_BOOT = "pref_auto_boot"
    }
}
