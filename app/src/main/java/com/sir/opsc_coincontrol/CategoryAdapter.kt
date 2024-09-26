// adapters/CategoryAdapter.kt
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], onItemClick)
    }

    override fun getItemCount(): Int = categories.size

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)

        fun bind(category: CategoryClass, onItemClick: (CategoryClass) -> Unit) {
            tvCategoryName.text = category.categoryName
            itemView.setOnClickListener {
                onItemClick(category)
            }
        }
    }
}
