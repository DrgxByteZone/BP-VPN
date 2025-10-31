package com.axel.mba.bpvpn.feature.splittunneling.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.axel.mba.bpvpn.data.local.preferences.BPPreferences
import com.axel.mba.bpvpn.feature.splittunneling.data.classifier.AppCategoryClassifier
import com.axel.mba.bpvpn.feature.splittunneling.data.db.SplitTunnelDao
import com.axel.mba.bpvpn.feature.splittunneling.data.db.SplitTunnelEntity
import com.axel.mba.bpvpn.feature.splittunneling.domain.repository.SplitTunnelRepository
import com.axel.mba.bpvpn.feature.splittunneling.model.AppCategory
import com.axel.mba.bpvpn.feature.splittunneling.model.BypassAppInfo
import com.axel.mba.bpvpn.feature.splittunneling.model.SplitTunnelPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Implementation of SplitTunnelRepository using PackageManager, SQLite, and Preferences.
 * Created by: Axel & M.B.A
 */
class SplitTunnelRepositoryImpl(
    private val context: Context,
    private val dao: SplitTunnelDao,
    private val preferences: BPPreferences
) : SplitTunnelRepository {

    private val policyFlow = MutableStateFlow(getSplitTunnelPolicy())

    override fun getSplitTunnelPolicy(): SplitTunnelPolicy {
        val raw = preferences.getString(KEY_SPLIT_POLICY, SplitTunnelPolicy.ALL_APPS.id)
        return SplitTunnelPolicy.fromId(raw)
    }

    override fun setSplitTunnelPolicy(policy: SplitTunnelPolicy) {
        preferences.putString(KEY_SPLIT_POLICY, policy.id)
        policyFlow.value = policy
    }

    override fun observePolicy(): Flow<SplitTunnelPolicy> = policyFlow.asStateFlow()

    override suspend fun getInstalledApps(): List<BypassAppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val bypassRules = dao.getAllRules()
        val ownPkg = context.packageName

        installed.filter { it.packageName != ownPkg }
            .map { appInfo ->
                val appName = pm.getApplicationLabel(appInfo).toString()
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val category = AppCategoryClassifier.classify(appInfo.packageName, appName)
                val isBypassed = bypassRules[appInfo.packageName] ?: false
                val icon = try {
                    pm.getApplicationIcon(appInfo)
                } catch (_: Exception) {
                    null
                }

                BypassAppInfo(
                    packageName = appInfo.packageName,
                    appName = appName,
                    icon = icon,
                    category = category,
                    isBypassed = isBypassed,
                    isSystemApp = isSystem
                )
            }
            .sortedWith(
                compareByDescending<BypassAppInfo> { it.isBypassed }
                    .thenBy { it.category != AppCategory.BANKING }
                    .thenBy { it.appName.lowercase() }
            )
    }

    override suspend fun setAppBypassed(packageName: String, isBypassed: Boolean) = withContext(Dispatchers.IO) {
        val entity = SplitTunnelEntity(
            packageName = packageName,
            isBypassed = isBypassed,
            categoryName = "USER_DEFINED",
            updatedAt = System.currentTimeMillis()
        )
        dao.insertOrUpdate(entity)
    }

    override suspend fun applyBankingPreset(): Int = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        var count = 0

        installed.forEach { appInfo ->
            val appName = pm.getApplicationLabel(appInfo).toString()
            if (AppCategoryClassifier.isBankingApp(appInfo.packageName, appName)) {
                dao.insertOrUpdate(
                    SplitTunnelEntity(
                        packageName = appInfo.packageName,
                        isBypassed = true,
                        categoryName = AppCategory.BANKING.name,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                count++
            }
        }
        count
    }

    override suspend fun getBypassedPackages(): Set<String> = withContext(Dispatchers.IO) {
        dao.getBypassedPackages()
    }

    companion object {
        private const val KEY_SPLIT_POLICY = "pref_split_tunnel_policy"
    }
}
