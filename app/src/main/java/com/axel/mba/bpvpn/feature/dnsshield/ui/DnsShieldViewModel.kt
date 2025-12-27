package com.axel.mba.bpvpn.feature.dnsshield.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axel.mba.bpvpn.feature.dnsshield.domain.repository.DnsShieldRepository
import com.axel.mba.bpvpn.feature.dnsshield.model.CustomDomainRule
import com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile
import com.axel.mba.bpvpn.feature.dnsshield.model.DomainRuleAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for DoH Shield and Custom Domain rules screen.
 * Created by: Axel & M.B.A
 */
class DnsShieldViewModel(
    private val repository: DnsShieldRepository
) : ViewModel() {

    private val _activeProfile = MutableStateFlow(repository.getActiveProfile())
    val activeProfile: StateFlow<DnsProfile> = _activeProfile.asStateFlow()

    private val _customRules = MutableStateFlow<List<CustomDomainRule>>(emptyList())
    val customRules: StateFlow<List<CustomDomainRule>> = _customRules.asStateFlow()

    init {
        loadRules()
    }

    fun loadRules() {
        viewModelScope.launch {
            _customRules.value = repository.getCustomRules()
        }
    }

    fun setProfile(profile: DnsProfile) {
        repository.setActiveProfile(profile)
        _activeProfile.value = profile
    }

    fun addBlockRule(domain: String, note: String = "") {
        val cleanDomain = domain.trim().lowercase().removePrefix("https://").removePrefix("http://").trimEnd('/')
        if (cleanDomain.isBlank()) return

        viewModelScope.launch {
            repository.addCustomRule(
                CustomDomainRule(
                    domain = cleanDomain,
                    action = DomainRuleAction.BLOCK,
                    note = note
                )
            )
            loadRules()
        }
    }

    fun deleteRule(domain: String) {
        viewModelScope.launch {
            repository.deleteCustomRule(domain)
            loadRules()
        }
    }
}
