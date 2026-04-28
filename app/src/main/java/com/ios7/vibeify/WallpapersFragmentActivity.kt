package com.ios7.vibeify

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ios7.vibeify.MyClasses.EzBlur
import com.ios7.vibeify.MyClasses.EzFade
import com.ios7.vibeify.MyClasses.EzTimer
import java.util.Timer
import java.util.TimerTask

class WallpapersFragmentActivity : Fragment() {
    private var isGridVisible = false
    private val timer = Timer()

    private var walllist = ArrayList<HashMap<String, Any>>()
    private var categorylist = ArrayList<HashMap<String, Any>>()

    private lateinit var rootlinear: LinearLayout
    private lateinit var linear1: LinearLayout
    private lateinit var tempcardview: CardView
    private lateinit var listview1: ListView
    private lateinit var gridlinear: LinearLayout
    private lateinit var linear2: LinearLayout
    private lateinit var gridview1: GridView
    private lateinit var textview1: TextView
    private lateinit var textloading: TextView
    private lateinit var linearloading: LinearLayout
    private lateinit var gridRounderLayout: LinearLayout
    private lateinit var gridfadelinear: LinearLayout
    private lateinit var gridloading: LinearLayout

    private lateinit var fetchwalljson: RequestNetwork
    private var fetchwalljsonRequestListener: RequestNetwork.RequestListener? = null
    private lateinit var selectedItemList: SharedPreferences
    private val launchWallPreview = Intent()
    private lateinit var config: SharedPreferences
    private lateinit var temporaryCache: SharedPreferences
    private var loadDelay: TimerTask? = null
    private lateinit var fetchcategoryjson: RequestNetwork
    private var fetchcategoryjsonRequestListener: RequestNetwork.RequestListener? = null
    private var combinedOutput: String? = null

