package com.sir.opsc_coincontrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sir.opsc_coincontrol.CategoryClass
import com.sir.opsc_coincontrol.R

class CategoryAdapter(
    var categories: List<CategoryClass>,  // Changed to var and MutableList
    private val onItemClick: (CategoryClass) -> Unit,
    private val onFavouriteClick: ((CategoryClass) -> Unit)? = null,
    private var onItemLongClickListener: ((CategoryClass) -> Unit)? = null,
    private val isEditable: Boolean = true
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category, onItemClick, onItemLongClickListener, onFavouriteClick, isEditable)
    }




    override fun getItemCount(): Int = categories.size

    // Method to set the long-click listener
    fun setOnItemLongClickListener(listener: (CategoryClass) -> Unit) {
        onItemLongClickListener = listener
    }

    // Method to return the current list of categories
    fun retrieveCategories(): List<CategoryClass> {
        return categories
    }

    // Method to update the adapter's categories list
    fun updateCategories(newCategories: List<CategoryClass>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val imgFavourite: ImageView = itemView.findViewById(R.id.imgFavourite)

        fun bind(
            category: CategoryClass,
            onItemClick: (CategoryClass) -> Unit,
            onItemLongClickListener: ((CategoryClass) -> Unit)?,
            onFavouriteClick: ((CategoryClass) -> Unit)?,
            isEditable: Boolean
        ) {
            tvCategoryName.text = category.categoryName ?: "No Name"

            val favoriteIconRes = if (category.isFavourite != true) {
                R.drawable.star_outline
            } else {
                R.drawable.star_full
            }
            imgFavourite.setImageResource(favoriteIconRes)

            if (isEditable && onFavouriteClick != null) {
                // Allow toggling favorite status
                imgFavourite.setOnClickListener {
                    onFavouriteClick(category)
                }
            } else {
                // Disable clicking on the favorite icon
                imgFavourite.setOnClickListener(null)
            }


            itemView.setOnClickListener {
                onItemClick(category)
            }

            itemView.setOnLongClickListener {
                onItemLongClickListener?.invoke(category)
                true
            }
        }
    }
}
