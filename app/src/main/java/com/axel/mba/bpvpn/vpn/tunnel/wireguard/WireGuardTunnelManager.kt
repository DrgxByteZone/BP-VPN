package com.axel.mba.bpvpn.vpn.tunnel.wireguard

import android.content.Context
import android.util.Log
import com.axel.mba.bpvpn.core.model.ServerLocation
import com.axel.mba.bpvpn.core.security.SecureStorage
import com.axel.mba.bpvpn.data.remote.api.WarpRegistrationApi
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.StringReader

/**
 * Pengelola tunnel WireGuard resmi untuk koneksi aman dan pengalihan IP.
 * Menghubungkan ke jaringan edge Cloudflare Anycast berkecepatan tinggi.
 */
class WireGuardTunnelManager(
    private val context: Context,
    private val warpRegistrationApi: WarpRegistrationApi,
    private val secureStorage: SecureStorage
) {

    companion object {
        private const val TAG = "WireGuardTunnelManager"
        private const val OLD_INVALID_ACCOUNT_ID = "dda778bb-c34c-434d-99f5-ca94dd83be3e"
    }

    private val backend = GoBackend(context)
    private val tunnel = object : Tunnel {
        override fun getName(): String = "BP-VPN-WARP"
        override fun onStateChange(state: Tunnel.State) {
            _tunnelState.value = state
        }
    }

    private val _tunnelState = MutableStateFlow(Tunnel.State.DOWN)
    val tunnelState: StateFlow<Tunnel.State> = _tunnelState.asStateFlow()

    private val _currentLocation = MutableStateFlow(ServerLocation.AUTO)
    val currentLocation: StateFlow<ServerLocation> = _currentLocation.asStateFlow()

    suspend fun startTunnel(location: ServerLocation = ServerLocation.AUTO): Boolean = withContext(Dispatchers.IO) {
        try {
            _currentLocation.value = location

            // Bersihkan kredensial lama jika tidak valid
            var accountId = secureStorage.getString("warp_account_id")
            if (accountId == OLD_INVALID_ACCOUNT_ID) {
                secureStorage.remove("warp_private_key")
                secureStorage.remove("warp_client_ip")
                secureStorage.remove("warp_account_id")
                accountId = null
            }

            var privateKey = secureStorage.getString("warp_private_key")
            var clientIp = secureStorage.getString("warp_client_ip")

            if (privateKey == null || clientIp == null || accountId == null) {
                val profile = warpRegistrationApi.registerEphemeralAccount()
                privateKey = profile.privateKey
                clientIp = profile.clientIpv4
                secureStorage.saveString("warp_private_key", privateKey)
                secureStorage.saveString("warp_client_ip", clientIp)
                secureStorage.saveString("warp_account_id", profile.accountId)
                secureStorage.saveString("warp_peer_pubkey", profile.peerPublicKey)
            }

            val peerPublicKey = location.peerPublicKey.ifBlank {
                secureStorage.getString("warp_peer_pubkey") ?: "bmXOC+F1FxEMF9dyiK2H5/1SUtzH0JuVo51h2wPfgyo="
            }
            val endpoint = "${location.endpointHost}:${location.endpointPort}"

            // Baca profil DNS aktif, aplikasi bypass (Split-Tunnel), dan tingkat obfuskasi
            val prefs = context.getSharedPreferences("bp_vpn_user_prefs", Context.MODE_PRIVATE)
            val profileId = prefs.getString("pref_active_doh_profile", com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile.CLOUDFLARE_STANDARD.id)
            val dnsProfile = com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile.fromId(profileId)
            val dnsServers = "${dnsProfile.primaryIp}, ${dnsProfile.secondaryIp}"

            val dbHelper = com.axel.mba.bpvpn.data.local.db.BPSQLiteHelper(context)
            val splitDao = com.axel.mba.bpvpn.feature.splittunneling.data.db.SplitTunnelDao(dbHelper)
            val bypassedPackages = try {
                splitDao.getBypassedPackages()
            } catch (_: Exception) {
                emptySet<String>()
            }
            val excludedAppsLine = if (bypassedPackages.isNotEmpty()) {
                "ExcludedApplications = ${bypassedPackages.joinToString(", ")}"
            } else {
                ""
            }

            val obfLevel = prefs.getString("pref_obfuscation_level", "DISABLED")
            val mtu = when (obfLevel) {
                "AGGRESSIVE" -> 1280
                "STANDARD" -> 1360
                else -> 1420
            }
            val keepalive = if (obfLevel == "AGGRESSIVE") 21 else 25

            // Konfigurasi WireGuard dinamis dengan integrasi penuh fitur keamanan BP VPN
            val configString = """
                [Interface]
                PrivateKey = $privateKey
                Address = $clientIp/32
                DNS = $dnsServers
                MTU = $mtu
                $excludedAppsLine

                [Peer]
                PublicKey = $peerPublicKey
                Endpoint = $endpoint
                AllowedIPs = 0.0.0.0/0
                PersistentKeepalive = $keepalive
            """.trimIndent()

            Log.i(TAG, "Membangun tunnel ke endpoint: $endpoint (MTU: $mtu, DNS: $dnsServers, Bypassed: ${bypassedPackages.size} apps)")
            val config = Config.parse(BufferedReader(StringReader(configString)))

            backend.setState(tunnel, Tunnel.State.UP, config)
            _tunnelState.value = Tunnel.State.UP
            true
        } catch (e: Exception) {
            Log.e(TAG, "Gagal memulai tunnel: ${e.message}", e)
            _tunnelState.value = Tunnel.State.DOWN
            false
        }
    }

    suspend fun stopTunnel() = withContext(Dispatchers.IO) {
        try {
            backend.setState(tunnel, Tunnel.State.DOWN, null)
            _tunnelState.value = Tunnel.State.DOWN
        } catch (e: Exception) {
            Log.w(TAG, "Gagal menghentikan tunnel: ${e.message}")
        }
    }

    val isConnected: Boolean get() = _tunnelState.value == Tunnel.State.UP
}