    private fun noConnectionTerminator() {
        val nointernet = Intent()
        val hostActivity = activity ?: return
        nointernet.setClass(hostActivity, NoInternet::class.java)
        startActivity(nointernet)
        hostActivity.finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.wallpapers_fragment, container, false)
        initialize(savedInstanceState, view)
        initializeLogic()
        return view
    }

    private fun initialize(savedInstanceState: Bundle?, view: View) {
        rootlinear = view.findViewById(R.id.rootlinear)
        linear1 = view.findViewById(R.id.linear1)
        tempcardview = view.findViewById(R.id.tempcardview)
        listview1 = view.findViewById(R.id.listview1)
        gridlinear = view.findViewById(R.id.gridlinear)
        gridfadelinear = view.findViewById(R.id.gridfadelinear)
        gridloading = view.findViewById(R.id.gridloading)
        linear2 = view.findViewById(R.id.linear2)
        gridview1 = view.findViewById(R.id.gridview1)
        textview1 = view.findViewById(R.id.textview1)
        textloading = view.findViewById(R.id.textloading)
        linearloading = view.findViewById(R.id.linearloading)
        textloading.visibility = View.GONE
        gridRounderLayout = view.findViewById(R.id.gridRounderLayout)
        gridRounderLayout.clipToOutline = true
        gridfadelinear.visibility = View.GONE

        fetchwalljson = RequestNetwork(requireActivity())
        selectedItemList = requireContext().getSharedPreferences("selectedItemList", Activity.MODE_PRIVATE)
        config = requireContext().getSharedPreferences("config", Activity.MODE_PRIVATE)
        temporaryCache = requireContext().getSharedPreferences("temporaryCache", Activity.MODE_PRIVATE)
        fetchcategoryjson = RequestNetwork(requireActivity())

        EzTimer.runWithDelay(5000) {
            textloading.visibility = View.VISIBLE
        }

        listview1.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val jsonPath = categorylist[position]["json"].toString()
                fetchwalljson.startRequestNetwork(
                    RequestNetworkController.GET,
                    config.getString("repo", "") + jsonPath,
                    "",
                    fetchwalljsonRequestListener,
                )
                combinedOutput = config.getString("repo", "") + jsonPath
                temporaryCache.edit().putString("directrepo", jsonPath).apply()
                gridlinear.visibility = View.GONE
                gridfadelinear.visibility = View.VISIBLE
                listview1.visibility = View.GONE
                gridlinear.alpha = 0f
                gridfadelinear.alpha = 0f
                isGridVisible = true
                config.edit().putString("fragmentCanExit", "0").apply()
                config.edit().putString("categoryName", categorylist[position]["category"].toString()).apply()

                if (config.getString("disableanims", "") == "1") {
                    gridfadelinear.visibility = View.VISIBLE
                    gridlinear.visibility = View.GONE
                    gridloading.visibility = View.VISIBLE
                    listview1.visibility = View.GONE
                    gridlinear.alpha = 1f
                    gridfadelinear.alpha = 1f
                    listview1.alpha = 1f
                } else {
                    EzFade.fadeIn(gridfadelinear, 500)
                }
            }

        val handler = Handler(Looper.getMainLooper())
        val runnable =
            object : Runnable {
                override fun run() {
                    if (config.getString("backSignal", "") == "1") {
                        config.edit().putString("backSignal", "0").apply()
                        if (config.getString("currenttab", "") == "0" && isGridVisible) {
                            gridfadelinear.visibility = View.GONE
                            gridlinear.visibility = View.GONE
                            gridloading.visibility = View.VISIBLE
                            listview1.visibility = View.VISIBLE
                            listview1.alpha = 0f
                            isGridVisible = false
                            config.edit().putString("fragmentCanExit", "1").apply()
                            if (config.getString("disableanims", "") == "1") {
                                gridfadelinear.visibility = View.GONE
                                gridlinear.visibility = View.GONE
                                gridloading.visibility = View.GONE
                                listview1.visibility = View.VISIBLE
                                listview1.alpha = 1f
                            } else {
                                EzFade.crossfade(gridfadelinear, listview1, 250)
                            }
                        }
                    }
                    handler.postDelayed(this, 75)
                }
            }
        handler.post(runnable)

        linear2.clipToOutline = true
        linear2.setOnClickListener {
            gridfadelinear.visibility = View.GONE
            gridlinear.visibility = View.GONE
            gridloading.visibility = View.VISIBLE
            listview1.visibility = View.VISIBLE
            listview1.alpha = 0f
            config.edit().putString("fragmentCanExit", "1").commit()
            if (config.getString("disableanims", "") == "1") {
                gridfadelinear.visibility = View.GONE
                gridlinear.visibility = View.GONE
                gridloading.visibility = View.GONE
                listview1.visibility = View.VISIBLE
                listview1.alpha = 1f
            } else {
                EzFade.crossfade(gridfadelinear, listview1, 250)
            }
        }

        gridview1.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedItemList.edit().putString("selectedWall", position.toString()).apply()
                config.edit().putString("wallpaperName", walllist[position]["name"].toString()).apply()
                launchWallPreview.putExtra("wallpaperLink", walllist[position]["link"].toString())
                launchWallPreview.setClass(requireContext().applicationContext, WalldownloadActivity::class.java)
                startActivity(launchWallPreview)
            }

        gridview1.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, position, _ ->
                if (config.getString("debugMode", "") == "1") {
                    Toast.makeText(context, "Launching the new beta kotlin activity", Toast.LENGTH_SHORT).show()
                    selectedItemList.edit().putString("selectedWall", position.toString()).apply()
                    config.edit().putString("wallpaperName", walllist[position]["name"].toString()).apply()
                    launchWallPreview.putExtra("wallpaperName", walllist[position]["name"].toString())
                    launchWallPreview.putExtra("wallpaperLink", walllist[position]["link"].toString())
                    launchWallPreview.setClass(requireContext().applicationContext, WalldownloadkotlinActivity::class.java)
                    startActivity(launchWallPreview)
                    true
                } else {
                    false
                }
            }

        fetchwalljsonRequestListener =
            object : RequestNetwork.RequestListener {
                override fun onResponse(
                    tag: String,
                    response: String,
                    responseHeaders: HashMap<String, Any>,
                ) {
                    Thread {
                        try {
                            val parsedList: ArrayList<HashMap<String, Any>> =
                                Gson().fromJson(
                                    response,
                                    object : TypeToken<ArrayList<HashMap<String, Any>>>() {}.type,
                                )
                            activity?.runOnUiThread {
                                walllist = parsedList
                                gridview1.adapter = Gridview1Adapter(walllist)
                                gridview1.numColumns = 2
                                gridlinear.alpha = 0f
                                if (config.getString("disableanims", "") == "1") {
                                    gridlinear.visibility = View.VISIBLE
                                    gridloading.visibility = View.GONE
                                    gridlinear.alpha = 1f
                                } else {
                                    gridlinear.visibility = View.VISIBLE
                                    gridloading.visibility = View.GONE
                                    gridlinear.alpha = 1f
                                    EzFade.fadeIn(gridlinear, 500)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            activity?.runOnUiThread {
                                Toast.makeText(context, "Failed to parse data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.start()
                }

                override fun onErrorResponse(tag: String, message: String) {
                    Toast.makeText(context, "Failed to fetch, are you connected to the internet?", Toast.LENGTH_SHORT).show()
                }
            }

        fetchcategoryjsonRequestListener =
            object : RequestNetwork.RequestListener {
                override fun onResponse(
                    tag: String,
                    response: String,
                    responseHeaders: HashMap<String, Any>,
                ) {
                    Thread {
                        try {
                            val parsedCategories: ArrayList<HashMap<String, Any>> =
                                Gson().fromJson(
                                    response,
                                    object : TypeToken<ArrayList<HashMap<String, Any>>>() {}.type,
                                )
                            activity?.runOnUiThread {
                                categorylist = parsedCategories
                                listview1.adapter = Listview1Adapter(categorylist)
                                (listview1.adapter as BaseAdapter).notifyDataSetChanged()
                                linearloading.visibility = View.GONE
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            activity?.runOnUiThread {
                                noConnectionTerminator()
                            }
                        }
                    }.start()
                }

                override fun onErrorResponse(tag: String, message: String) {}
            }
    }

    private fun initializeLogic() {
        gridlinear.visibility = View.GONE
        if (temporaryCache.getString("firstTimeLoad", "") == "") {
            loadDelay =
                object : TimerTask() {
                    override fun run() {
                        activity?.runOnUiThread {
                            if (config.getString("categories", "") == "1") {
                                fetchcategoryjson.startRequestNetwork(
                                    RequestNetworkController.GET,
                                    config.getString("repo", "") + "categories.json",
                                    "",
                                    fetchcategoryjsonRequestListener,
                                )
                            } else {
                                fetchwalljson.startRequestNetwork(
                                    RequestNetworkController.GET,
                                    config.getString("directrepo", ""),
                                    "",
                                    fetchwalljsonRequestListener,
                                )
                            }
                        }
                    }
                }
            timer.schedule(loadDelay, 250)
            temporaryCache.edit().putString("firstTimeLoad", "0").apply()
        } else {
            fetchcategoryjson.startRequestNetwork(
                RequestNetworkController.GET,
                config.getString("repo", "") + "categories.json",
                "",
                fetchcategoryjsonRequestListener,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    fun _setViewSize(view: View, width: Double, height: Double) {
        view.layoutParams = LinearLayout.LayoutParams(width.toInt(), height.toInt())
    }

    inner class Listview1Adapter(
        private val data: ArrayList<HashMap<String, Any>>,
    ) : BaseAdapter() {
        private val repoPrefix = config.getString("repo", "")

        override fun getCount(): Int = data.size

        override fun getItem(index: Int): HashMap<String, Any> = data[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view =
                convertView ?: requireActivity().layoutInflater.inflate(R.layout.categorylist, null)

            val linear1 = view.findViewById<LinearLayout>(R.id.linear1)
            val linear2 = view.findViewById<FrameLayout>(R.id.linear2)
            val imageview1 = view.findViewById<ImageView>(R.id.imageview1)
            val textview1 = view.findViewById<TextView>(R.id.textview1)
            linear1.alpha = 0f

            if (config.getString("disableblur", "") != "1") {
                EzBlur.setBlur(imageview1, 20f)
            }

            textview1.text = categorylist[position]["category"].toString()
            if (config.getString("debugMode", "") == "1") {
                textview1.text = "${categorylist[position]["category"]}(index:$position)"
            }
            Glide.with(requireContext().applicationContext)
                .load(Uri.parse(repoPrefix + categorylist[position]["preview"].toString()))
                .into(imageview1)
            linear2.clipToOutline = true

            if (config.getString("disableanims", "") == "1") {
                linear1.visibility = View.VISIBLE
                linear1.alpha = 1f
            } else {
                EzFade.fadeIn(linear1, 100)
            }
            return view
        }
    }

    inner class Gridview1Adapter(
        private val data: ArrayList<HashMap<String, Any>>,
    ) : BaseAdapter() {
        private val repoPrefix = config.getString("repo", "")

        override fun getCount(): Int = data.size

        override fun getItem(index: Int): HashMap<String, Any> = data[index]

        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view =
                convertView ?: requireActivity().layoutInflater.inflate(R.layout.wallpaperlist, null)

            val linear1 = view.findViewById<LinearLayout>(R.id.linear1)
            val linear2 = view.findViewById<FrameLayout>(R.id.linear2)
            val wallimage = view.findViewById<ImageView>(R.id.wallimage)
            val linear3 = view.findViewById<LinearLayout>(R.id.linear3)
            val wallname = view.findViewById<TextView>(R.id.wallname)
            var isPfp = false
            linear1.alpha = 0f

            Glide.with(requireContext().applicationContext)
                .load(Uri.parse(repoPrefix + walllist[position]["lowprew"].toString()))
                .into(wallimage)

            if (walllist[position]["name"].toString().isEmpty()) {
                linear3.visibility = View.GONE
                linear3.setBackgroundColor(Color.TRANSPARENT)
                linear3.background = null
                wallname.text = ""
                if (config.getString("debugMode", "") == "1") {
                    wallname.text = "${walllist[position]["name"]}(index:$position)"
                    linear3.visibility = View.VISIBLE
                    linear3.background = resources.getDrawable(R.drawable.fade, null)
                }
            } else {
                linear3.visibility = View.VISIBLE
                linear3.background = resources.getDrawable(R.drawable.fade, null)
                wallname.text = walllist[position]["name"].toString()
                var original = wallname.text.toString()
                if (original.startsWith("fpfp.")) {
                    isPfp = true
                    original = original.substring("fpfp.".length)
                    wallname.text = original
                }
                if (isPfp) {
                    linear2.post {
                        if (linear2.width > 0) {
                            val params = linear2.layoutParams
                            if (params.height != linear2.width) {
                                params.height = linear2.width
                                linear2.layoutParams = params
                            }
                        }
                    }
                }
                if (config.getString("debugMode", "") == "1") {
                    wallname.text = "${walllist[position]["name"]}(index:$position)"
                    linear3.visibility = View.VISIBLE
                    linear3.background = resources.getDrawable(R.drawable.fade, null)
                }
            }
            linear2.clipToOutline = true

            if (config.getString("disableanims", "") == "1") {
                linear1.visibility = View.VISIBLE
                linear1.alpha = 1f
            } else {
                EzFade.fadeIn(linear1, 250)
            }
            return view
        }
    }
}
