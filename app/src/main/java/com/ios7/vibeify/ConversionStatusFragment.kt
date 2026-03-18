package com.ios7.vibeify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ConversionStatusFragment : Fragment() {

    private lateinit var statusList: ListView
    private val activities = listOf(
        ActivityStatus("MainActivity", "Kotlin (MainKotlinActivity)", "✅"),
        ActivityStatus("WallpapersFragmentActivity", "Kotlin (WallpapersActivity)", "✅"),
        ActivityStatus("WalldownloadActivity", "Kotlin Beta (WalldownloadkotlinActivity)", "🛠️"),
        ActivityStatus("SettingsDialogFragmentActivity", "Java", "❌"),
        ActivityStatus("SetupActivity1", "Java", "❌"),
        ActivityStatus("AppRestarter", "Kotlin (AppRestarterKotlin)", "✅"),
        ActivityStatus("ManualDebugEnabler", "Java", "❌"),
        ActivityStatus("NoInternet", "Java", "❌"),
        ActivityStatus("CropWallpaper", "Java", "❌"),
        ActivityStatus("Setwall1Activity", "Java", "❌"),
        ActivityStatus("Setwall2Activity", "Java", "❌")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_conversion_status, container, false)
        statusList = view.findViewById(R.id.status_list)
        statusList.adapter = StatusAdapter()
        return view
    }

    data class ActivityStatus(val name: String, val status: String, val icon: String)

    inner class StatusAdapter : BaseAdapter() {
        override fun getCount(): Int = activities.size
        override fun getItem(position: Int): Any = activities[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_conversion_status, parent, false)
            val textName = view.findViewById<TextView>(R.id.textName)
            val textStatus = view.findViewById<TextView>(R.id.textStatus)

            val item = activities[position]
            textName.text = "${item.icon} ${item.name}"
            textStatus.text = item.status

            return view
        }
    }
}
