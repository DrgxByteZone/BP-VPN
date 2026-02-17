package com.axel.mba.bpvpn.feature.splittunneling.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.feature.splittunneling.model.BypassAppInfo
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * Adapter for Split Tunneling app list.
 * Created by: Axel & M.B.A
 */
class SplitTunnelAppAdapter(
    private val onToggleBypass: (BypassAppInfo) -> Unit
) : ListAdapter<BypassAppInfo, SplitTunnelAppAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.ivAppIcon)
        val tvName: TextView = view.findViewById(R.id.tvAppName)
        val tvCategory: TextView = view.findViewById(R.id.tvAppCategory)
        val tvPackage: TextView = view.findViewById(R.id.tvPackageName)
        val switchBypass: SwitchMaterial = view.findViewById(R.id.switchBypass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_split_tunnel_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvName.text = item.appName
        holder.tvPackage.text = item.packageName
        holder.tvCategory.text = item.category.displayName

        if (item.icon != null) {
            holder.ivIcon.setImageDrawable(item.icon)
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_bp_panther_logo)
        }

        holder.switchBypass.setOnCheckedChangeListener(null)
        holder.switchBypass.isChecked = item.isBypassed
        holder.switchBypass.setOnCheckedChangeListener { _, _ ->
            onToggleBypass(item)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<BypassAppInfo>() {
        override fun areItemsTheSame(oldItem: BypassAppInfo, newItem: BypassAppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: BypassAppInfo, newItem: BypassAppInfo): Boolean {
            return oldItem.isBypassed == newItem.isBypassed && oldItem.appName == newItem.appName
        }
    }
}
