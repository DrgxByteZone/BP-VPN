package com.axel.mba.bpvpn.feature.dnsshield.engine

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.Random

/**
 * RFC 8484 DNS wire-format serializer & deserializer.
 * Created by: Axel & M.B.A
 */
object DohPacketSerializer {

    private val random = Random()

    fun buildDnsQuery(domain: String, queryTypeA: Boolean = true): ByteArray {
        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)

        // 1. Transaction ID (16 bits)
        dos.writeShort(random.nextInt(0xFFFF))

        // 2. Flags: Standard query, recursion desired (0x0100)
        dos.writeShort(0x0100)

        // 3. Question Count = 1, Answer Count = 0, Authority = 0, Additional = 0
        dos.writeShort(1)
        dos.writeShort(0)
        dos.writeShort(0)
        dos.writeShort(0)

        // 4. QNAME (labels: length followed by chars, ending with 0)
        val parts = domain.trimEnd('.').split('.')
        for (part in parts) {
            val bytes = part.toByteArray(Charsets.US_ASCII)
            dos.writeByte(bytes.size)
            dos.write(bytes)
        }
        dos.writeByte(0) // End of domain labels

        // 5. QTYPE: 1 for A (IPv4), 28 for AAAA (IPv6)
        dos.writeShort(if (queryTypeA) 1 else 28)

        // 6. QCLASS: 1 for IN (Internet)
        dos.writeShort(1)

        dos.flush()
        return baos.toByteArray()
    }

    fun parseIpsFromResponse(responseBytes: ByteArray): List<String> {
        val ips = mutableListOf<String>()
        if (responseBytes.size < 12) return ips

        try {
            val answerCount = ((responseBytes[6].toInt() and 0xFF) shl 8) or (responseBytes[7].toInt() and 0xFF)
            if (answerCount <= 0) return ips

            // Skip Header (12 bytes)
            var offset = 12

            // Skip Question Name
            while (offset < responseBytes.size && responseBytes[offset].toInt() != 0) {
                val len = responseBytes[offset].toInt() and 0xFF
                if ((len and 0xC0) == 0xC0) {
                    offset += 2
                    break
                } else {
                    offset += 1 + len
                }
            }
            if (offset < responseBytes.size && responseBytes[offset].toInt() == 0) {
                offset += 1 // skip null byte
            }
            offset += 4 // Skip QTYPE and QCLASS

            // Parse Answers
            for (i in 0 until answerCount) {
                if (offset >= responseBytes.size) break

                // Name (Pointer or Label)
                if ((responseBytes[offset].toInt() and 0xC0) == 0xC0) {
                    offset += 2
                } else {
                    while (offset < responseBytes.size && responseBytes[offset].toInt() != 0) {
                        offset += 1 + (responseBytes[offset].toInt() and 0xFF)
                    }
                    offset += 1
                }

                if (offset + 10 > responseBytes.size) break

                val type = ((responseBytes[offset].toInt() and 0xFF) shl 8) or (responseBytes[offset + 1].toInt() and 0xFF)
                // skip class (2 bytes) + TTL (4 bytes)
                offset += 8
                val dataLen = ((responseBytes[offset].toInt() and 0xFF) shl 8) or (responseBytes[offset + 1].toInt() and 0xFF)
                offset += 2

                if (type == 1 && dataLen == 4 && offset + 4 <= responseBytes.size) { // A Record
                    val ip = "${responseBytes[offset].toInt() and 0xFF}." +
                            "${responseBytes[offset + 1].toInt() and 0xFF}." +
                            "${responseBytes[offset + 2].toInt() and 0xFF}." +
                            "${responseBytes[offset + 3].toInt() and 0xFF}"
                    ips.add(ip)
                }
                offset += dataLen
            }
        } catch (_: Exception) {
            // Graceful parse error handling
        }

        return ips
    }
}
