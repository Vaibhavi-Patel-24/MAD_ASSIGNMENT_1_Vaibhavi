package com.example.mad_assignment_1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class IconSpinnerAdapter(private val context: Context, private val icons: Array<Int>) : BaseAdapter() {
    override fun getCount(): Int = icons.size

    override fun getItem(position: Int): Any = icons[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.spinner_item_icon, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.icon)
        imageView.setImageResource(icons[position])
        return view
    }
}
