package com.axel.mba.bpvpn.feature.watchdog.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.axel.mba.bpvpn.BPApplication
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.feature.watchdog.data.preferences.WatchdogPreferences
import com.axel.mba.bpvpn.feature.watchdog.data.repository.WatchdogRepositoryImpl
import com.axel.mba.bpvpn.feature.watchdog.engine.BatteryOptimizationAdvisor
import com.axel.mba.bpvpn.feature.watchdog.engine.KillSwitchManager
import com.axel.mba.bpvpn.feature.watchdog.engine.NetworkStateObserver
import com.axel.mba.bpvpn.feature.watchdog.model.KillSwitchPolicy
import com.axel.mba.bpvpn.feature.watchdog.model.ReconnectStrategy
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

/**
 * Advanced Settings Activity for Kill Switch & Auto Reconnect.
 * Created by: Axel & M.B.A
 */
class AdvancedSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    private lateinit var btnBack: ImageView
    private lateinit var rgKillSwitch: RadioGroup
    private lateinit var rbKsDisabled: RadioButton
    private lateinit var rbKsUnexpected: RadioButton
    private lateinit var rbKsLockdown: RadioButton

    private lateinit var switchAutoReconnect: SwitchMaterial
    private lateinit var rgReconnectStrategy: RadioGroup
    private lateinit var rbImmediate: RadioButton
    private lateinit var rbBackoff: RadioButton
    private lateinit var rbPersistent: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced_settings)

        val container = (application as BPApplication).container
        val prefs = WatchdogPreferences(container.preferences)
        val observer = NetworkStateObserver(this)
        val ksManager = KillSwitchManager()
        val batteryAdvisor = BatteryOptimizationAdvisor(this)
        val repository = WatchdogRepositoryImpl(prefs, observer, ksManager, batteryAdvisor)
        viewModel = SettingsViewModel(repository)

        initViews()
        setupListeners()
        observeState()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        rgKillSwitch = findViewById(R.id.rgKillSwitch)
        rbKsDisabled = findViewById(R.id.rbKsDisabled)
        rbKsUnexpected = findViewById(R.id.rbKsUnexpected)
        rbKsLockdown = findViewById(R.id.rbKsLockdown)

        switchAutoReconnect = findViewById(R.id.switchAutoReconnect)
        rgReconnectStrategy = findViewById(R.id.rgReconnectStrategy)
        rbImmediate = findViewById(R.id.rbImmediate)
        rbBackoff = findViewById(R.id.rbBackoff)
        rbPersistent = findViewById(R.id.rbPersistent)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        rgKillSwitch.setOnCheckedChangeListener { _, checkedId ->
            val policy = when (checkedId) {
                R.id.rbKsUnexpected -> KillSwitchPolicy.ON_UNEXPECTED_DROP
                R.id.rbKsLockdown -> KillSwitchPolicy.STRICT_LOCKDOWN
                else -> KillSwitchPolicy.DISABLED
            }
            viewModel.setKillSwitchPolicy(policy)
            Toast.makeText(this, "Kebijakan Kill Switch: ${policy.title}", Toast.LENGTH_SHORT).show()
        }

        switchAutoReconnect.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAutoReconnect(isChecked)
            rgReconnectStrategy.isEnabled = isChecked
            for (i in 0 until rgReconnectStrategy.childCount) {
                rgReconnectStrategy.getChildAt(i).isEnabled = isChecked
            }
        }

        rgReconnectStrategy.setOnCheckedChangeListener { _, checkedId ->
            val strategy = when (checkedId) {
                R.id.rbImmediate -> ReconnectStrategy.IMMEDIATE
                R.id.rbPersistent -> ReconnectStrategy.PERSISTENT
                else -> ReconnectStrategy.EXPONENTIAL_BACKOFF
            }
            viewModel.setReconnectStrategy(strategy)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.killSwitchPolicy.collect { policy ->
                when (policy) {
                    KillSwitchPolicy.DISABLED -> rbKsDisabled.isChecked = true
                    KillSwitchPolicy.ON_UNEXPECTED_DROP -> rbKsUnexpected.isChecked = true
                    KillSwitchPolicy.STRICT_LOCKDOWN -> rbKsLockdown.isChecked = true
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isAutoReconnect.collect { enabled ->
                switchAutoReconnect.isChecked = enabled
            }
        }

        lifecycleScope.launch {
            viewModel.reconnectStrategy.collect { strategy ->
                when (strategy) {
                    ReconnectStrategy.IMMEDIATE -> rbImmediate.isChecked = true
                    ReconnectStrategy.EXPONENTIAL_BACKOFF -> rbBackoff.isChecked = true
                    ReconnectStrategy.PERSISTENT -> rbPersistent.isChecked = true
                }
            }
        }
    }
}
