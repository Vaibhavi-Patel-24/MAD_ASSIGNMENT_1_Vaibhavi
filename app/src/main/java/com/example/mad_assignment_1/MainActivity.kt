package com.example.mad_assignment_1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mad_assignment_1.data.AppDatabase
import com.example.mad_assignment_1.ui.TransactionViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.mad_assignment_1.repository.TransactionRepository
import com.example.mad_assignment_1.ui.TransactionViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var emptyTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private val transactionList = mutableListOf<Transaction>()
    private lateinit var addTransactionLauncher: ActivityResultLauncher<Intent>
    private lateinit var inputText1: TextView
    private lateinit var totalExpenseTextView: TextView
    private lateinit var incomeTextView: TextView
    private lateinit var  transactionViewModel: TransactionViewModel



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
        inputText1 = findViewById(R.id.input_text_1)
        totalExpenseTextView = findViewById(R.id.card_6_text_2)
        incomeTextView = findViewById(R.id.card_5_text_2)

        // Retrieve and display the stored total balance
        val totalBalance = getTotalBalance()
        inputText1.text = "$ $totalBalance"

        // Initialize the TransactionAdapter with an empty list and the empty TextView
        transactionAdapter = TransactionAdapter(transactionList, emptyTextView,totalExpenseTextView,incomeTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionAdapter

        val transactionDao = AppDatabase.getDatabase(this).transactionDao()
        val repository = TransactionRepository(transactionDao)
        val factory = TransactionViewModelFactory(repository)
        transactionViewModel = ViewModelProvider(this, factory)[TransactionViewModel::class.java]


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

        val addIcon: ImageButton = findViewById(R.id.add_icon)
        addIcon.setOnClickListener {
            val intent = Intent(this, AddExpenses::class.java)
            addTransactionLauncher.launch(intent) // Use the launcher instead
        }



        // Load initial transaction data
        updateEmptyState()


        // Show the salary input prompt when the app opens
        val newEntryButton = findViewById<Button>(R.id.btnNewEntry)

        newEntryButton.setOnClickListener {
            showSalaryInputPrompt()
        }

        // Load initial transaction data from the database
        loadTransactionsFromDatabase()



        //bottom nav
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        val colorStateList = ContextCompat.getColorStateList(this, R.color.nav_item_icon_color)
        bottomNavigationView.itemIconTintList = colorStateList
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Stay on MainActivity for Home
                    true
                }

                R.id.nav_graph -> {
                    // Navigate to GraphActivity
                    val intent = Intent(this, Graph::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }
    private fun updateEmptyState() {
        if (transactionList.isEmpty()) {
            emptyTextView.text = "No transactions available" // Set the empty message
            emptyTextView.visibility = TextView.VISIBLE // Show the empty message
            recyclerView.visibility = RecyclerView.GONE // Hide the recycler view
            totalExpenseTextView.text = "$0.00"
        } else {
            emptyTextView.visibility = TextView.GONE // Hide the empty message
            recyclerView.visibility = RecyclerView.VISIBLE // Show the recycler view
            updateTotalExpense()
        }
    }

    private fun updateTotalExpense() {
        val totalExpense = transactionList.sumOf {
            it.amount.toDoubleOrNull() ?: 0.0
        }
        val formattedExpense = if (totalExpense < 0) {
            "- $${String.format("%.2f", -totalExpense)}" // Display as "- $100.00"
        } else {
            "$${String.format("%.2f", totalExpense)}"   // Display as "$100.00"
        }

        totalExpenseTextView.text = formattedExpense

        updateIncome()

    }

    private fun updateIncome() {
        // Fetch the current total balance and expense from their respective TextViews
        val totalBalance =inputText1.text.toString().replace("$", "").trim().toDoubleOrNull() ?: 0.0
        val totalExpense = totalExpenseTextView .text.toString().replace("$", "").replace("-", "").trim().toDoubleOrNull() ?: 0.0

        Log.d("MainActivity", "Total Balance: $totalBalance")
        Log.d("MainActivity", "Total Expense: $totalExpense")

        // Calculate income
        val income = totalBalance - totalExpense

        // Format the income with a "+" sign if it is positive
        val formattedIncome = if (income > 0) {
            "+$${String.format("%.2f", income)}"
        } else {
            "$${String.format("%.2f", income)}"
        }

        // Update the Income TextView
            incomeTextView.text = formattedIncome



        Log.d("MainActivity", "Formatted Income: $formattedIncome")

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
        transactionViewModel.insertTransaction(transaction)
        updateEmptyState() // Update the empty state after adding a new transaction
        updateTotalExpense()
        updateIncome()
    }

    private fun showSalaryInputPrompt() {
        val salaryInput = EditText(this)
        salaryInput.hint = "$20000"
        val dialog = AlertDialog.Builder(this)
            .setTitle("Total Balance")
            .setMessage("Please enter your total balance")
            .setView(salaryInput)
            .setPositiveButton("OK") { _, _ ->
                val salaryText = salaryInput.text.toString()
                val salary = salaryText.toDoubleOrNull()
                if (salary != null) {
                    saveTotalBalance(salary)
                    inputText1.text = "$ $salary"
                    updateIncome()
                } else {
                    inputText1.text = "$0.00"
                    Log.e("MainActivity", "Invalid input for salary")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun loadTransactionsFromDatabase() {
        transactionViewModel.getAllTransactions { transactions ->
            // Clear the existing list to prevent duplicates
            transactionList.clear()

            // Add all transactions fetched from the database
            transactionList.addAll(transactions)

            // Notify the adapter of the data change
            transactionAdapter.notifyDataSetChanged()

            // Update the UI based on the new data
            updateEmptyState()
        }
    }
    // Save Total Balance
    private fun saveTotalBalance(balance: Double) {
        val sharedPreferences = getSharedPreferences("BalancePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("totalBalance", balance.toFloat())
        editor.apply()
    }
    //Retrieve Total Balance
    private fun getTotalBalance(): Double {
        val sharedPreferences = getSharedPreferences("BalancePrefs", MODE_PRIVATE)
        return sharedPreferences.getFloat("totalBalance", 0.0f).toDouble()
    }


    companion object {
        const val ADD_TRANSACTION_REQUEST = 1 // Request code for adding transactions
    }
}
