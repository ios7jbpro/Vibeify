package com.ios7.vibeify

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class DeveloperAdapter(
    private val context: Context,
    private val developerList: List<Developer>,
) : BaseAdapter() {
    override fun getCount(): Int = developerList.size

    override fun getItem(position: Int): Any = developerList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context).inflate(R.layout.devs, parent, false)

        val developer = developerList[position]
        val nameTextView = view.findViewById<TextView>(R.id.textview4)
        val imageView = view.findViewById<ImageView>(R.id.circleimageview1)

        nameTextView.text = developer.name
        Picasso.get().load(developer.imageUrl).into(imageView)

        return view
    }
}
