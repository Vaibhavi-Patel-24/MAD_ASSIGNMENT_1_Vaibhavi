package com.example.mad_assignment_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactionList: List<Transaction>,
    private val emptyTextView: TextView // Pass the TextView to handle visibility
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    // Define ViewHolder for Transaction item
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.icon_1)
        val title: TextView = view.findViewById(R.id.icon_text_1)
        val amount: TextView = view.findViewById(R.id.icon_text_2)
        val date: TextView = view.findViewById(R.id.icon_text_3)
    }

    // Define ViewHolder for Empty view
    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emptyMessage: TextView = view.findViewById(R.id.empty_text)
    }

    override fun getItemViewType(position: Int): Int {
        return if (transactionList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            // Inflate empty view layout
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.empty_transaction_view, parent, false)
            EmptyViewHolder(view)
        } else {
            // Inflate transaction item layout
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false)
            TransactionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TransactionViewHolder) {
            val transaction = transactionList[position]
            holder.icon.setImageResource(transaction.iconResId) // Assuming you have icons
            holder.title.text = transaction.title
            holder.amount.text = transaction.amount
            holder.date.text = transaction.date
        } else if (holder is EmptyViewHolder) {
            holder.emptyMessage.text = "No transactions available"
        }
    }

    override fun getItemCount(): Int {
        return if (transactionList.isEmpty()) 1 else transactionList.size
    }

    // Method to update the transaction list and manage empty view visibility
    fun updateTransactions(newTransactions: List<Transaction>) {
        transactionList = newTransactions
        notifyDataSetChanged()
        emptyTextView.visibility = if (transactionList.isEmpty()) View.VISIBLE else View.GONE
    }
}
