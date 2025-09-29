package com.axel.mba.bpvpn.feature.dnsshield.data.repository

import com.axel.mba.bpvpn.data.local.preferences.BPPreferences
import com.axel.mba.bpvpn.feature.dnsshield.data.db.CustomDomainRuleDao
import com.axel.mba.bpvpn.feature.dnsshield.data.db.CustomDomainRuleEntity
import com.axel.mba.bpvpn.feature.dnsshield.domain.repository.DnsShieldRepository
import com.axel.mba.bpvpn.feature.dnsshield.engine.MultiProfileDohResolver
import com.axel.mba.bpvpn.feature.dnsshield.model.CustomDomainRule
import com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile
import com.axel.mba.bpvpn.feature.dnsshield.model.DomainRuleAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Implementation of DnsShieldRepository.
 * Created by: Axel & M.B.A
 */
class DnsShieldRepositoryImpl(
    private val preferences: BPPreferences,
    private val dao: CustomDomainRuleDao,
    private val resolver: MultiProfileDohResolver = MultiProfileDohResolver()
) : DnsShieldRepository {

    private val profileFlow = MutableStateFlow(getActiveProfile())

    override fun getActiveProfile(): DnsProfile {
        val raw = preferences.getString(KEY_ACTIVE_DOH_PROFILE, DnsProfile.CLOUDFLARE_STANDARD.id)
        return DnsProfile.fromId(raw)
    }

    override fun setActiveProfile(profile: DnsProfile) {
        preferences.putString(KEY_ACTIVE_DOH_PROFILE, profile.id)
        profileFlow.value = profile
    }

    override fun observeActiveProfile(): Flow<DnsProfile> = profileFlow.asStateFlow()

    override suspend fun resolveDomain(domain: String): List<String> = withContext(Dispatchers.IO) {
        resolver.resolve(domain, getActiveProfile())
    }

    override suspend fun getCustomRules(): List<CustomDomainRule> = withContext(Dispatchers.IO) {
        dao.getAll().map {
            CustomDomainRule(
                id = it.id,
                domain = it.domain,
                action = if (it.action == DomainRuleAction.BYPASS_UNFILTERED.name) DomainRuleAction.BYPASS_UNFILTERED else DomainRuleAction.BLOCK,
                note = it.note,
                createdAt = it.createdAt
            )
        }
    }

    override suspend fun addCustomRule(rule: CustomDomainRule) = withContext(Dispatchers.IO) {
        dao.insertOrUpdate(
            CustomDomainRuleEntity(
                domain = rule.domain,
                action = rule.action.name,
                note = rule.note,
                createdAt = rule.createdAt
            )
        )
        Unit
    }

    override suspend fun deleteCustomRule(domain: String) = withContext(Dispatchers.IO) {
        dao.delete(domain)
    }

    companion object {
        private const val KEY_ACTIVE_DOH_PROFILE = "pref_active_doh_profile"
    }
}
