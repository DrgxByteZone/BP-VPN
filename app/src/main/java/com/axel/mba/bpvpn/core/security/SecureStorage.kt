package com.axel.mba.bpvpn.core.security

import android.content.Context
import android.content.SharedPreferences

/**
 * Secure persistent key-value storage for tunnel credentials.
 */
class SecureStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("bp_vpn_secure_vault", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String, default: String? = null): String? {
        return prefs.getString(key, default)
    }

    fun saveBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
