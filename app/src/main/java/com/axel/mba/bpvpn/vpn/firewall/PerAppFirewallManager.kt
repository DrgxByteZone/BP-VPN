package com.axel.mba.bpvpn.vpn.firewall

import com.axel.mba.bpvpn.core.model.AppNetworkInfo
import java.util.concurrent.ConcurrentHashMap

class PerAppFirewallManager {

    private val blockedPackages = ConcurrentHashMap.newKeySet<String>()
    private val bypassedPackages = ConcurrentHashMap.newKeySet<String>()

    fun setAppBlocked(packageName: String, isBlocked: Boolean) {
        if (isBlocked) {
            blockedPackages.add(packageName)
        } else {
            blockedPackages.remove(packageName)
        }
    }

    fun setAppBypassed(packageName: String, isBypassed: Boolean) {
        if (isBypassed) {
            bypassedPackages.add(packageName)
        } else {
            bypassedPackages.remove(packageName)
        }
    }

    fun isAppBlocked(packageName: String): Boolean = blockedPackages.contains(packageName)
    fun isAppBypassed(packageName: String): Boolean = bypassedPackages.contains(packageName)

    fun getBlockedPackages(): Set<String> = blockedPackages.toSet()
    fun getBypassedPackages(): Set<String> = bypassedPackages.toSet()

    fun loadRules(blocked: Set<String>, bypassed: Set<String>) {
        blockedPackages.clear()
        blockedPackages.addAll(blocked)
        bypassedPackages.clear()
        bypassedPackages.addAll(bypassed)
    }
}
