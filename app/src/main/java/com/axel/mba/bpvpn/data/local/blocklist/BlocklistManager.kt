package com.axel.mba.bpvpn.data.local.blocklist

import com.axel.mba.bpvpn.core.model.ThreatCategory
import com.axel.mba.bpvpn.core.network.dns.DnsTrieMatcher
import com.axel.mba.bpvpn.data.local.db.BPSQLiteHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlocklistManager(
    private val matcher: DnsTrieMatcher,
    private val dbHelper: BPSQLiteHelper
) {

    fun initializeAsync() {
        CoroutineScope(Dispatchers.IO).launch {
            loadAll()
        }
    }

    fun loadAll() {
        matcher.clear()

        // 1. Load curated default domains
        for ((category, domains) in DefaultBlocklists.ALL_CATEGORIES) {
            for (domain in domains) {
                matcher.insert(domain, category)
            }
        }

        // 2. Load custom rules from database
        val customRules = dbHelper.getAllCustomRules()
        for (rule in customRules) {
            if (rule.isBlocked) {
                matcher.insert(rule.domain, ThreatCategory.CUSTOM_RULE)
            } else {
                matcher.addWhitelist(rule.domain)
            }
        }
    }

    fun addCustomRule(domain: String, isBlocked: Boolean) {
        val cleanDomain = domain.trim().lowercase()
        if (isBlocked) {
            matcher.insert(cleanDomain, ThreatCategory.CUSTOM_RULE)
        } else {
            matcher.addWhitelist(cleanDomain)
        }
    }

    fun removeCustomRule(domain: String) {
        loadAll()
    }
}
