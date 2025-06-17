package com.axel.mba.bpvpn.core.model

data class TrafficMetrics(
    val totalQueries: Long = 0,
    val blockedQueries: Long = 0,
    val bytesIn: Long = 0,
    val bytesOut: Long = 0,
    val estimatedBytesSaved: Long = 0
) {
    val blockPercentage: Int
        get() = if (totalQueries == 0L) 0 else ((blockedQueries.toDouble() / totalQueries) * 100).toInt()
}
