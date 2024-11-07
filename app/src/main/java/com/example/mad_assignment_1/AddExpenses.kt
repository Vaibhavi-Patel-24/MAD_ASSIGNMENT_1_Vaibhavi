package com.example.mad_assignment_1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import yuku.ambilwarna.AmbilWarnaDialog
import android.app.DatePickerDialog
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class AddExpenses : AppCompatActivity() {
    private lateinit var categorySpinner: Spinner
    private lateinit var categoryEditText: EditText
    private var currentColor: Int = Color.parseColor("#D06BED")
    private lateinit var mainActivity: MainActivity
    private lateinit var dateCard: MaterialCardView
    private lateinit var dateTextView: TextView

    private val icons = arrayOf(
        R.drawable.agriculture,
        R.drawable.airplane,
        R.drawable.fast_food,
        R.drawable.home,
        R.drawable.healthcare,
        R.drawable.shopping,
        R.drawable.mortarboard
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_expenses)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //addExpenses
        val enterExpenses: EditText = findViewById(R.id.enter_expenses)
        val expenseText = enterExpenses.text.toString()
        if (expenseText.isEmpty()) {
            Toast.makeText(this, "Please enter an expense amount", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Expense: $expenseText", Toast.LENGTH_SHORT).show()
        }

        //create_category
        //name
        categoryEditText = findViewById(R.id.enter_category_name)
        val enteredCategoryName = categoryEditText.text.toString()


        //spinner
        categorySpinner = findViewById(R.id.category_spinner)




        val adapter = IconSpinnerAdapter(this, icons)
        categorySpinner.adapter = adapter

        //color
        val colorCard = findViewById<MaterialCardView>(R.id.color_1)
        colorCard.setOnClickListener {
            openColorPicker(colorCard) // Call the function to open the color picker
        }

        //date
        dateCard = findViewById(R.id.date)
        dateTextView = dateCard.findViewById(R.id.date_text)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDate = "$day/${month + 1}/$year"
        dateTextView.text = currentDate
        dateCard.setOnClickListener {
            openDatePicker()
        }

        // Save button
        val saveButton: Button = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            saveTransaction()
        }




    }

    private fun openColorPicker(colorCard: MaterialCardView) {
        // Create the AmbilWarna color picker dialog
        val colorPicker =
            AmbilWarnaDialog(this, currentColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onCancel(dialog: AmbilWarnaDialog) {
                    // Handle the cancel event
                    Toast.makeText(this@AddExpenses, "Color selection canceled", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    // Handle the OK event
                    currentColor = color // Update the current color
                    // Update the card's background color with the selected color
                    colorCard.setCardBackgroundColor(currentColor)
                    Toast.makeText(
                        this@AddExpenses,
                        "Color selected: #${Integer.toHexString(color)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        colorPicker.show()
    }

    private fun openDatePicker() {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Update the TextView with the selected date
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            dateTextView.text = selectedDate
        }, year, month, day)

        datePickerDialog.show()
    }


    private fun saveTransaction() {
        val expenseEditText: EditText = findViewById(R.id.enter_expenses)
        val expenseText = expenseEditText.text.toString()
        val enteredCategoryName = categoryEditText.text.toString()
        val selectedIconPosition = categorySpinner.selectedItemPosition // Get the selected icon position
        val selectedIcon = icons[selectedIconPosition] // Assuming `icons` is an array of drawable resource IDs

        if (expenseText.isEmpty() || enteredCategoryName.isEmpty()) {
            Toast.makeText(this, "Please enter an expense amount and category name", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the new transaction object with the selected color
        val newTransaction = Transaction(
            selectedIcon,
            enteredCategoryName,
            "-$expenseText",
            dateTextView.text.toString(),
            currentColor // Pass the current color
        )

        // Prepare the result intent
        val resultIntent = Intent().apply {
            putExtra("transaction", newTransaction)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
        Log.d("AddExpenses", "Transaction saved: $newTransaction")
    }


}