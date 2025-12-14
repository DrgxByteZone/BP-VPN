package com.axel.mba.bpvpn.feature.dnsshield.model

/**
 * User-defined custom DNS filtering rule.
 * Created by: Axel & M.B.A
 */
enum class DomainRuleAction {
    BLOCK,
    BYPASS_UNFILTERED
}

data class CustomDomainRule(
    val id: Long = 0,
    val domain: String,
    val action: DomainRuleAction,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
