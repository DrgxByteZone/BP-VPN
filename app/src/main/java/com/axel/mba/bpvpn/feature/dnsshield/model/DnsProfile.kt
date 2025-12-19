package com.axel.mba.bpvpn.feature.dnsshield.model

/**
 * High-security DNS-over-HTTPS (DoH) profiles.
 * Created by: Axel & M.B.A
 */
enum class DnsProfile(
    val id: String,
    val title: String,
    val description: String,
    val dohEndpoint: String,
    val primaryIp: String,
    val secondaryIp: String
) {
    CLOUDFLARE_STANDARD(
        id = "cf_standard",
        title = "Cloudflare 1.1.1.1",
        description = "Resolusi DNS standar dengan latensi rendah dan tanpa log",
        dohEndpoint = "https://cloudflare-dns.com/dns-query",
        primaryIp = "1.1.1.1",
        secondaryIp = "1.0.0.1"
    ),
    CLOUDFLARE_SECURITY(
        id = "cf_security",
        title = "Cloudflare Security",
        description = "Memfilter domain phishing dan malware (1.1.1.2)",
        dohEndpoint = "https://security.cloudflare-dns.com/dns-query",
        primaryIp = "1.1.1.2",
        secondaryIp = "1.0.0.2"
    ),
    CLOUDFLARE_FAMILY(
        id = "cf_family",
        title = "Cloudflare Family",
        description = "Memfilter domain malware dan konten dewasa (1.1.1.3)",
        dohEndpoint = "https://family.cloudflare-dns.com/dns-query",
        primaryIp = "1.1.1.3",
        secondaryIp = "1.0.0.3"
    ),
    ADGUARD_DNS(
        id = "adguard",
        title = "AdGuard DNS",
        description = "Memfilter domain iklan dan pelacak di tingkat DNS",
        dohEndpoint = "https://dns.adguard-dns.com/dns-query",
        primaryIp = "94.140.14.14",
        secondaryIp = "94.140.15.15"
    ),
    QUAD9_SECURE(
        id = "quad9",
        title = "Quad9 Secure",
        description = "Proteksi terhadap domain berbahaya berdasarkan database ancaman global",
        dohEndpoint = "https://dns.quad9.net/dns-query",
        primaryIp = "9.9.9.9",
        secondaryIp = "149.112.112.112"
    ),
    GOOGLE_PUBLIC(
        id = "google",
        title = "Google Public DNS",
        description = "Infrastruktur DNS global yang andal dan stabil",
        dohEndpoint = "https://dns.google/dns-query",
        primaryIp = "8.8.8.8",
        secondaryIp = "8.8.4.4"
    );

    companion object {
        fun fromId(id: String?): DnsProfile {
            return values().firstOrNull { it.id == id } ?: CLOUDFLARE_STANDARD
        }
    }
}
