package com.axel.mba.bpvpn.feature.splittunneling.model

/**
 * Intelligent categorization of Android applications.
 * Created by: Axel & M.B.A
 */
enum class AppCategory(val displayName: String, val iconResName: String) {
    BANKING("Perbankan & Dompet Digital", "ic_category_banking"),
    SOCIAL("Media Sosial & Komunikasi", "ic_category_social"),
    STREAMING("Streaming & Hiburan", "ic_category_streaming"),
    GAMING("Game & Hiburan Interaktif", "ic_category_gaming"),
    BROWSING("Peramban Web", "ic_category_browsing"),
    SYSTEM("Sistem & Utilitas", "ic_category_system"),
    OTHER("Lainnya", "ic_category_other")
}
