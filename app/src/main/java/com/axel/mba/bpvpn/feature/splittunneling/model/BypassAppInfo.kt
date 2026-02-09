package com.axel.mba.bpvpn.feature.splittunneling.model

import android.graphics.drawable.Drawable

/**
 * Representation of an application configured for Split Tunneling.
 * Created by: Axel & M.B.A
 */
data class BypassAppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val category: AppCategory = AppCategory.OTHER,
    val isBypassed: Boolean = false,
    val isSystemApp: Boolean = false
)
