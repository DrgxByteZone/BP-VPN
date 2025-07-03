package com.axel.mba.bpvpn.vpn.tun

import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * High-performance DirectByteBuffer pool to prevent GC overhead.
 */
class BufferPool(
    private val bufferSize: Int = 32768,
    private val maxPoolSize: Int = 64
) {

    private val pool = ConcurrentLinkedQueue<ByteBuffer>()

    fun acquire(): ByteBuffer {
        val buffer = pool.poll() ?: ByteBuffer.allocateDirect(bufferSize)
        buffer.clear()
        return buffer
    }

    fun release(buffer: ByteBuffer) {
        if (pool.size < maxPoolSize) {
            buffer.clear()
            pool.offer(buffer)
        }
    }

    fun clear() {
        pool.clear()
    }
}
