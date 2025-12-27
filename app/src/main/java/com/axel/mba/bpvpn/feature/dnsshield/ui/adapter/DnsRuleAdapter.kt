package com.axel.mba.bpvpn.feature.dnsshield.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.feature.dnsshield.model.CustomDomainRule
import com.axel.mba.bpvpn.feature.dnsshield.model.DomainRuleAction

/**
 * Adapter for custom domain filtering rules.
 * Created by: Axel & M.B.A
 */
class DnsRuleAdapter(
    private val onDelete: (CustomDomainRule) -> Unit
) : ListAdapter<CustomDomainRule, DnsRuleAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAction: TextView = view.findViewById(R.id.tvRuleAction)
        val tvDomain: TextView = view.findViewById(R.id.tvRuleDomain)
        val tvNote: TextView = view.findViewById(R.id.tvRuleNote)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteRule)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dns_rule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvDomain.text = item.domain
        holder.tvNote.text = item.note.ifEmpty { "Aturan filter kustom" }
        if (item.action == DomainRuleAction.BLOCK) {
            holder.tvAction.text = "BLOKIR"
            holder.tvAction.setTextColor(android.graphics.Color.parseColor("#EF4444"))
            holder.tvAction.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#2D1518"))
        } else {
            holder.tvAction.text = "BYPASS"
            holder.tvAction.setTextColor(android.graphics.Color.parseColor("#10B981"))
            holder.tvAction.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#142921"))
        }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CustomDomainRule>() {
        override fun areItemsTheSame(oldItem: CustomDomainRule, newItem: CustomDomainRule): Boolean {
            return oldItem.domain == newItem.domain
        }

        override fun areContentsTheSame(oldItem: CustomDomainRule, newItem: CustomDomainRule): Boolean {
            return oldItem == newItem
        }
    }
}
