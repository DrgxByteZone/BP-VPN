package com.axel.mba.bpvpn.feature.securityaudit.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axel.mba.bpvpn.feature.securityaudit.domain.repository.SecurityAuditRepository
import com.axel.mba.bpvpn.feature.securityaudit.model.SecurityAuditReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Security Auditor & Leak Sentinel screen.
 * Created by: Axel & M.B.A
 */
class SecurityAuditViewModel(
    private val repository: SecurityAuditRepository
) : ViewModel() {

    private val _report = MutableStateFlow(SecurityAuditReport())
    val report: StateFlow<SecurityAuditReport> = _report.asStateFlow()

    private val _isAuditing = MutableStateFlow(false)
    val isAuditing: StateFlow<Boolean> = _isAuditing.asStateFlow()

    fun runAudit(currentIp: String, ispName: String) {
        if (_isAuditing.value) return

        viewModelScope.launch {
            _isAuditing.value = true
            val result = repository.runFullAudit(currentIp, ispName)
            _report.value = result
            _isAuditing.value = false
        }
    }
}
