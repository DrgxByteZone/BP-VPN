package com.axel.mba.bpvpn.vpn.tunnel.wireguard

import java.net.InetSocketAddress

data class WireGuardConfig(
    val privateKeyBase64: String,
    val publicKeyBase64: String,
    val presharedKeyBase64: String? = null,
    val clientIpv4: String,
    val clientIpv6: String? = null,
    val peerPublicKeyBase64: String,
    val endpoint: InetSocketAddress,
    val persistentKeepalive: Int = 25
)

object CryptoConstants {
    const val NOISE_CONSTRUCTION = "Noise_IKpsk2_25519_ChaChaPoly_BLAKE2s"
    const val IDENTIFIER = "WireGuard v1 zx2c4 uapi"
    const val HANDSHAKE_INITIATION_SIZE = 148
    const val HANDSHAKE_RESPONSE_SIZE = 92
    const val DATA_PACKET_HEADER_SIZE = 16
}
