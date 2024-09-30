package com.sir.opsc_coincontrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sir.opsc_coincontrol.CategoryClass
import com.sir.opsc_coincontrol.R

class CategoryAdapter(
    private val categories: List<CategoryClass>,
    private val onItemClick: (CategoryClass) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    // Variable to hold the long-click listener
    private var onItemLongClickListener: ((CategoryClass) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category, onItemClick, onItemLongClickListener)
    }

    override fun getItemCount(): Int = categories.size

    // Method to set the long-click listener
    fun setOnItemLongClickListener(listener: (CategoryClass) -> Unit) {
        onItemLongClickListener = listener
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(
            category: CategoryClass,
            onItemClick: (CategoryClass) -> Unit,
            onItemLongClickListener: ((CategoryClass) -> Unit)?
        ) {
            tvCategoryName.text = category.categoryName

            itemView.setOnClickListener {
                onItemClick(category)
            }

            // Set up long-click listener
            itemView.setOnLongClickListener {
                onItemLongClickListener?.invoke(category)
                true // Return true to indicate the click was handled
            }
        }
    }
}
