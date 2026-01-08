package com.axel.mba.bpvpn.feature.watchdog.engine

import android.content.Context
import android.os.Build
import android.os.PowerManager

/**
 * Checks system battery optimizations and Doze whitelist.
 * Created by: Axel & M.B.A
 */
class BatteryOptimizationAdvisor(private val context: Context) {

    fun isIgnoringBatteryOptimizations(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            return pm?.isIgnoringBatteryOptimizations(context.packageName) ?: true
        }
        return true
    }
}
