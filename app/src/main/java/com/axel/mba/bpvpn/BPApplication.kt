package com.axel.mba.bpvpn

import android.app.Application
import com.axel.mba.bpvpn.di.AppContainer
import com.axel.mba.bpvpn.vpn.service.VpnNotificationManager

/**
 * Black Panther VPN (BP VPN)
 * Created by: Axel & M.B.A
 *
 * Core Application lifecycle & Dependency Injection initialization.
 */
class BPApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initialize Proprietary In-House DI Container
        container = AppContainer(this)

        // Register Notification Channel for Foreground Protection
        VpnNotificationManager.createNotificationChannel(this)

        // Preload default ad/tracker blocklists into RAM Trie
        container.blocklistManager.initializeAsync()
    }

    companion object {
        lateinit var instance: BPApplication
            private set
    }
}
