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
    private val transactions: List<TransactionsClass>
) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTransactionName: TextView = itemView.findViewById(R.id.tvTransactionName)
        private val tvTransactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
        private val tvTransactionDate: TextView = itemView.findViewById(R.id.tvTransactionDate)

        fun bind(transaction: TransactionsClass) {
            tvTransactionName.text = transaction.transaction_Name ?: "No Name"
            tvTransactionAmount.text = "Amount: $${transaction.transaction_Amount ?: 0.0}"
            tvTransactionDate.text = "Date: ${transaction.date ?: "Unknown"}"
        }
    }
}
