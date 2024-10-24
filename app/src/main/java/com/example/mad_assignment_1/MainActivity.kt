package com.example.mad_assignment_1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var emptyTextView: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        emptyTextView = findViewById(R.id.empty_text)
        recyclerView = findViewById(R.id.recyclerview)

        // Initialize the TransactionAdapter with an empty list and the empty TextView
        transactionAdapter = TransactionAdapter(mutableListOf(), emptyTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionAdapter

        // Load initial transaction data
        updateTransactionList()
    }

    private fun updateTransactionList() {
        val transactionList = listOf(
            Transaction(R.drawable.fast_food_1, "Food", "-$40.00", "Today"),
            Transaction(R.drawable.airplane_1, "Travel", "-80.00", "Yesterday"),
            Transaction(R.drawable.shopping_1, "Shopping", "-$18.00", "Last Week")
        )

        // Update the adapter with new transaction data
        transactionAdapter.updateTransactions(transactionList)
    }
}
