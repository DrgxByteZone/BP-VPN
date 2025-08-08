package com.axel.mba.bpvpn.core.network.dns

data class DnsPacket(
    val header: DnsHeader,
    val questions: List<DnsQuestion>,
    val answers: List<DnsResourceRecord> = emptyList()
) {
    val primaryQuestion: DnsQuestion? get() = questions.firstOrNull()
    val domainName: String? get() = primaryQuestion?.domain
}
