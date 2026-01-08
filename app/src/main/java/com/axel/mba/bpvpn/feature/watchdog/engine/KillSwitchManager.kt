package com.axel.mba.bpvpn.feature.watchdog.engine

import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages device-level traffic lockdown when VPN disconnects unexpectedly.
 * Created by: Axel & M.B.A
 */
class KillSwitchManager {

    private val _isLockdownActive = MutableStateFlow(false)
    val isLockdownActive: StateFlow<Boolean> = _isLockdownActive.asStateFlow()

    fun onVpnStateChanged(isConnected: Boolean, isExpectedUserDisconnect: Boolean, policy: KillSwitchPolicy) {
        if (isConnected) {
            _isLockdownActive.value = false
            return
        }

        when (policy) {
            KillSwitchPolicy.DISABLED -> {
                _isLockdownActive.value = false
            }
            KillSwitchPolicy.ON_UNEXPECTED_DROP -> {
                if (!isExpectedUserDisconnect) {
                    _isLockdownActive.value = true
                } else {
                    _isLockdownActive.value = false
                }
            }
            KillSwitchPolicy.STRICT_LOCKDOWN -> {
                _isLockdownActive.value = true
            }
        }
    }

    fun liftLockdown() {
        _isLockdownActive.value = false
    }
}
