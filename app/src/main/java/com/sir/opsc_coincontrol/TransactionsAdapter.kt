// adapters/TransactionAdapter.kt
package com.sir.opsc_coincontrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sir.opsc_coincontrol.R
import com.sir.opsc_coincontrol.TransactionsClass

class TransactionsAdapter(
    private val transactions: List<TransactionsClass>,
    private val onItemClick: (TransactionsClass) -> Unit
) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    // Listener for long-click events
    private var onItemLongClickListener: ((TransactionsClass) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position], onItemClick, onItemLongClickListener)
    }

    override fun getItemCount(): Int = transactions.size

    // Method to set the long-click listener
    fun setOnItemLongClickListener(listener: (TransactionsClass) -> Unit) {
        onItemLongClickListener = listener
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTransactionName: TextView = itemView.findViewById(R.id.tvTransactionName)
        private val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        private val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)

        fun bind(
            transaction: TransactionsClass,
            onItemClick: (TransactionsClass) -> Unit,
            onItemLongClickListener: ((TransactionsClass) -> Unit)?
        ) {
            tvTransactionName.text = transaction.transaction_Name ?: "No Name"
            tvTransactionAmount.text = "Amount: R${transaction.transaction_Amount ?: 0.0}"
            tvTransactionDate.text = "Date: ${transaction.date ?: "Unknown"}"

            itemView.setOnClickListener {
                onItemClick(transaction)
            }

            // Set the long-click listener
            itemView.setOnLongClickListener {
                onItemLongClickListener?.invoke(transaction)
                true // Return true to indicate the long-click event was handled
            }
        }
    }
}
