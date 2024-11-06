package com.example.mad_assignment_1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var emptyTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private val transactionList = mutableListOf<Transaction>()
    private lateinit var addTransactionLauncher: ActivityResultLauncher<Intent>


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
        transactionAdapter = TransactionAdapter(transactionList, emptyTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionAdapter

        // Initialize the ActivityResultLaunche
        addTransactionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val transaction = data?.getParcelableExtra<Transaction>("transaction")
                if (transaction != null) {
                    addTransaction(transaction)
                } else {
                    Log.e("MainActivity", "Transaction data is null")
                }
            }
        }

        // Navigate to the add_expenses activity
        val addIcon: ImageButton = findViewById(R.id.add_icon)
        addIcon.setOnClickListener {
            val intent = Intent(this, AddExpenses::class.java)
            addTransactionLauncher.launch(intent) // Use the launcher instead
        }



        // Load initial transaction data
        updateEmptyState()
    }
    private fun updateEmptyState() {
        if (transactionList.isEmpty()) {
            emptyTextView.text = "No transactions available" // Set the empty message
            emptyTextView.visibility = TextView.VISIBLE // Show the empty message
            recyclerView.visibility = RecyclerView.GONE // Hide the recycler view
        } else {
            emptyTextView.visibility = TextView.GONE // Hide the empty message
            recyclerView.visibility = RecyclerView.VISIBLE // Show the recycler view
        }
    }

    // This method is called when the AddExpenses activity returns with a new transaction
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_TRANSACTION_REQUEST && resultCode == RESULT_OK) {
            val transaction = data?.getParcelableExtra("transaction", Transaction::class.java)
            if (transaction != null) {
                addTransaction(transaction)
            } else {
                Log.e("MainActivity", "Transaction data is null")
            }
        }

    }

    // Function to add a transaction to the list and notify the adapter
    private fun addTransaction(transaction: Transaction) {
        transactionList.add(transaction)
        transactionAdapter.notifyItemInserted(transactionList.size - 1) // Notify adapter of new item
        updateEmptyState() // Update the empty state after adding a new transaction
    }

    companion object {
        const val ADD_TRANSACTION_REQUEST = 1 // Request code for adding transactions
    }
}
