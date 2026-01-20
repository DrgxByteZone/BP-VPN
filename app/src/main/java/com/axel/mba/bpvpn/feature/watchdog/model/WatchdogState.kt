package com.axel.mba.bpvpn.feature.watchdog.model

/**
 * Operating state of the connection watchdog.
 * Created by: Axel & M.B.A
 */
enum class WatchdogState {
    STANDBY,
    ARMED,
    TRIGGERED_KILL_SWITCH,
    RECONNECTING
}
