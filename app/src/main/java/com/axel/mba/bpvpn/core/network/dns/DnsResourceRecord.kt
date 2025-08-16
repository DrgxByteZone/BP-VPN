package com.axel.mba.bpvpn.core.network.dns

data class DnsResourceRecord(
    val name: String,
    val type: Int,
    val rClass: Int,
    val ttl: Long,
    val rData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DnsResourceRecord
        return name == other.name && type == other.type && rData.contentEquals(other.rData)
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type
        result = 31 * result + rData.contentHashCode()
        return result
    }
}
