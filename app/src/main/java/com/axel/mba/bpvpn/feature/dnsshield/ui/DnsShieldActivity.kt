package com.axel.mba.bpvpn.feature.dnsshield.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.axel.mba.bpvpn.BPApplication
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.feature.dnsshield.data.db.CustomDomainRuleDao
import com.axel.mba.bpvpn.feature.dnsshield.data.repository.DnsShieldRepositoryImpl
import com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile
import com.axel.mba.bpvpn.feature.dnsshield.ui.adapter.DnsRuleAdapter
import kotlinx.coroutines.launch

/**
 * Advanced DoH Shield & Custom Filtering Activity.
 * Created by: Axel & M.B.A
 */
class DnsShieldActivity : AppCompatActivity() {

    private lateinit var viewModel: DnsShieldViewModel
    private lateinit var adapter: DnsRuleAdapter

    private lateinit var btnBack: ImageView
    private lateinit var rgDnsProfiles: RadioGroup
    private lateinit var rbCfStandard: RadioButton
    private lateinit var rbCfSecurity: RadioButton
    private lateinit var rbCfFamily: RadioButton
    private lateinit var rbAdGuard: RadioButton
    private lateinit var rbQuad9: RadioButton
    private lateinit var rbGoogle: RadioButton
    private lateinit var etDomainRule: EditText
    private lateinit var btnAddRule: TextView
    private lateinit var rvCustomRules: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dns_shield)

        val container = (application as BPApplication).container
        val dao = CustomDomainRuleDao(container.dbHelper)
        val repository = DnsShieldRepositoryImpl(container.preferences, dao)
        viewModel = DnsShieldViewModel(repository)

        initViews()
        setupAdapter()
        setupListeners()
        observeState()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        rgDnsProfiles = findViewById(R.id.rgDnsProfiles)
        rbCfStandard = findViewById(R.id.rbCfStandard)
        rbCfSecurity = findViewById(R.id.rbCfSecurity)
        rbCfFamily = findViewById(R.id.rbCfFamily)
        rbAdGuard = findViewById(R.id.rbAdGuard)
        rbQuad9 = findViewById(R.id.rbQuad9)
        rbGoogle = findViewById(R.id.rbGoogle)
        etDomainRule = findViewById(R.id.etDomainRule)
        btnAddRule = findViewById(R.id.btnAddRule)
        rvCustomRules = findViewById(R.id.rvCustomRules)
    }

    private fun setupAdapter() {
        adapter = DnsRuleAdapter { rule ->
            viewModel.deleteRule(rule.domain)
            Toast.makeText(this, "Aturan '${rule.domain}' dihapus", Toast.LENGTH_SHORT).show()
        }
        rvCustomRules.layoutManager = LinearLayoutManager(this)
        rvCustomRules.adapter = adapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        rgDnsProfiles.setOnCheckedChangeListener { _, checkedId ->
            val profile = when (checkedId) {
                R.id.rbCfSecurity -> DnsProfile.CLOUDFLARE_SECURITY
                R.id.rbCfFamily -> DnsProfile.CLOUDFLARE_FAMILY
                R.id.rbAdGuard -> DnsProfile.ADGUARD_DNS
                R.id.rbQuad9 -> DnsProfile.QUAD9_SECURE
                R.id.rbGoogle -> DnsProfile.GOOGLE_PUBLIC
                else -> DnsProfile.CLOUDFLARE_STANDARD
            }
            viewModel.setProfile(profile)
            Toast.makeText(this, "Profil DoH diubah: ${profile.title}", Toast.LENGTH_SHORT).show()
        }

        btnAddRule.setOnClickListener {
            val domain = etDomainRule.text.toString().trim()
            if (domain.isNotEmpty()) {
                viewModel.addBlockRule(domain)
                etDomainRule.text.clear()
                Toast.makeText(this, "Domain '$domain' ditambahkan ke daftar blokir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.activeProfile.collect { profile ->
                when (profile) {
                    DnsProfile.CLOUDFLARE_STANDARD -> rbCfStandard.isChecked = true
                    DnsProfile.CLOUDFLARE_SECURITY -> rbCfSecurity.isChecked = true
                    DnsProfile.CLOUDFLARE_FAMILY -> rbCfFamily.isChecked = true
                    DnsProfile.ADGUARD_DNS -> rbAdGuard.isChecked = true
                    DnsProfile.QUAD9_SECURE -> rbQuad9.isChecked = true
                    DnsProfile.GOOGLE_PUBLIC -> rbGoogle.isChecked = true
                }
            }
        }

        lifecycleScope.launch {
            viewModel.customRules.collect { rules ->
                adapter.submitList(rules)
            }
        }
    }
}
