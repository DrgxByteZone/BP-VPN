package com.axel.mba.bpvpn.vpn.service

sealed class VpnAction {
    object Start : VpnAction()
    object Stop : VpnAction()
    data class SwitchMode(val mode: com.axel.mba.bpvpn.core.model.ProtectionMode) : VpnAction()
    object FlushDns : VpnAction()
    data class ReloadFirewall(val blockedUids: Set<Int>) : VpnAction()
}
