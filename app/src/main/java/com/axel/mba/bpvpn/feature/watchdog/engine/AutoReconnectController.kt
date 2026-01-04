package com.axel.mba.bpvpn.feature.watchdog.engine

import com.axel.mba.bpvpn.feature.watchdog.model.ReconnectStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Controller orchestrating intelligent VPN reconnection after unexpected drops.
 * Created by: Axel & M.B.A
 */
class AutoReconnectController(
    private val onTriggerReconnect: suspend () -> Boolean
) {

    private var retryJob: Job? = null
    private var retryAttempts = 0

    fun scheduleReconnect(scope: CoroutineScope, strategy: ReconnectStrategy) {
        if (retryJob?.isActive == true) return

        retryJob = scope.launch {
            while (isActive && retryAttempts < strategy.maxRetries) {
                retryAttempts++
                val delayTime = when (strategy) {
                    ReconnectStrategy.IMMEDIATE -> 1000L
                    ReconnectStrategy.EXPONENTIAL_BACKOFF -> (1000L * (1 shl (retryAttempts - 1))).coerceAtMost(30000L)
                    ReconnectStrategy.PERSISTENT -> 3000L
                }
                delay(delayTime)

                val success = onTriggerReconnect()
                if (success) {
                    reset()
                    break
                }
            }
        }
    }

    fun reset() {
        retryJob?.cancel()
        retryJob = null
        retryAttempts = 0
    }
}
