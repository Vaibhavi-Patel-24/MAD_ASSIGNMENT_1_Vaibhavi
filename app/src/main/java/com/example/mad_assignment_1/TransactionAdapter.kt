package com.example.mad_assignment_1

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.drawable.GradientDrawable
import android.util.Log


class TransactionAdapter(
    private var transactionList: List<Transaction>,
    private val emptyTextView: TextView ,
    private val totalExpenseTextView: TextView,
    private val incomeTextView: TextView

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

            val defaultColor = Color.parseColor("#D06BED")

            val iconColor: Int = when (transaction.iconColor) {
                null -> {
                    defaultColor // Use the default color if it's null
                }

                else -> {
                    transaction.iconColor // Use the provided color
                }
            }


            val gradientDrawable = GradientDrawable()
            gradientDrawable.shape = GradientDrawable.OVAL // Set the shape to a circle
            gradientDrawable.setColor(iconColor) // Set the dynamic color

            holder.icon.background = gradientDrawable
            holder.icon.setImageResource(transaction.iconResId) // Assuming you have icons
            holder.title.text = transaction.title

            val amountValue = transaction.amount.toDoubleOrNull() ?: 0.0

            holder.amount.text = if (amountValue < 0) {
                "- $${String.format("%.2f", -amountValue)}"
            } else {
                "$${String.format("%.2f", amountValue)}"
            }

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
