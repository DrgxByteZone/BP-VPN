package com.axel.mba.bpvpn.feature.splittunneling.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.axel.mba.bpvpn.feature.splittunneling.data.db.SplitTunnelDao
import com.axel.mba.bpvpn.feature.splittunneling.data.repository.SplitTunnelRepositoryImpl
import com.axel.mba.bpvpn.feature.splittunneling.model.SplitTunnelPolicy
import com.axel.mba.bpvpn.feature.splittunneling.ui.adapter.SplitTunnelAppAdapter
import kotlinx.coroutines.launch

/**
 * Split Tunneling configuration Activity.
 * Created by: Axel & M.B.A
 */
class SplitTunnelActivity : AppCompatActivity() {

    private lateinit var viewModel: SplitTunnelViewModel
    private lateinit var adapter: SplitTunnelAppAdapter

    private lateinit var btnBack: ImageView
    private lateinit var btnAutoBanking: TextView
    private lateinit var rgPolicy: RadioGroup
    private lateinit var rbAllApps: RadioButton
    private lateinit var rbBypassSelected: RadioButton
    private lateinit var rbOnlySelected: RadioButton
    private lateinit var etSearchApp: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var rvSplitApps: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_tunnel)

        val container = (application as BPApplication).container
        val splitDao = SplitTunnelDao(container.dbHelper)
        val splitRepo = SplitTunnelRepositoryImpl(this, splitDao, container.preferences)
        viewModel = SplitTunnelViewModel(splitRepo)

        initViews()
        setupAdapter()
        setupListeners()
        observeState()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnAutoBanking = findViewById(R.id.btnAutoBanking)
        rgPolicy = findViewById(R.id.rgPolicy)
        rbAllApps = findViewById(R.id.rbAllApps)
        rbBypassSelected = findViewById(R.id.rbBypassSelected)
        rbOnlySelected = findViewById(R.id.rbOnlySelected)
        etSearchApp = findViewById(R.id.etSearchApp)
        progressBar = findViewById(R.id.progressBar)
        rvSplitApps = findViewById(R.id.rvSplitApps)
    }

    private fun setupAdapter() {
        adapter = SplitTunnelAppAdapter { app ->
            viewModel.toggleAppBypass(app)
        }
        rvSplitApps.layoutManager = LinearLayoutManager(this)
        rvSplitApps.adapter = adapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnAutoBanking.setOnClickListener {
            viewModel.applyBankingPreset { count ->
                Toast.makeText(
                    this,
                    "Berhasil mendeteksi & bypass $count aplikasi perbankan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        rgPolicy.setOnCheckedChangeListener { _, checkedId ->
            val selected = when (checkedId) {
                R.id.rbBypassSelected -> SplitTunnelPolicy.BYPASS_SELECTED
                R.id.rbOnlySelected -> SplitTunnelPolicy.ONLY_SELECTED
                else -> SplitTunnelPolicy.ALL_APPS
            }
            viewModel.setPolicy(selected)
        }

        etSearchApp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterList(query: String) {
        val fullList = viewModel.appList.value
        if (query.isBlank()) {
            adapter.submitList(fullList)
        } else {
            val q = query.lowercase().trim()
            val filtered = fullList.filter {
                it.appName.lowercase().contains(q) || it.packageName.lowercase().contains(q)
            }
            adapter.submitList(filtered)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.policy.collect { policy ->
                when (policy) {
                    SplitTunnelPolicy.ALL_APPS -> rbAllApps.isChecked = true
                    SplitTunnelPolicy.BYPASS_SELECTED -> rbBypassSelected.isChecked = true
                    SplitTunnelPolicy.ONLY_SELECTED -> rbOnlySelected.isChecked = true
                }
            }
        }

        lifecycleScope.launch {
            viewModel.appList.collect { list ->
                adapter.submitList(list)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { loading ->
                progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }
    }
}
