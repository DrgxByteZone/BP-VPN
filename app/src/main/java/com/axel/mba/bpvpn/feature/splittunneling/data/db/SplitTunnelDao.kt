package com.axel.mba.bpvpn.feature.splittunneling.data.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.axel.mba.bpvpn.data.local.db.BPSQLiteHelper

/**
 * Data Access Object for Split Tunneling configurations.
 * Created by: Axel & M.B.A
 */
class SplitTunnelDao(private val dbHelper: BPSQLiteHelper) {

    init {
        createTableIfNotExists()
    }

    private fun createTableIfNotExists() {
        val db = dbHelper.writableDatabase
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                package_name TEXT PRIMARY KEY NOT NULL,
                is_bypassed INTEGER NOT NULL,
                category_name TEXT NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    fun insertOrUpdate(entity: SplitTunnelEntity) {
        val values = ContentValues().apply {
            put("package_name", entity.packageName)
            put("is_bypassed", if (entity.isBypassed) 1 else 0)
            put("category_name", entity.categoryName)
            put("updated_at", entity.updatedAt)
        }
        dbHelper.writableDatabase.insertWithOnConflict(
            TABLE_NAME,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getAllRules(): Map<String, Boolean> {
        val result = mutableMapOf<String, Boolean>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf("package_name", "is_bypassed"), null, null, null, null, null)
        cursor.use {
            val pkgIdx = it.getColumnIndexOrThrow("package_name")
            val bypassIdx = it.getColumnIndexOrThrow("is_bypassed")
            while (it.moveToNext()) {
                val pkg = it.getString(pkgIdx)
                val bypassed = it.getInt(bypassIdx) == 1
                result[pkg] = bypassed
            }
        }
        return result
    }

    fun getBypassedPackages(): Set<String> {
        val result = mutableSetOf<String>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf("package_name"), "is_bypassed = 1", null, null, null, null)
        cursor.use {
            val pkgIdx = it.getColumnIndexOrThrow("package_name")
            while (it.moveToNext()) {
                result.add(it.getString(pkgIdx))
            }
        }
        return result
    }

    fun deleteRule(packageName: String) {
        dbHelper.writableDatabase.delete(TABLE_NAME, "package_name = ?", arrayOf(packageName))
    }

    fun clearAll() {
        dbHelper.writableDatabase.delete(TABLE_NAME, null, null)
    }

    companion object {
        const val TABLE_NAME = "split_tunnel_rules"
    }
}
