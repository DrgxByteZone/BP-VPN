package com.axel.mba.bpvpn.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.net.VpnService
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axel.mba.bpvpn.BPApplication
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.core.common.formatDataSize
import com.axel.mba.bpvpn.core.model.ServerLocation
import com.axel.mba.bpvpn.core.model.VpnState
import com.axel.mba.bpvpn.ui.adapter.AppFirewallAdapter
import com.axel.mba.bpvpn.ui.adapter.ServerSelectorAdapter
import com.axel.mba.bpvpn.ui.view.LiquidGlassCardView
import com.axel.mba.bpvpn.ui.view.LiquidShieldButtonView
import android.content.Intent
import com.axel.mba.bpvpn.feature.dnsshield.ui.DnsShieldActivity
import com.axel.mba.bpvpn.feature.obfuscation.ui.ObfuscationSettingsDialog
import com.axel.mba.bpvpn.feature.securityaudit.ui.SecurityAuditActivity
import com.axel.mba.bpvpn.feature.splittunneling.ui.SplitTunnelActivity
import com.axel.mba.bpvpn.feature.watchdog.ui.AdvancedSettingsActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    // Splash views
    private lateinit var splashOverlay: View
    private lateinit var splashContent: View

    // Dashboard views
    private lateinit var btnShieldToggle: LiquidShieldButtonView
    private lateinit var tvProtectionStatus: TextView
    private lateinit var tvProtectionSubtext: TextView
    private lateinit var tvLiveIpAddress: TextView
    private lateinit var tvLiveLocation: TextView
    private lateinit var tvRefreshIp: TextView
    private lateinit var tvModeBadge: TextView
    private lateinit var cardServerSelector: LiquidGlassCardView
    private lateinit var ivSelectedServerIcon: ImageView
    private lateinit var tvSelectedServerName: TextView
    private lateinit var tvSelectedServerDesc: TextView

    // Hidden views for backward compatibility with ViewModel
    private lateinit var cardModeSelector: View
    private lateinit var tvCurrentModeName: TextView
    private lateinit var tvBlockedCounter: TextView
    private lateinit var tvDataSavedCounter: TextView

    // Navigation Pages (Clean 4-Tab Structure)
    private lateinit var pageDashboard: View
    private lateinit var pageFirewall: View
    private lateinit var pageSettings: View
    private lateinit var pageAbout: View

    // Bottom Navigation Tabs
    private lateinit var tabDashboard: LinearLayout
    private lateinit var tabFirewall: LinearLayout
    private lateinit var tabSettings: LinearLayout
    private lateinit var tabAbout: LinearLayout

    private lateinit var ivTabDashboard: ImageView
    private lateinit var ivTabFirewall: ImageView
    private lateinit var ivTabSettings: ImageView
    private lateinit var ivTabAbout: ImageView

    private lateinit var tvTabDashboard: TextView
    private lateinit var tvTabFirewall: TextView
    private lateinit var tvTabSettings: TextView
    private lateinit var tvTabAbout: TextView

    // App Firewall
    private lateinit var rvAppsList: RecyclerView
    private lateinit var appFirewallAdapter: AppFirewallAdapter

    // VpnService Permission Launcher
    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.toggleVpn()
        } else {
            Toast.makeText(this, "Izin VPN diperlukan untuk mengaktifkan proteksi", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = (application as BPApplication).container
        viewModel = MainViewModel(container)

        initViews()
        setupAdapters()
        setupListeners()
        playFluidEntranceAnimation()
        observeViewModel()
    }

    private fun initViews() {
        splashOverlay = findViewById(R.id.splashOverlay)
        splashContent = findViewById(R.id.splashContent)
        btnShieldToggle = findViewById(R.id.btnShieldToggle)
        tvProtectionStatus = findViewById(R.id.tvProtectionStatus)
        tvProtectionSubtext = findViewById(R.id.tvProtectionSubtext)
        tvLiveIpAddress = findViewById(R.id.tvLiveIpAddress)
        tvLiveLocation = findViewById(R.id.tvLiveLocation)
        tvRefreshIp = findViewById(R.id.tvRefreshIp)
        tvModeBadge = findViewById(R.id.tvModeBadge)
        cardServerSelector = findViewById(R.id.cardServerSelector)
        ivSelectedServerIcon = findViewById(R.id.ivSelectedServerIcon)
        tvSelectedServerName = findViewById(R.id.tvSelectedServerName)
        tvSelectedServerDesc = findViewById(R.id.tvSelectedServerDesc)

        // Compatibility views
        cardModeSelector = findViewById(R.id.cardModeSelector)
        tvCurrentModeName = findViewById(R.id.tvCurrentModeName)
        tvBlockedCounter = findViewById(R.id.tvBlockedCounter)
        tvDataSavedCounter = findViewById(R.id.tvDataSavedCounter)

        pageDashboard = findViewById(R.id.pageDashboard)
        pageFirewall = findViewById(R.id.pageFirewall)
        pageSettings = findViewById(R.id.pageSettings)
        pageAbout = findViewById(R.id.pageAbout)

        tabDashboard = findViewById(R.id.tabDashboard)
        tabFirewall = findViewById(R.id.tabFirewall)
        tabSettings = findViewById(R.id.tabSettings)
        tabAbout = findViewById(R.id.tabAbout)

        ivTabDashboard = findViewById(R.id.ivTabDashboard)
        ivTabFirewall = findViewById(R.id.ivTabFirewall)
        ivTabSettings = findViewById(R.id.ivTabSettings)
        ivTabAbout = findViewById(R.id.ivTabAbout)

        tvTabDashboard = findViewById(R.id.tvTabDashboard)
        tvTabFirewall = findViewById(R.id.tvTabFirewall)
        tvTabSettings = findViewById(R.id.tvTabSettings)
        tvTabAbout = findViewById(R.id.tvTabAbout)

        rvAppsList = findViewById(R.id.rvAppsList)
    }

    private fun setupAdapters() {
        appFirewallAdapter = AppFirewallAdapter { app, isBlocked ->
            viewModel.setAppBlocked(app, isBlocked)
        }
        rvAppsList.layoutManager = LinearLayoutManager(this)
        rvAppsList.adapter = appFirewallAdapter
    }

    private fun setupListeners() {
        btnShieldToggle.setOnClickListener {
            handleShieldClick()
        }

        tvRefreshIp.setOnClickListener {
            tvLiveIpAddress.text = "Memeriksa..."
            tvLiveLocation.text = "Mendeteksi rute..."
            viewModel.refreshPublicIp()
        }

        cardServerSelector.setOnClickListener {
            showServerSelectorDialog()
        }

        tabDashboard.setOnClickListener { switchTab(0) }
        tabFirewall.setOnClickListener { switchTab(1) }
        tabSettings.setOnClickListener { switchTab(2) }
        tabAbout.setOnClickListener { switchTab(3) }

        // Split Tunneling Configuration in Firewall Page
        findViewById<View>(R.id.btnOpenSplitTunnelConfig)?.setOnClickListener {
            startActivity(Intent(this, SplitTunnelActivity::class.java))
        }

        // Security Settings Navigation in pageSettings
        findViewById<View>(R.id.cardSettingKillSwitch)?.setOnClickListener {
            startActivity(Intent(this, AdvancedSettingsActivity::class.java))
        }

        findViewById<View>(R.id.cardSettingObfuscation)?.setOnClickListener {
            val container = (application as BPApplication).container
            val currentLevel = container.obfuscationPreferences.getObfuscationLevel()
            ObfuscationSettingsDialog(this, currentLevel) { newLevel ->
                container.obfuscationPreferences.setObfuscationLevel(newLevel)
                Toast.makeText(this, "Obfuskasi: ${newLevel.title}", Toast.LENGTH_SHORT).show()
            }.show()
        }

        findViewById<View>(R.id.cardSettingDnsShield)?.setOnClickListener {
            startActivity(Intent(this, DnsShieldActivity::class.java))
        }

        findViewById<View>(R.id.cardSettingSecurityAudit)?.setOnClickListener {
            startActivity(Intent(this, SecurityAuditActivity::class.java))
        }
    }

    private fun showServerSelectorDialog() {
        val dialog = android.app.Dialog(this).apply {
            requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_server_selector)
            window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.92).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val rvServers: RecyclerView = dialog.findViewById(R.id.rvServerLocations)
        val btnClose: View = dialog.findViewById(R.id.btnDismissDialog)

        rvServers.layoutManager = LinearLayoutManager(this)
        rvServers.adapter = ServerSelectorAdapter(
            servers = ServerLocation.values().toList(),
            selectedServer = viewModel.selectedLocation.value
        ) { selected ->
            viewModel.selectServerLocation(selected)
            dialog.dismiss()
            Toast.makeText(this, "Server diubah ke ${selected.countryName}", Toast.LENGTH_SHORT).show()
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun handleShieldClick() {
        if (viewModel.vpnState.value.isConnected) {
            viewModel.toggleVpn()
        } else {
            val intent = VpnService.prepare(this)
            if (intent != null) {
                vpnPermissionLauncher.launch(intent)
            } else {
                viewModel.toggleVpn()
            }
        }
    }

    private fun playFluidEntranceAnimation() {
        splashContent.scaleX = 0.88f
        splashContent.scaleY = 0.88f
        splashContent.alpha = 0f

        splashContent.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(500)
            .withEndAction {
                splashOverlay.postDelayed({
                    splashOverlay.animate()
                        .alpha(0f)
                        .scaleX(1.04f)
                        .scaleY(1.04f)
                        .setDuration(400)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                splashOverlay.visibility = View.GONE
                            }
                        })
                }, 600)
            }
            .start()
    }

    private fun switchTab(index: Int) {
        pageDashboard.visibility = if (index == 0) View.VISIBLE else View.GONE
        pageFirewall.visibility = if (index == 1) View.VISIBLE else View.GONE
        pageSettings.visibility = if (index == 2) View.VISIBLE else View.GONE
        pageAbout.visibility = if (index == 3) View.VISIBLE else View.GONE

        val activeColor = Color.parseColor("#F4F4F7")
        val inactiveColor = Color.parseColor("#686B80")

        ivTabDashboard.setColorFilter(if (index == 0) activeColor else inactiveColor)
        tvTabDashboard.setTextColor(if (index == 0) activeColor else inactiveColor)

        ivTabFirewall.setColorFilter(if (index == 1) activeColor else inactiveColor)
        tvTabFirewall.setTextColor(if (index == 1) activeColor else inactiveColor)

        ivTabSettings.setColorFilter(if (index == 2) activeColor else inactiveColor)
        tvTabSettings.setTextColor(if (index == 2) activeColor else inactiveColor)

        ivTabAbout.setColorFilter(if (index == 3) activeColor else inactiveColor)
        tvTabAbout.setTextColor(if (index == 3) activeColor else inactiveColor)

        if (index == 1) viewModel.loadInstalledApps()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.vpnState.collect { state ->
                        updateVpnStateUi(state)
                    }
                }

                launch {
                    viewModel.publicIpDetails.collect { details ->
                        if (details != null) {
                            tvLiveIpAddress.text = details.ip
                            val locationText = listOfNotNull(details.city, details.country, details.isp)
                                .joinToString(" • ")
                            tvLiveLocation.text = if (locationText.isNotEmpty()) locationText else "Jaringan Terenkripsi"
                        }
                    }
                }

                launch {
                    viewModel.trafficMetrics.collect { metrics ->
                        tvBlockedCounter.text = metrics.blockedQueries.toString()
                        tvDataSavedCounter.text = metrics.estimatedBytesSaved.formatDataSize()
                    }
                }

                launch {
                    viewModel.currentMode.collect { mode ->
                        tvCurrentModeName.text = mode.displayName
                    }
                }

                launch {
                    viewModel.installedApps.collect { apps ->
                        appFirewallAdapter.submitList(apps)
                    }
                }

                launch {
                    viewModel.selectedLocation.collect { loc ->
                        ivSelectedServerIcon.setImageResource(loc.iconResId)
                        tvSelectedServerName.text = loc.countryName
                        tvSelectedServerDesc.text = "${loc.cityName} • ${loc.description}"
                    }
                }
            }
        }
    }

    private fun updateVpnStateUi(state: VpnState) {
        when (state) {
            is VpnState.Connected -> {
                btnShieldToggle.setState(connected = true, connecting = false)
                tvProtectionStatus.text = getString(R.string.status_protected)
                tvProtectionStatus.setTextColor(Color.parseColor("#10B981"))
                tvProtectionSubtext.text = getString(R.string.connected_subtext)
                tvModeBadge.text = "ONLINE"
                tvModeBadge.setTextColor(Color.parseColor("#10B981"))
            }
            is VpnState.Connecting -> {
                btnShieldToggle.setState(connected = false, connecting = true)
                tvProtectionStatus.text = getString(R.string.status_connecting)
                tvProtectionStatus.setTextColor(Color.parseColor("#F59E0B"))
                tvProtectionSubtext.text = "Sedang menghubungkan ke server..."
                tvModeBadge.text = "CONNECTING"
                tvModeBadge.setTextColor(Color.parseColor("#F59E0B"))
            }
            is VpnState.Disconnected -> {
                btnShieldToggle.setState(connected = false, connecting = false)
                tvProtectionStatus.text = getString(R.string.status_unprotected)
                tvProtectionStatus.setTextColor(Color.parseColor("#686B80"))
                tvProtectionSubtext.text = getString(R.string.tap_to_protect)
                tvModeBadge.text = "OFFLINE"
                tvModeBadge.setTextColor(Color.parseColor("#686B80"))
            }
            is VpnState.Error -> {
                btnShieldToggle.setState(connected = false, connecting = false)
                tvProtectionStatus.text = "Gagal Terhubung"
                tvProtectionStatus.setTextColor(Color.parseColor("#EF4444"))
                tvProtectionSubtext.text = state.message
                tvModeBadge.text = "ERROR"
                tvModeBadge.setTextColor(Color.parseColor("#EF4444"))
            }
        }
    }
}
