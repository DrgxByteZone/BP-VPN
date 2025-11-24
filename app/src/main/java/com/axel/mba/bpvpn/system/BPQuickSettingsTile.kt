package com.axel.mba.bpvpn.system

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.axel.mba.bpvpn.BPApplication
import com.axel.mba.bpvpn.vpn.service.BPVpnService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
class BPQuickSettingsTile : TileService() {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val app = application as BPApplication
        val container = app.container

        scope.launch {
            if (BPVpnService.isRunning) {
                container.stopVpnUseCase()
            } else {
                val mode = container.preferences.protectionMode
                container.startVpnUseCase(mode)
            }
            updateTileState()
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val isRunning = BPVpnService.isRunning
        tile.state = if (isRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = if (isRunning) "BP VPN: Active" else "BP VPN: Off"
        tile.updateTile()
    }
}
