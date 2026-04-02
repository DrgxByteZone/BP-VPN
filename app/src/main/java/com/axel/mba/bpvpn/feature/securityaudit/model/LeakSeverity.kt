package com.axel.mba.bpvpn.feature.securityaudit.model

/**
 * Severity level of a detected leak or security vulnerability.
 * Created by: Axel & M.B.A
 */
enum class LeakSeverity(val title: String, val colorHex: String) {
    SECURE("Aman", "#00D26A"),
    WARNING("Peringatan", "#FFBB00"),
    CRITICAL("Bocor", "#FF4D4D")
}
