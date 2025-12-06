package com.axel.mba.bpvpn.feature.dnsshield.data.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.axel.mba.bpvpn.data.local.db.BPSQLiteHelper

/**
 * Data Access Object for User-Defined Custom DNS Rules.
 * Created by: Axel & M.B.A
 */
class CustomDomainRuleDao(private val dbHelper: BPSQLiteHelper) {

    init {
        createTableIfNotExists()
    }

    private fun createTableIfNotExists() {
        val db = dbHelper.writableDatabase
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                domain TEXT UNIQUE NOT NULL,
                action TEXT NOT NULL,
                note TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    fun insertOrUpdate(entity: CustomDomainRuleEntity): Long {
        val values = ContentValues().apply {
            put("domain", entity.domain.lowercase())
            put("action", entity.action)
            put("note", entity.note)
            put("created_at", entity.createdAt)
        }
        return dbHelper.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getAll(): List<CustomDomainRuleEntity> {
        val list = mutableListOf<CustomDomainRuleEntity>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "domain ASC")
        cursor.use {
            val idIdx = it.getColumnIndexOrThrow("id")
            val domainIdx = it.getColumnIndexOrThrow("domain")
            val actionIdx = it.getColumnIndexOrThrow("action")
            val noteIdx = it.getColumnIndexOrThrow("note")
            val createdIdx = it.getColumnIndexOrThrow("created_at")

            while (it.moveToNext()) {
                list.add(
                    CustomDomainRuleEntity(
                        id = it.getLong(idIdx),
                        domain = it.getString(domainIdx),
                        action = it.getString(actionIdx),
                        note = it.getString(noteIdx),
                        createdAt = it.getLong(createdIdx)
                    )
                )
            }
        }
        return list
    }

    fun delete(domain: String) {
        dbHelper.writableDatabase.delete(TABLE_NAME, "domain = ?", arrayOf(domain.lowercase()))
    }

    companion object {
        const val TABLE_NAME = "custom_dns_shield_rules"
    }
}
