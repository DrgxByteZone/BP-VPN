package com.axel.mba.bpvpn.core.security

import com.wireguard.crypto.Key
import com.wireguard.crypto.KeyPair

/**
 * Generates mathematically valid Curve25519 / X25519 keypairs for WireGuard / Noise protocol.
 * Uses the official WireGuard Java crypto engine.
 */
object Curve25519KeyGenerator {

    fun generateKeyPair(): KeyPair {
        return KeyPair()
    }

    fun generatePrivateKeyBase64(): String {
        return KeyPair().privateKey.toBase64()
    }

    fun generatePublicKeyBase64(privateKeyBase64: String): String {
        return try {
            val privKey = Key.fromBase64(privateKeyBase64)
            KeyPair(privKey).publicKey.toBase64()
        } catch (e: Exception) {
            KeyPair().publicKey.toBase64()
        }
    }
}
