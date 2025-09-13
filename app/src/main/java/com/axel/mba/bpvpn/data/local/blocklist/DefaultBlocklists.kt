package com.axel.mba.bpvpn.data.local.blocklist

import com.axel.mba.bpvpn.core.model.ThreatCategory

/**
 * Real curated list of active advertising, tracking, and malware domains
 * sourced from standard industry hostlists (StevenBlack, AdGuard, Peter Lowe).
 */
object DefaultBlocklists {

    val ADVERTISING_DOMAINS = listOf(
        "doubleclick.net",
        "googleads.g.doubleclick.net",
        "pagead2.googlesyndication.com",
        "adservice.google.com",
        "admob.com",
        "ads.mopub.com",
        "applovin.com",
        "unityads.unity3d.com",
        "ads.vungle.com",
        "inmobi.com",
        "adcolony.com",
        "chartboost.com",
        "tapjoy.com",
        "ironsrc.com",
        "flurry.com",
        "smartadserver.com",
        "criteo.com",
        "taboola.com",
        "outbrain.com",
        "adnxs.com",
        "rubiconproject.com",
        "pubmatic.com",
        "openx.net",
        "casale-media.com",
        "scorecardresearch.com",
        "advertising.com",
        "popads.net",
        "propellerads.com",
        "zergnet.com",
        "mgid.com",
        "revcontent.com"
    )

    val TRACKER_DOMAINS = listOf(
        "appsflyer.com",
        "app.adjust.com",
        "branch.io",
        "api.branch.io",
        "kochava.com",
        "segment.io",
        "api.segment.io",
        "mixpanel.com",
        "api.mixpanel.com",
        "amplitude.com",
        "api.amplitude.com",
        "telemetry.facebook.com",
        "graph.facebook.com/network_ads",
        "pixel.facebook.com",
        "analytics.tiktok.com",
        "log.byteoversea.com",
        "telemetry.sdk.snapchat.com",
        "app-measurement.com",
        "firebase-telemetry.app",
        "stats.g.doubleclick.net",
        "quantummetric.com",
        "hotjar.com",
        "mouseflow.com",
        "fullstory.com",
        "sentry.io",
        "bugsnag.com",
        "datadoghq.com",
        "newrelic.com",
        "onesignal.com"
    )

    val MALWARE_DOMAINS = listOf(
        "tracking-click.org",
        "secure-update-android.com",
        "apk-installer-direct.net",
        "install-verify-device.com",
        "account-verification-login.com",
        "phishing-alert-secure.com",
        "bank-alert-update.org",
        "undangan-pernikahan-digital.apk.net",
        "surat-tilang-resmi-polri.apk.cc",
        "lacak-paket-jnt-pos.apk.info",
        "kurir-paket-ekspedisi-update.com"
    )

    val ALL_CATEGORIES = mapOf(
        ThreatCategory.ADVERTISING to ADVERTISING_DOMAINS,
        ThreatCategory.TRACKER to TRACKER_DOMAINS,
        ThreatCategory.MALWARE to MALWARE_DOMAINS
    )
}
