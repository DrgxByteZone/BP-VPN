package com.axel.mba.bpvpn.feature.securityaudit.data.db

import android.content.ContentValues
import com.axel.mba.bpvpn.data.local.db.BPSQLiteHelper

/**
 * Data Access Object for Security Audit persistence.
 * Created by: Axel & M.B.A
 */
class SecurityAuditDao(private val dbHelper: BPSQLiteHelper) {

    init {
        createTableIfNotExists()
    }

    private fun createTableIfNotExists() {
        val db = dbHelper.writableDatabase
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                overall_score INTEGER NOT NULL,
                dns_leak_status TEXT NOT NULL,
                webrtc_leak_status TEXT NOT NULL,
                ipv6_leak_status TEXT NOT NULL,
                ip_address TEXT NOT NULL,
                isp_name TEXT NOT NULL,
                audited_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    fun insert(entity: SecurityAuditEntity): Long {
        val values = ContentValues().apply {
            put("overall_score", entity.overallScore)
            put("dns_leak_status", entity.dnsLeakStatus)
            put("webrtc_leak_status", entity.webRtcLeakStatus)
            put("ipv6_leak_status", entity.ipv6LeakStatus)
            put("ip_address", entity.ipAddress)
            put("isp_name", entity.ispName)
            put("audited_at", entity.auditedAt)
        }
        return dbHelper.writableDatabase.insert(TABLE_NAME, null, values)
    }

    fun getLatestAudit(): SecurityAuditEntity? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "audited_at DESC", "1")
        cursor.use {
            if (it.moveToFirst()) {
                val idIdx = it.getColumnIndexOrThrow("id")
                val scoreIdx = it.getColumnIndexOrThrow("overall_score")
                val dnsIdx = it.getColumnIndexOrThrow("dns_leak_status")
                val rtcIdx = it.getColumnIndexOrThrow("webrtc_leak_status")
                val ip6Idx = it.getColumnIndexOrThrow("ipv6_leak_status")
                val ipIdx = it.getColumnIndexOrThrow("ip_address")
                val ispIdx = it.getColumnIndexOrThrow("isp_name")
                val timeIdx = it.getColumnIndexOrThrow("audited_at")

                return SecurityAuditEntity(
                    id = it.getLong(idIdx),
                    overallScore = it.getInt(scoreIdx),
                    dnsLeakStatus = it.getString(dnsIdx),
                    webRtcLeakStatus = it.getString(rtcIdx),
                    ipv6LeakStatus = it.getString(ip6Idx),
                    ipAddress = it.getString(ipIdx),
                    ispName = it.getString(ispIdx),
                    auditedAt = it.getLong(timeIdx)
                )
            }
        }
        return null
    }

    companion object {
        const val TABLE_NAME = "security_audit_history"
    }
}
