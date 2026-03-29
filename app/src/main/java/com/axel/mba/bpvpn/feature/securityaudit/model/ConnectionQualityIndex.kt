package com.axel.mba.bpvpn.feature.securityaudit.model

/**
 * Composite Connection Quality Index (CQI) score (0 - 100).
 * Created by: Axel & M.B.A
 */
data class ConnectionQualityIndex(
    val score: Int = 100,
    val rating: String = "Sempurna",
    val latencyScore: Int = 100,
    val stabilityScore: Int = 100,
    val encryptionScore: Int = 100
) {
    companion object {
        fun calculate(pingMs: Long, jitterMs: Long, hasLeaks: Boolean): ConnectionQualityIndex {
            var score = 100
            if (pingMs > 150) score -= 20
            else if (pingMs > 80) score -= 10

            if (jitterMs > 20) score -= 15
            else if (jitterMs > 5) score -= 5

            if (hasLeaks) score -= 40

            val clampedScore = score.coerceIn(0, 100)
            val rating = when {
                clampedScore >= 85 -> "Sempurna"
                clampedScore >= 70 -> "Baik"
                clampedScore >= 50 -> "Cukup"
                else -> "Rentan"
            }

            return ConnectionQualityIndex(
                score = clampedScore,
                rating = rating,
                latencyScore = (100 - (pingMs / 3).toInt()).coerceIn(0, 100),
                stabilityScore = (100 - (jitterMs * 2).toInt()).coerceIn(0, 100),
                encryptionScore = if (hasLeaks) 30 else 100
            )
        }
    }
}
