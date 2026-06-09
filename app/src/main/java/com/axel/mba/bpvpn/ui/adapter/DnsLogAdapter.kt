package com.axel.mba.bpvpn.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.core.common.formatTime
import com.axel.mba.bpvpn.core.model.DnsLogRecord

class DnsLogAdapter : RecyclerView.Adapter<DnsLogAdapter.ViewHolder>() {

    private var items: List<DnsLogRecord> = emptyList()

    fun submitList(newItems: List<DnsLogRecord>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun prependItem(item: DnsLogRecord) {
        items = listOf(item) + items.take(199)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dns_log, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDomain: TextView = itemView.findViewById(R.id.tvDomain)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        private val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)

        fun bind(record: DnsLogRecord) {
            tvDomain.text = record.domain
            tvTime.text = record.timestamp.formatTime()

            if (record.isBlocked) {
                tvStatusBadge.text = "BLOCKED"
                tvStatusBadge.setTextColor(Color.parseColor("#EF4444")) // Flat Crimson
                val category = record.threatCategory?.label ?: "Ad / Tracker"
                tvDetails.text = "$category • 0.0.0.0"
                tvDetails.setTextColor(Color.parseColor("#EF4444"))
            } else {
                tvStatusBadge.text = "ALLOWED"
                tvStatusBadge.setTextColor(Color.parseColor("#10B981")) // Flat Emerald
                tvDetails.text = "Encrypted Route • ${record.queryType}"
                tvDetails.setTextColor(Color.parseColor("#A4A4AF"))
            }
        }
    }
}
