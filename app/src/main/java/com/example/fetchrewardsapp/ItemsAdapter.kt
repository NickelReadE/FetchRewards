
package com.example.fetchrewardsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsAdapter(private val data: List<Pair<Int, List<Item>>>) :
    RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listId: TextView = view.findViewById(R.id.listId)
        val itemName: TextView = view.findViewById(R.id.itemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (listId, items) = data[position]
        holder.listId.text = "List ID: $listId"
        holder.itemName.text = items.joinToString("\n") { "• ${it.name}" }
    }

    override fun getItemCount(): Int = data.size
}
