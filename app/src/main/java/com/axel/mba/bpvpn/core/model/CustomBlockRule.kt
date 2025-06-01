package com.axel.mba.bpvpn.core.model

data class CustomBlockRule(
    val id: Long = 0,
    val domain: String,
    val isBlocked: Boolean = true, // true for blacklist, false for whitelist
    val createdAt: Long = System.currentTimeMillis()
)
