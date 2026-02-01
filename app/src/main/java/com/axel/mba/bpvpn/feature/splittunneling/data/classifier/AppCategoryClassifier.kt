package com.axel.mba.bpvpn.feature.splittunneling.data.classifier

import com.axel.mba.bpvpn.feature.splittunneling.model.AppCategory

/**
 * Intelligent categorization of installed applications.
 * Detects financial/banking apps, e-wallets, social media, and streaming services.
 * Created by: Axel & M.B.A
 */
object AppCategoryClassifier {

    private val BANKING_SIGNATURES = setOf(
        "bca", "klikbca", "mybca",
        "mandiri", "livin",
        "bri", "brimo",
        "bni", "bni.mobile",
        "cimb", "octo",
        "permata", "jenius", "btpn",
        "danamon", "panin", "bsi", "byond",
        "dana", "ovo", "gopay", "shopeepay", "linkaja", "flip", "plink",
        "jago", "seabank", "bankneo", "aladin", "krom", "superbank",
        "paypal", "wise", "binance", "tokocrypto", "indodax", "ajaib", "bibit", "pluang"
    )

    private val SOCIAL_SIGNATURES = setOf(
        "whatsapp", "telegram", "instagram", "tiktok", "twitter", "x.corp",
        "facebook", "messenger", "threads", "discord", "line", "signal",
        "wechat", "snapchat", "reddit"
    )

    private val STREAMING_SIGNATURES = setOf(
        "youtube", "netflix", "spotify", "disney", "hotstar", "vidio",
        "primevideo", "appletv", "wetv", "iqiyi", "twitch", "soundcloud", "deezer"
    )

    private val GAMING_SIGNATURES = setOf(
        "mobilelegends", "pubg", "freefire", "genshin", "honkai",
        "roblox", "minecraft", "supercell", "clash", "codm", "riot", "wildrift"
    )

    private val BROWSING_SIGNATURES = setOf(
        "chrome", "firefox", "brave", "edge", "opera", "ucmobile", "browser"
    )

    fun classify(packageName: String, appName: String): AppCategory {
        val lowerPkg = packageName.lowercase()
        val lowerName = appName.lowercase()

        // 1. Check Banking & E-Wallets
        if (BANKING_SIGNATURES.any { lowerPkg.contains(it) || lowerName.contains(it) }) {
            return AppCategory.BANKING
        }

        // 2. Check Social Media
        if (SOCIAL_SIGNATURES.any { lowerPkg.contains(it) || lowerName.contains(it) }) {
            return AppCategory.SOCIAL
        }

        // 3. Check Streaming
        if (STREAMING_SIGNATURES.any { lowerPkg.contains(it) || lowerName.contains(it) }) {
            return AppCategory.STREAMING
        }

        // 4. Check Gaming
        if (GAMING_SIGNATURES.any { lowerPkg.contains(it) || lowerName.contains(it) }) {
            return AppCategory.GAMING
        }

        // 5. Check Browsers
        if (BROWSING_SIGNATURES.any { lowerPkg.contains(it) || lowerName.contains(it) }) {
            return AppCategory.BROWSING
        }

        // 6. Check System / Android OS
        if (lowerPkg.startsWith("android") || lowerPkg.startsWith("com.android") || lowerPkg.startsWith("com.google.android.gms")) {
            return AppCategory.SYSTEM
        }

        return AppCategory.OTHER
    }

    fun isBankingApp(packageName: String, appName: String): Boolean {
        return classify(packageName, appName) == AppCategory.BANKING
    }
}
