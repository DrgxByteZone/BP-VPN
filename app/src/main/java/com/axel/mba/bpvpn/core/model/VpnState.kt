package com.axel.mba.bpvpn.core.model

sealed class VpnState {
    object Disconnected : VpnState()
    object Connecting : VpnState()
    data class Connected(
        val connectedSince: Long,
        val assignedIp: String,
        val maskedIp: String?,
        val mode: ProtectionMode,
        val serverLocation: ServerLocation = ServerLocation.AUTO
    ) : VpnState()
    data class Error(val message: String, val throwable: Throwable? = null) : VpnState()

    val isConnected: Boolean get() = this is Connected
    val isConnecting: Boolean get() = this is Connecting
    val isDisconnected: Boolean get() = this is Disconnected
}
