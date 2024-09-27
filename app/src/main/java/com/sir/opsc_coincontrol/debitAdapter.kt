package com.sir.opsc_coincontrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class debitAdapter (
    private val debitOrders: List<DebitOrderClass>

) : RecyclerView.Adapter<debitAdapter.DebitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.debit_item, parent, false)
        return DebitViewHolder(view)
    }

    override fun onBindViewHolder(holder: DebitViewHolder, position: Int) {
        holder.bind(debitOrders[position])
    }

    override fun getItemCount(): Int = debitOrders.size

    class DebitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDebitName: TextView = itemView.findViewById(R.id.txtDebitName)
        private val tvDebitAmount: TextView = itemView.findViewById(R.id.txtDebitAmount)
        private val tvDebitDate: TextView = itemView.findViewById(R.id.txtDebitDate)



        fun bind(debitOrder: DebitOrderClass) {
            tvDebitName.text = debitOrder.debit_Name ?: "No Name"
            tvDebitAmount.text = "Amount: R${debitOrder.debit_Amount?: 0.0}"
            tvDebitDate.text = "Date: ${debitOrder.debit_Date ?: "Unknown"}"
        }
    }
}