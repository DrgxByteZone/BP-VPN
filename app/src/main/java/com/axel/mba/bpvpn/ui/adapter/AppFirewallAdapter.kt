package com.axel.mba.bpvpn.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.core.model.AppNetworkInfo

class AppFirewallAdapter(
    private val onToggleBlocked: (app: AppNetworkInfo, isBlocked: Boolean) -> Unit
) : RecyclerView.Adapter<AppFirewallAdapter.ViewHolder>() {

    private var items: List<AppNetworkInfo> = emptyList()

    fun submitList(newItems: List<AppNetworkInfo>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_firewall, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAppIcon: ImageView = itemView.findViewById(R.id.ivAppIcon)
        private val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
        private val tvPackageName: TextView = itemView.findViewById(R.id.tvPackageName)
        private val switchBlock: SwitchCompat = itemView.findViewById(R.id.switchBlock)

        fun bind(app: AppNetworkInfo) {
            tvAppName.text = app.appName
            tvPackageName.text = app.packageName
            if (app.icon != null) {
                ivAppIcon.setImageDrawable(app.icon)
            } else {
                ivAppIcon.setImageResource(R.drawable.ic_bp_panther_logo)
            }

            switchBlock.setOnCheckedChangeListener(null)
            switchBlock.isChecked = !app.isInternetBlocked // checked means allowed

            switchBlock.setOnCheckedChangeListener { _, isAllowed ->
                onToggleBlocked(app, !isAllowed)
            }
        }
    }
}
