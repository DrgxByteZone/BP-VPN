package com.axel.mba.bpvpn.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.axel.mba.bpvpn.R
import com.axel.mba.bpvpn.core.model.ServerLocation

class ServerSelectorAdapter(
    private val servers: List<ServerLocation>,
    private var selectedServer: ServerLocation,
    private val onServerSelected: (ServerLocation) -> Unit
) : RecyclerView.Adapter<ServerSelectorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardServerItem: View = view.findViewById(R.id.cardServerItem)
        val ivItemServer: ImageView = view.findViewById(R.id.ivItemServer)
        val tvItemCountryName: TextView = view.findViewById(R.id.tvItemCountryName)
        val tvItemCityDesc: TextView = view.findViewById(R.id.tvItemCityDesc)
        val tvItemStatus: TextView = view.findViewById(R.id.tvItemStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_server_location, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val server = servers[position]
        holder.ivItemServer.setImageResource(server.iconResId)
        holder.tvItemCountryName.text = server.countryName
        holder.tvItemCityDesc.text = "${server.cityName} • ${server.description}"

        val isSelected = server == selectedServer
        holder.cardServerItem.setBackgroundResource(
            if (isSelected) R.drawable.bg_server_item_selected else R.drawable.bg_server_item_normal
        )
        holder.tvItemStatus.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.tvItemStatus.text = if (isSelected) "TERPILIH" else ""

        holder.cardServerItem.setOnClickListener {
            selectedServer = server
            notifyDataSetChanged()
            onServerSelected(server)
        }
    }

    override fun getItemCount(): Int = servers.size

    fun updateSelected(server: ServerLocation) {
        selectedServer = server
        notifyDataSetChanged()
    }
}
