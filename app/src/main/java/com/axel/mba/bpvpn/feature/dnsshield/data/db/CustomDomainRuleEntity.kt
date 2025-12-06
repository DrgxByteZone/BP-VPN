package com.axel.mba.bpvpn.feature.dnsshield.data.db

/**
 * SQLite Entity for Custom Domain Filtering Rules.
 * Created by: Axel & M.B.A
 */
data class CustomDomainRuleEntity(
    val id: Long = 0,
    val domain: String,
    val action: String, // BLOCK, BYPASS
    val note: String,
    val createdAt: Long
)
