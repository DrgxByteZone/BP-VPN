package com.axel.mba.bpvpn.vpn.tun

import android.os.ParcelFileDescriptor
import com.axel.mba.bpvpn.core.network.dns.DnsForwarder
import com.axel.mba.bpvpn.core.network.dns.DnsParser
import com.axel.mba.bpvpn.core.network.dns.DnsResponseBuilder
import com.axel.mba.bpvpn.core.network.packet.PacketParser
import com.axel.mba.bpvpn.vpn.dns.DnsSinkholeEngine
import com.axel.mba.bpvpn.vpn.tunnel.TunnelManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class PacketPump(
    private val bufferPool: BufferPool,
    private val dnsSinkholeEngine: DnsSinkholeEngine,
    private val dnsForwarder: DnsForwarder,
    private val tunnelManager: TunnelManager,
    private val onTrafficStatsUpdated: (bytesIn: Long, bytesOut: Long) -> Unit
) {

    private var pumpJob: Job? = null

    fun start(pfd: ParcelFileDescriptor, scope: CoroutineScope) {
        stop()

        val inChannel = FileInputStream(pfd.fileDescriptor).channel
        val outChannel = FileOutputStream(pfd.fileDescriptor).channel

        pumpJob = scope.launch(Dispatchers.IO) {
            val buffer = ByteBuffer.allocateDirect(32768)

            while (isActive) {
                buffer.clear()
                val bytesRead = try {
                    inChannel.read(buffer)
                } catch (e: Exception) {
                    break
                }

                if (bytesRead <= 0) continue
                buffer.flip()

                onTrafficStatsUpdated(0, bytesRead.toLong())

                // Parse IP packet
                val packet = PacketParser.parse(buffer)
                if (packet != null) {
                    if (packet.isDnsQuery) {
                        // Inspect DNS Query
                        val dnsPacket = DnsParser.parse(buffer, packet.payloadOffset)
                        val domain = dnsPacket?.domainName

                        if (domain != null) {
                            val isBlocked = dnsSinkholeEngine.inspectAndRecord(domain)
                            if (isBlocked) {
                                // Synthesize instant 0.0.0.0 blocked response
                                val responseBuffer = DnsResponseBuilder.buildBlockedResponse(packet, buffer)
                                try {
                                    val bytesWritten = outChannel.write(responseBuffer)
                                    onTrafficStatsUpdated(bytesWritten.toLong(), 0)
                                } catch (e: Exception) {
                                    // Channel closed
                                }
                                continue
                            } else {
                                // CLEAN / ALLOWED QUERY (e.g. google.com, instagram.com)
                                // Forward to upstream DNS and return real IP to app asynchronously
                                dnsForwarder.forwardDnsQuery(packet, buffer, outChannel, scope)
                                continue
                            }
                        }
                    }

                    // Forward clean non-DNS traffic through active tunnel
                    tunnelManager.forwardPacket(packet, buffer, outChannel)
                }
            }
        }
    }

    fun stop() {
        pumpJob?.cancel()
        pumpJob = null
    }
}
