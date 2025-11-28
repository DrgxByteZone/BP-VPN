package com.axel.mba.bpvpn.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.axel.mba.bpvpn.BPApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val app = context.applicationContext as? BPApplication ?: return
            val prefs = app.container.preferences
            if (prefs.isAutoStartOnBoot) {
                CoroutineScope(Dispatchers.Main).launch {
                    app.container.startVpnUseCase(prefs.protectionMode)
                }
            }
        }
    }
}

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Network state changes are observed reactively by NetworkMonitor
    }
}
