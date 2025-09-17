package com.axel.mba.bpvpn.data.local.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.axel.mba.bpvpn.data.local.db.entity.CustomRuleEntity
import com.axel.mba.bpvpn.data.local.db.entity.DnsLogEntity
import com.axel.mba.bpvpn.data.local.db.entity.FirewallRuleEntity
import com.axel.mba.bpvpn.data.local.db.entity.ThreatEntity

class BPSQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // 1. DNS Logs Table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_DNS_LOGS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                domain TEXT NOT NULL,
                query_type TEXT NOT NULL,
                resolved_ip TEXT NOT NULL,
                is_blocked INTEGER NOT NULL,
                threat_category TEXT,
                requesting_app TEXT,
                timestamp INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_dns_timestamp ON $TABLE_DNS_LOGS (timestamp DESC)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_dns_blocked ON $TABLE_DNS_LOGS (is_blocked)")

        // 2. Threat Statistics Table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_THREAT_STATS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                category TEXT UNIQUE NOT NULL,
                count INTEGER NOT NULL,
                last_blocked_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // 3. Firewall Rules Table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_FIREWALL_RULES (
                package_name TEXT PRIMARY KEY NOT NULL,
                is_blocked INTEGER NOT NULL,
                is_bypassed INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        // 4. Custom Rules Table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_CUSTOM_RULES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                domain TEXT UNIQUE NOT NULL,
                is_blocked INTEGER NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DNS_LOGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_THREAT_STATS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FIREWALL_RULES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOM_RULES")
        onCreate(db)
    }

    // --- DNS Log Operations ---
    fun insertDnsLog(log: DnsLogEntity): Long {
        val values = ContentValues().apply {
            put("domain", log.domain)
            put("query_type", log.queryType)
            put("resolved_ip", log.resolvedIp)
            put("is_blocked", if (log.isBlocked) 1 else 0)
            put("threat_category", log.threatCategory)
            put("requesting_app", log.requestingAppPackage)
            put("timestamp", log.timestamp)
        }
        return writableDatabase.insert(TABLE_DNS_LOGS, null, values)
    }

    fun getRecentDnsLogs(limit: Int = 100, filterBlockedOnly: Boolean? = null): List<DnsLogEntity> {
        val list = mutableListOf<DnsLogEntity>()
        val selection = when (filterBlockedOnly) {
            true -> "is_blocked = 1"
            false -> "is_blocked = 0"
            null -> null
        }
        val cursor = readableDatabase.query(
            TABLE_DNS_LOGS,
            null,
            selection,
            null,
            null,
            null,
            "timestamp DESC",
            limit.toString()
        )
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow("id")
            val domainIdx = it.getColumnIndexOrThrow("domain")
            val qTypeIdx = it.getColumnIndexOrThrow("query_type")
            val ipIdx = it.getColumnIndexOrThrow("resolved_ip")
            val blockedIdx = it.getColumnIndexOrThrow("is_blocked")
            val categoryIdx = it.getColumnIndexOrThrow("threat_category")
            val appIdx = it.getColumnIndexOrThrow("requesting_app")
            val timeIdx = it.getColumnIndexOrThrow("timestamp")

            while (it.moveToNext()) {
                list.add(
                    DnsLogEntity(
                        id = it.getLong(idIdx),
                        domain = it.getString(domainIdx),
                        queryType = it.getString(qTypeIdx),
                        resolvedIp = it.getString(ipIdx),
                        isBlocked = it.getInt(blockedIdx) == 1,
                        threatCategory = it.getString(categoryIdx),
                        requestingAppPackage = it.getString(appIdx),
                        timestamp = it.getLong(timeIdx)
                    )
                )
            }
        }
        return list
    }

    fun clearDnsLogs() {
        writableDatabase.delete(TABLE_DNS_LOGS, null, null)
    }

    // --- Firewall Rule Operations ---
    fun saveFirewallRule(rule: FirewallRuleEntity) {
        val values = ContentValues().apply {
            put("package_name", rule.packageName)
            put("is_blocked", if (rule.isBlocked) 1 else 0)
            put("is_bypassed", if (rule.isBypassed) 1 else 0)
            put("updated_at", rule.updatedAt)
        }
        writableDatabase.insertWithOnConflict(
            TABLE_FIREWALL_RULES,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getAllFirewallRules(): List<FirewallRuleEntity> {
        val list = mutableListOf<FirewallRuleEntity>()
        val cursor = readableDatabase.query(TABLE_FIREWALL_RULES, null, null, null, null, null, null)
        cursor.use {
            val pkgIdx = it.getColumnIndexOrThrow("package_name")
            val blockedIdx = it.getColumnIndexOrThrow("is_blocked")
            val bypassedIdx = it.getColumnIndexOrThrow("is_bypassed")
            val timeIdx = it.getColumnIndexOrThrow("updated_at")

            while (it.moveToNext()) {
                list.add(
                    FirewallRuleEntity(
                        packageName = it.getString(pkgIdx),
                        isBlocked = it.getInt(blockedIdx) == 1,
                        isBypassed = it.getInt(bypassedIdx) == 1,
                        updatedAt = it.getLong(timeIdx)
                    )
                )
            }
        }
        return list
    }

    // --- Custom Domain Rules ---
    fun saveCustomRule(rule: CustomRuleEntity) {
        val values = ContentValues().apply {
            put("domain", rule.domain.lowercase())
            put("is_blocked", if (rule.isBlocked) 1 else 0)
            put("created_at", rule.createdAt)
        }
        writableDatabase.insertWithOnConflict(
            TABLE_CUSTOM_RULES,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getAllCustomRules(): List<CustomRuleEntity> {
        val list = mutableListOf<CustomRuleEntity>()
        val cursor = readableDatabase.query(TABLE_CUSTOM_RULES, null, null, null, null, null, "domain ASC")
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow("id")
            val domainIdx = it.getColumnIndexOrThrow("domain")
            val blockedIdx = it.getColumnIndexOrThrow("is_blocked")
            val timeIdx = it.getColumnIndexOrThrow("created_at")

            while (it.moveToNext()) {
                list.add(
                    CustomRuleEntity(
                        id = it.getLong(idIdx),
                        domain = it.getString(domainIdx),
                        isBlocked = it.getInt(blockedIdx) == 1,
                        createdAt = it.getLong(timeIdx)
                    )
                )
            }
        }
        return list
    }

    fun deleteCustomRule(domain: String) {
        writableDatabase.delete(TABLE_CUSTOM_RULES, "domain = ?", arrayOf(domain.lowercase()))
    }

    companion object {
        const val DATABASE_NAME = "bp_vpn_master.db"
        const val DATABASE_VERSION = 1

        const val TABLE_DNS_LOGS = "dns_logs"
        const val TABLE_THREAT_STATS = "threat_stats"
        const val TABLE_FIREWALL_RULES = "firewall_rules"
        const val TABLE_CUSTOM_RULES = "custom_rules"
    }
}
