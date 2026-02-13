package com.axel.mba.bpvpn.feature.splittunneling.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axel.mba.bpvpn.feature.splittunneling.domain.repository.SplitTunnelRepository
import com.axel.mba.bpvpn.feature.splittunneling.model.BypassAppInfo
import com.axel.mba.bpvpn.feature.splittunneling.model.SplitTunnelPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Split Tunneling screen.
 * Created by: Axel & M.B.A
 */
class SplitTunnelViewModel(
    private val repository: SplitTunnelRepository
) : ViewModel() {

    private val _policy = MutableStateFlow(repository.getSplitTunnelPolicy())
    val policy: StateFlow<SplitTunnelPolicy> = _policy.asStateFlow()

    private val _appList = MutableStateFlow<List<BypassAppInfo>>(emptyList())
    val appList: StateFlow<List<BypassAppInfo>> = _appList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val apps = repository.getInstalledApps()
            _appList.value = apps
            _isLoading.value = false
        }
    }

    fun setPolicy(newPolicy: SplitTunnelPolicy) {
        repository.setSplitTunnelPolicy(newPolicy)
        _policy.value = newPolicy
    }

    fun toggleAppBypass(app: BypassAppInfo) {
        viewModelScope.launch {
            val updatedState = !app.isBypassed
            repository.setAppBypassed(app.packageName, updatedState)
            _appList.value = _appList.value.map {
                if (it.packageName == app.packageName) it.copy(isBypassed = updatedState) else it
            }
        }
    }

    fun applyBankingPreset(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val count = repository.applyBankingPreset()
            val apps = repository.getInstalledApps()
            _appList.value = apps
            _isLoading.value = false
            onComplete(count)
        }
    }
}
