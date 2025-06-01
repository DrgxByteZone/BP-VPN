package com.axel.mba.bpvpn.core.model

import android.graphics.drawable.Drawable

data class AppNetworkInfo(
    val packageName: String,
    val appName: String,
    val uid: Int,
    val icon: Drawable? = null,
    val isInternetBlocked: Boolean = false,
    val isBypassed: Boolean = false,
    val isSystemApp: Boolean = false
)
