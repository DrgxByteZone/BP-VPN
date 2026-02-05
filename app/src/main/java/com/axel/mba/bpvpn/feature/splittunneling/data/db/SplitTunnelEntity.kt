package com.axel.mba.bpvpn.feature.splittunneling.data.db

/**
 * Database representation of a Split Tunneling entry.
 * Created by: Axel & M.B.A
 */
data class SplitTunnelEntity(
    val packageName: String,
    val isBypassed: Boolean,
    val categoryName: String,
    val updatedAt: Long
)
