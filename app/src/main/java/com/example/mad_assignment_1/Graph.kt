package com.example.mad_assignment_1

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import com.google.android.material.bottomnavigation.BottomNavigationView





class Graph : AppCompatActivity() {




    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_graph)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val transactions = intent.getParcelableArrayListExtra<Transaction>("transactions") ?: arrayListOf()
        Log.d("GraphActivity", "Transactions: $transactions")


        setupBarChart(transactions)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_bar)

        val colorStateList = ContextCompat.getColorStateList(this, R.color.nav_item_icon_color)
        bottomNavigationView.itemIconTintList = colorStateList
        bottomNavigationView.selectedItemId = R.id.nav_graph
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Navigate back to MainActivity for Home
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_graph -> {
                    // Stay in GraphActivity
                    true
                }
                else -> false
            }
        }
    }
    private fun setupBarChart(transactions: List<Transaction>) {
        val barChart = findViewById<BarChart>(R.id.barChart)

        if (transactions.isEmpty()) {
            Log.d("GraphActivity", "No data to display on the BarChart!")
            return
        }

        // Create BarEntries from the transactions list
        val barEntries = transactions.mapIndexed { index, transaction ->
            BarEntry(index.toFloat(), transaction.amount.toFloat()) // Use index for X-axis, amount for Y-axis
        }

        // Generate colors based on the number of entries
        val colors = generateBarColors(barEntries.size)

        // Setup BarDataSet
        val dataSet = BarDataSet(barEntries, "Transactions").apply {
            valueTextSize = 14f
            valueTextColor = Color.BLACK
            this.colors = colors
        }

        // Create BarData and set it on the BarChart
        val barData = BarData(dataSet).apply {
            barWidth = 0.3f // Set bar width; adjust for visual appearance
        }
        barChart.data = barData

        // Configure BarChart appearance
        barChart.apply {
            description.isEnabled = false
            axisLeft.textColor = Color.BLACK
            axisRight.isEnabled = false
            setFitBars(true) // Makes the bars fit within the X-axis
            xAxis.apply {
                setPosition(XAxis.XAxisPosition.BOTTOM)
                textColor = Color.BLACK
                granularity = 1f // Ensure labels are spaced correctly
                valueFormatter = XAxisValueFormatter(transactions) // Use custom formatter
            }
            legend.isEnabled = false
            extraBottomOffset = 10f // Adjust spacing for X-axis labels if needed
        }

        // Setup Legend
        val legend = barChart.legend
        legend.apply {
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(false)
            textSize = 12f
        }

        // Refresh the chart
        barChart.notifyDataSetChanged()
        barChart.invalidate() // Refresh the chart
    }

    private fun generateBarColors(size: Int): List<Int> {
        val baseColors = listOf(
            Color.BLUE, Color.rgb(254, 153, 0),
            Color.MAGENTA,Color.GRAY, Color.DKGRAY, Color.LTGRAY,Color.CYAN,
        )

        // Repeat the base colors list if there are more entries than the base color list
        val colors = mutableListOf<Int>()
        while (colors.size < size) {
            colors.addAll(baseColors)
        }

        return colors.take(size) // Ensure the list has the exact number of colors needed
    }


}

class XAxisValueFormatter(private val transactions: List<Transaction>) : com.github.mikephil.charting.formatter.IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String? {
        val index = value.toInt()
        return if (index in transactions.indices) {
            transactions[index].title  // Use transaction title for X-axis labels
        } else {
            ""
        }
    }
}