package com.axel.mba.bpvpn.feature.securityaudit.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.axel.mba.bpvpn.BPApplication
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.feature.securityaudit.data.db.SecurityAuditDao
import com.axel.mba.bpvpn.feature.securityaudit.data.repository.SecurityAuditRepositoryImpl
import com.axel.mba.bpvpn.feature.securityaudit.model.LeakSeverity
import com.axel.mba.bpvpn.feature.securityaudit.ui.view.SecurityScoreMeterView
import kotlinx.coroutines.launch

/**
 * Security Auditor Activity.
 * Created by: Axel & M.B.A
 */
class SecurityAuditActivity : AppCompatActivity() {

    private lateinit var viewModel: SecurityAuditViewModel

    private lateinit var btnBack: ImageView
    private lateinit var scoreMeter: SecurityScoreMeterView
    private lateinit var tvDnsLeakStatus: TextView
    private lateinit var tvWebRtcStatus: TextView
    private lateinit var tvIpv6Status: TextView
    private lateinit var tvAuditIp: TextView
    private lateinit var tvAuditAsn: TextView
    private lateinit var btnRunAudit: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_audit)

        val container = (application as BPApplication).container
        val dao = SecurityAuditDao(container.dbHelper)
        val repository = SecurityAuditRepositoryImpl(dao)
        viewModel = SecurityAuditViewModel(repository)

        initViews()
        setupListeners()
        observeState()

        // Auto trigger audit on open
        viewModel.runAudit("162.159.192.1", "Cloudflare WARP Anycast")
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        scoreMeter = findViewById(R.id.scoreMeter)
        tvDnsLeakStatus = findViewById(R.id.tvDnsLeakStatus)
        tvWebRtcStatus = findViewById(R.id.tvWebRtcStatus)
        tvIpv6Status = findViewById(R.id.tvIpv6Status)
        tvAuditIp = findViewById(R.id.tvAuditIp)
        tvAuditAsn = findViewById(R.id.tvAuditAsn)
        btnRunAudit = findViewById(R.id.btnRunAudit)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnRunAudit.setOnClickListener {
            viewModel.runAudit("162.159.192.1", "Cloudflare WARP Anycast")
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.report.collect { report ->
                scoreMeter.setScore(report.overallScore, report.cqi.rating)

                applyStatusBadge(tvDnsLeakStatus, report.dnsLeakStatus)
                applyStatusBadge(tvWebRtcStatus, report.webRtcLeakStatus)
                applyStatusBadge(tvIpv6Status, report.ipv6LeakStatus)

                tvAuditIp.text = "IP: ${report.ipInfo.ip}"
                tvAuditAsn.text = "${report.ipInfo.asn} • ${report.ipInfo.asOrganization}"
            }
        }

        lifecycleScope.launch {
            viewModel.isAuditing.collect { auditing ->
                btnRunAudit.isEnabled = !auditing
                btnRunAudit.text = if (auditing) "Sedang Memeriksa..." else "Jalankan Audit Keamanan"
            }
        }
    }

    private fun applyStatusBadge(textView: TextView, status: LeakSeverity) {
        textView.text = status.title
        when (status) {
            LeakSeverity.SECURE -> {
                textView.setTextColor(Color.parseColor("#10B981"))
                textView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#142921"))
            }
            LeakSeverity.WARNING -> {
                textView.setTextColor(Color.parseColor("#F59E0B"))
                textView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2C2212"))
            }
            LeakSeverity.CRITICAL -> {
                textView.setTextColor(Color.parseColor("#EF4444"))
                textView.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2D1518"))
            }
        }
    }
}
