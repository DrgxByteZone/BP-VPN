package com.axel.mba.bpvpn.data.local.db.entity

data class DnsLogEntity(
    val id: Long = 0,
    val domain: String,
    val queryType: String,
    val resolvedIp: String,
    val isBlocked: Boolean,
    val threatCategory: String?,
    val requestingAppPackage: String?,
    val timestamp: Long
)

data class ThreatEntity(
    val id: Long = 0,
    val category: String,
    val count: Long,
    val lastBlockedAt: Long
)

data class FirewallRuleEntity(
    val packageName: String,
    val isBlocked: Boolean,
    val isBypassed: Boolean,
    val updatedAt: Long
)

data class CustomRuleEntity(
    val id: Long = 0,
    val domain: String,
    val isBlocked: Boolean,
    val createdAt: Long
)
