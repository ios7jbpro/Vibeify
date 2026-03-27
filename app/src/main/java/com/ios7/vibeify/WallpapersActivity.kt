package com.ios7.vibeify

import android.animation.*
import android.app.Activity
import android.content.*
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.media.*
import android.net.*
import android.os.*
import android.text.*
import android.text.style.*
import android.util.*
import android.view.*
import android.view.View.*
import android.view.animation.*
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ios7.vibeify.MyClasses.EzBlur
import com.ios7.vibeify.MyClasses.EzFade
import com.ios7.vibeify.MyClasses.EzTimer
import com.ios7.vibeify.MyClasses.EzTimerLooped
import java.io.*
import java.text.*
import java.util.*
import org.json.*

class WallpapersActivity : AppCompatActivity() {
    private var isGridVisible = false
    private val _timer = Timer()
    
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
    private var _fetchwalljson_request_listener: RequestNetwork.RequestListener? = null
    private var relay: TimerTask? = null
    private lateinit var selectedItemList: SharedPreferences
    private val launchWallPreview = Intent()
    private lateinit var config: SharedPreferences
    private lateinit var temporaryCache: SharedPreferences
    private var loadDelay: TimerTask? = null
    private lateinit var fetchcategoryjson: RequestNetwork
    private var _fetchcategoryjson_request_listener: RequestNetwork.RequestListener? = null
    private var combinedOutput: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wallpapers_fragment)
        initialize(savedInstanceState)
        initializeLogic()
    }

    private fun noConnectionTerminator() {
        val nointernet = Intent()
        nointernet.setClass(this, NoInternet::class.java)
        startActivity(nointernet)
        finish()
    }

    private fun initialize(savedInstanceState: Bundle?) {
        rootlinear = findViewById(R.id.rootlinear)
        linear1 = findViewById(R.id.linear1)
        tempcardview = findViewById(R.id.tempcardview)
        listview1 = findViewById(R.id.listview1)
        gridlinear = findViewById(R.id.gridlinear)
        gridfadelinear = findViewById(R.id.gridfadelinear)
        gridloading = findViewById(R.id.gridloading)
        linear2 = findViewById(R.id.linear2)
        gridview1 = findViewById(R.id.gridview1)
        textview1 = findViewById(R.id.textview1)
        textloading = findViewById(R.id.textloading)
        linearloading = findViewById(R.id.linearloading)
        textloading.visibility = View.GONE
        gridRounderLayout = findViewById(R.id.gridRounderLayout)
        gridRounderLayout.clipToOutline = true
        gridfadelinear.visibility = View.GONE
        
        fetchwalljson = RequestNetwork(this)
        selectedItemList = getSharedPreferences("selectedItemList", Activity.MODE_PRIVATE)
        config = getSharedPreferences("config", Activity.MODE_PRIVATE)
        temporaryCache = getSharedPreferences("temporaryCache", Activity.MODE_PRIVATE)
        fetchcategoryjson = RequestNetwork(this)
        
        EzTimer.runWithDelay(5000) {
            textloading.visibility = View.VISIBLE
        }

        listview1.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val jsonPath = categorylist[position]["json"].toString()
            fetchwalljson.startRequestNetwork(RequestNetworkController.GET, config.getString("repo", "") + jsonPath, "", _fetchwalljson_request_listener)
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
        val runnable = object : Runnable {
            override fun run() {
                if (config.getString("backSignal", "") == "1") {
                    config.edit().putString("backSignal", "0").apply()
                    if (config.getString("currenttab", "") == "0") {
                        if (isGridVisible) {
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

        gridview1.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedItemList.edit().putString("selectedWall", position.toString()).apply()
            Log.d("WallpaperDebug", "Setting selected wall to = '$position'")
            config.edit().putString("wallpaperName", walllist[position]["name"].toString()).apply()
            launchWallPreview.putExtra("wallpaperLink", walllist[position]["link"].toString())
            launchWallPreview.setClass(applicationContext, WalldownloadActivity::class.java)
            startActivity(launchWallPreview)
        }

        gridview1.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            if (config.getString("debugMode", "") == "1") {
                Toast.makeText(this, "Launching the new beta kotlin activity", Toast.LENGTH_SHORT).show()
                selectedItemList.edit().putString("selectedWall", position.toString()).apply()
                Log.d("WallpaperDebug", "Setting selected wall to = '$position'")
                config.edit().putString("wallpaperName", walllist[position]["name"].toString()).apply()
                launchWallPreview.putExtra("wallpaperName", walllist[position]["name"].toString())
                launchWallPreview.putExtra("wallpaperLink", walllist[position]["link"].toString())
                launchWallPreview.setClass(applicationContext, WalldownloadkotlinActivity::class.java)
                startActivity(launchWallPreview)
                true
            } else {
                false
            }
        }

        _fetchwalljson_request_listener = object : RequestNetwork.RequestListener {
            override fun onResponse(tag: String, response: String, responseHeaders: HashMap<String, Any>) {
                Thread {
                    try {
                        val parsedList: ArrayList<HashMap<String, Any>> = Gson().fromJson(response, object : TypeToken<ArrayList<HashMap<String, Any>>>() {}.type)
                        runOnUiThread {
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
                        runOnUiThread {
                            Toast.makeText(this@WallpapersActivity, "Failed to parse data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }

            override fun onErrorResponse(tag: String, message: String) {
                Toast.makeText(this@WallpapersActivity, "Failed to fetch, are you connected to the internet?", Toast.LENGTH_SHORT).show()
            }
        }

        _fetchcategoryjson_request_listener = object : RequestNetwork.RequestListener {
            override fun onResponse(tag: String, response: String, responseHeaders: HashMap<String, Any>) {
                Thread {
                    try {
                        val parsedCategories: ArrayList<HashMap<String, Any>> = Gson().fromJson(response, object : TypeToken<ArrayList<HashMap<String, Any>>>() {}.type)
                        runOnUiThread {
                            categorylist = parsedCategories
                            listview1.adapter = Listview1Adapter(categorylist)
                            (listview1.adapter as BaseAdapter).notifyDataSetChanged()
                            linearloading.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            noConnectionTerminator()
                        }
                    }
                }.start()
            }

            override fun onErrorResponse(tag: String, message: String) {
                // No-op
            }
        }
    }

    private fun initializeLogic() {
        // Initial setup matching MainActivity
        config.edit().putString("repo", "https://raw.githubusercontent.com/j1459863h/wallify-walls/refs/heads/main/").commit()
        config.edit().putString("categories", "1").commit()
        config.edit().putString("directrepo", "https://altdisk.eimaen.pw/api/download/a69b5e5031f23e06cd1af7f885de5c0c/anime.json").commit()
        if (config.getString("timeout", "") == "") {
            config.edit().putString("timeout", "5000").commit()
        }
        
        val setupFlag = config.getString("setupcomplete", "")
        if (setupFlag == "") {
            startActivity(Intent(this, SetupActivity1::class.java))
        }

        gridlinear.visibility = View.GONE
        if (temporaryCache.getString("firstTimeLoad", "") == "") {
            loadDelay = object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (config.getString("categories", "") == "1") {
                            fetchcategoryjson.startRequestNetwork(RequestNetworkController.GET, config.getString("repo", "") + "categories.json", "", _fetchcategoryjson_request_listener)
                        } else {
                            fetchwalljson.startRequestNetwork(RequestNetworkController.GET, config.getString("directrepo", ""), "", _fetchwalljson_request_listener)
                        }
                    }
                }
            }
            _timer.schedule(loadDelay, 250)
            temporaryCache.edit().putString("firstTimeLoad", "0").apply()
        } else {
            fetchcategoryjson.startRequestNetwork(RequestNetworkController.GET, config.getString("repo", "") + "categories.json", "", _fetchcategoryjson_request_listener)
        }
    }

    override fun onBackPressed() {
        if (isGridVisible) {
            gridfadelinear.visibility = View.GONE
            gridlinear.visibility = View.GONE
            gridloading.visibility = View.VISIBLE
            listview1.visibility = View.VISIBLE
            listview1.alpha = 0f
            isGridVisible = false
            config.edit().putString("fragmentCanExit", "1").commit()
            if (config.getString("disableanims", "") == "1") {
                listview1.alpha = 1f
            } else {
                EzFade.crossfade(gridfadelinear, listview1, 250)
            }
        } else {
            super.onBackPressed()
        }
    }

    inner class Listview1Adapter(private val _data: ArrayList<HashMap<String, Any>>) : BaseAdapter() {
        private val repoPrefix = config.getString("repo", "")
        override fun getCount(): Int = _data.size
        override fun getItem(index: Int): HashMap<String, Any> = _data[index]
        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(position: Int, v: View?, container: ViewGroup?): View {
            val inflater = layoutInflater
            var view = v
            if (view == null) {
                view = inflater.inflate(R.layout.categorylist, null)
            }

            val linear1 = view!!.findViewById<LinearLayout>(R.id.linear1)
            val linear2 = view.findViewById<FrameLayout>(R.id.linear2)
            val imageview1 = view.findViewById<ImageView>(R.id.imageview1)
            val textview1 = view.findViewById<TextView>(R.id.textview1)
            linear1.alpha = 0f

            if (config.getString("disableblur", "") != "1") {
                EzBlur.setBlur(imageview1, 20f)
            }

            textview1.text = _data[position]["category"].toString()
            if (config.getString("debugMode", "") == "1") {
                textview1.text = "${_data[position]["category"]}(index:$position)"
            }
            
            Glide.with(applicationContext).load(Uri.parse(repoPrefix + _data[position]["preview"].toString())).into(imageview1)
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

    inner class Gridview1Adapter(private val _data: ArrayList<HashMap<String, Any>>) : BaseAdapter() {
        private val repoPrefix = config.getString("repo", "")
        override fun getCount(): Int = _data.size
        override fun getItem(index: Int): HashMap<String, Any> = _data[index]
        override fun getItemId(index: Int): Long = index.toLong()

        override fun getView(position: Int, v: View?, container: ViewGroup?): View {
            val inflater = layoutInflater
            var view = v
            if (view == null) {
                view = inflater.inflate(R.layout.wallpaperlist, null)
            }

            val linear1 = view!!.findViewById<LinearLayout>(R.id.linear1)
            val linear2 = view.findViewById<FrameLayout>(R.id.linear2)
            val wallimage = view.findViewById<ImageView>(R.id.wallimage)
            val linear3 = view.findViewById<LinearLayout>(R.id.linear3)
            val wallname = view.findViewById<TextView>(R.id.wallname)
            var isPfp = false
            linear1.alpha = 0f

            Glide.with(applicationContext).load(Uri.parse(repoPrefix + _data[position]["lowprew"].toString())).into(wallimage)

            if (_data[position]["name"].toString() == "") {
                linear3.visibility = View.GONE
                linear3.setBackgroundColor(Color.TRANSPARENT)
                linear3.background = null
                wallname.text = ""
                if (config.getString("debugMode", "") == "1") {
                    wallname.text = "${_data[position]["name"]}(index:$position)"
                    linear3.visibility = View.VISIBLE
                    linear3.background = resources.getDrawable(R.drawable.fade)
                }
            } else {
                linear3.visibility = View.VISIBLE
                linear3.background = resources.getDrawable(R.drawable.fade)
                var original = _data[position]["name"].toString()
                if (original.startsWith("fpfp.")) {
                    isPfp = true
                    original = original.substring("fpfp.".length)
                }
                wallname.text = original
                
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
                    wallname.text = "${_data[position]["name"]}(index:$position)"
                    linear3.visibility = View.VISIBLE
                    linear3.background = resources.getDrawable(R.drawable.fade)
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
