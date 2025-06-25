package com.axel.mba.bpvpn.vpn.service

import android.content.Intent
import android.net.VpnService
import com.axel.mba.bpvpn.BPApplication
import com.axel.mba.bpvpn.core.common.Constants
import com.axel.mba.bpvpn.core.model.ProtectionMode
import com.axel.mba.bpvpn.core.model.VpnState
import com.axel.mba.bpvpn.vpn.tun.BufferPool
import com.axel.mba.bpvpn.vpn.tun.PacketPump
import com.axel.mba.bpvpn.vpn.tun.TunInterfaceManager
import com.axel.mba.bpvpn.vpn.tunnel.TunnelManager
import com.axel.mba.bpvpn.vpn.tunnel.warp.WarpTunnelEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BPVpnService : VpnService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var tunManager: TunInterfaceManager
    private lateinit var bufferPool: BufferPool
    private lateinit var packetPump: PacketPump
    private lateinit var tunnelManager: TunnelManager

    private var activeMode: ProtectionMode = ProtectionMode.FULL_STEALTH
    private var totalBytesIn: Long = 0
    private var totalBytesOut: Long = 0

    override fun onCreate() {
        super.onCreate()
        val app = application as BPApplication
        val container = app.container

        tunManager = TunInterfaceManager(this)
        bufferPool = BufferPool()
        val dnsForwarder = com.axel.mba.bpvpn.core.network.dns.DnsForwarder(this)
        val warpEngine = WarpTunnelEngine(this)
        tunnelManager = TunnelManager(this, warpEngine)

        packetPump = PacketPump(
            bufferPool = bufferPool,
            dnsSinkholeEngine = container.dnsSinkholeEngine,
            dnsForwarder = dnsForwarder,
            tunnelManager = tunnelManager,
            onTrafficStatsUpdated = { bytesIn, bytesOut ->
                totalBytesIn += bytesIn
                totalBytesOut += bytesOut
            }
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_START_VPN -> {
                val modeOrdinal = intent.getIntExtra("mode", ProtectionMode.FULL_STEALTH.ordinal)
                activeMode = ProtectionMode.values()[modeOrdinal]
                startVpnInternal()
            }
            Constants.ACTION_STOP_VPN -> {
                stopVpnInternal()
            }
            Constants.ACTION_UPDATE_CONFIG -> {
                // Reload dynamic firewall or mode without interrupting session if possible
            }
            else -> {
                startVpnInternal()
            }
        }
        return START_STICKY
    }

    private fun startVpnInternal() {
        serviceScope.launch {
            _vpnStateFlow.value = VpnState.Connecting
            val notification = VpnNotificationManager.buildNotification(
                context = this@BPVpnService,
                mode = activeMode,
                blockedThreatsCount = 0
            )
            startForeground(Constants.NOTIFICATION_ID, notification)

            try {
                val app = application as BPApplication
                val firewallManager = app.container.perAppFirewallManager
                val blockedPkgs = firewallManager.getBlockedPackages()

                val pfd = tunManager.establish(disallowedApps = blockedPkgs)
                tunnelManager.setMode(activeMode)

                // Start packet processing loop
                packetPump.start(pfd, serviceScope)

                val state = VpnState.Connected(
                    connectedSince = System.currentTimeMillis(),
                    assignedIp = Constants.VPN_IPV4_ADDRESS,
                    maskedIp = if (activeMode == ProtectionMode.FULL_STEALTH) "Cloaked" else null,
                    mode = activeMode
                )
                _vpnStateFlow.value = state
                isRunning = true
            } catch (e: Exception) {
                _vpnStateFlow.value = VpnState.Error("Failed to start BP VPN: ${e.message}", e)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun stopVpnInternal() {
        packetPump.stop()
        tunManager.close()
        tunnelManager.disconnect()
        bufferPool.clear()
        _vpnStateFlow.value = VpnState.Disconnected
        isRunning = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpnInternal()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVpnInternal()
        super.onRevoke()
    }

    companion object {
        var isRunning: Boolean = false
            private set

        private val _vpnStateFlow = MutableStateFlow<VpnState>(VpnState.Disconnected)
        val vpnStateFlow: StateFlow<VpnState> = _vpnStateFlow.asStateFlow()
    }
}
