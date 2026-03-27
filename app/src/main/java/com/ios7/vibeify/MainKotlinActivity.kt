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
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import org.json.*

class MainKotlinActivity : AppCompatActivity() {

    private lateinit var linear1: LinearLayout
    private lateinit var viewpager1: ViewPager
    private lateinit var bottombarroot: LinearLayout
    private lateinit var linear2: LinearLayout
    private lateinit var textview1: TextView
    private lateinit var linear4: LinearLayout
    private lateinit var button1: TextView
    private lateinit var button2: TextView
    private var bottom_nav: BottomNavigationView? = null
    private var navview1: NavigationView? = null
    private var navDetector: String = "0"

    private lateinit var pageLoaderInit: PageLoaderInitFragmentAdapter
    private lateinit var config: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundviolent)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.backgroundviolent)

        initialize(savedInstanceState)
        initializeLogic()
    }

    private fun initialize(savedInstanceState: Bundle?) {
        val root = findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val topBar = findViewById<View>(R.id.linear1)
            val bottomBar = findViewById<View>(R.id.bottombarroot)
            topBar?.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            bottomBar?.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        linear1 = findViewById(R.id.linear1)
        viewpager1 = findViewById(R.id.viewpager1)
        bottombarroot = findViewById(R.id.bottombarroot)
        linear2 = findViewById(R.id.linear2)
        textview1 = findViewById(R.id.textview1)
        linear4 = findViewById(R.id.linear4)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        linear4.visibility = View.GONE

        try {
            bottom_nav = findViewById(R.id.bottomnav1)
            bottom_nav?.visibility = View.GONE
            bottom_nav?.visibility = View.VISIBLE
            navDetector = "1"
        } catch (a: Exception) {
            try {
                Log.d("DEBUG", "Bottom nav not found, possible a large screen device?")
                navview1 = findViewById(R.id.navview1)
                navview1?.visibility = View.GONE
                navview1?.visibility = View.VISIBLE
                navDetector = "2"
            } catch (e: Exception) {
                Log.d("DEBUG", "Bottom nav not found, possible a large screen device?")
                navDetector = "0"
            }
        }

        button1.setBackgroundResource(R.drawable.activetab)
        button2.setBackgroundResource(R.drawable.roundedbgviolent)
        pageLoaderInit = PageLoaderInitFragmentAdapter(this, supportFragmentManager)
        config = getSharedPreferences("config", MODE_PRIVATE)

        button1.setOnClickListener {
            viewpager1.currentItem = 0
        }

        button2.setOnClickListener {
            viewpager1.currentItem = 1
        }
    }

    override fun onBackPressed() {
        config.edit().putString("backSignal", "1").apply()

        if (config.getString("currenttab", "") == "1") {
            Handler(Looper.getMainLooper()).postDelayed({
                viewpager1.currentItem = 0
            }, 150)
        } else {
            if (config.getString("fragmentCanExit", "") == "0") {
                // Do nothing
            } else {
                super.onBackPressed()
                finish()
            }
        }
    }

    private fun showKotlinConversionDialog() {
        val inflater = LayoutInflater.from(this)
        val customView = inflater.inflate(R.layout.abandon_dialog, null)

        val title = customView.findViewById<TextView>(R.id.textView)
        val message = customView.findViewById<TextView>(R.id.textView4)
        val closebtn = customView.findViewById<TextView>(R.id.closebtn)
        val repobtn = customView.findViewById<TextView>(R.id.repobtn)

        title.text = "🚀 Kotlin Migration in Progress"
        message.text = "The app is currently undergoing a technical migration from Java to Kotlin. While we strive for 1:1 functionality, some experimental components may behave unexpectedly during this transition.\n\nThank you for being part of this AI-driven evolution!\n- Antigravity AI"
        repobtn.visibility = View.VISIBLE
        repobtn.text = "Java Version"

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(customView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()

        closebtn.setOnClickListener {
            dialog.dismiss()
        }

        repobtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }
    }

    private fun initializeLogic() {
        showKotlinConversionDialog()
        // Repository and default settings
        config.edit().putString("repo", "https://raw.githubusercontent.com/j1459863h/wallify-walls/refs/heads/main/").apply()
        config.edit().putString("categories", "1").apply()
        config.edit().putString("directrepo", "https://altdisk.eimaen.pw/api/download/a69b5e5031f23e06cd1af7f885de5c0c/anime.json").apply()
        if (config.getString("timeout", "") == "") {
            config.edit().putString("timeout", "5000").apply()
        }

        val setupFlag = config.getString("setupcomplete", "")
        if (setupFlag == "") {
            startActivity(Intent(this, SetupActivity1::class.java))
        }

        if (config.getString("colorextraction", "") == "") {
            config.edit().putString("colorextraction", "1").apply()
        }
        if (config.getString("disableanims", "") == "") {
            config.edit().putString("disableanims", "0").apply()
        }
        if (config.getString("disableblur", "") == "") {
            config.edit().putString("disableblur", "0").apply()
        }

        if (config.getString("forcedDebug", "") == "1") {
            config.edit().putString("debugMode", "1").apply()
        } else {
            config.edit().putString("debugMode", "0").apply()
        }

        if (android.os.Debug.isDebuggerConnected()) {
            config.edit().putString("debugMode", "1").apply()
            textview1.text = "WALLIFY"
            config.edit().putString("disableanims", "1").apply()
            config.edit().putString("disableblur", "1").apply()
        } else if (config.getString("forcedDebug", "") == "1") {
            config.edit().putString("debugMode", "1").apply()
            textview1.text = "DEBUGGER NOT ATTACHED!"
            config.edit().putString("disableanims", "1").apply()
            config.edit().putString("disableblur", "1").apply()
        } else {
            config.edit().putString("debugMode", "0").apply()
        }

        config.edit().putString("debugMode", "0").apply()
        config.edit().putString("disableanims", "0").apply()
        config.edit().putString("disableblur", "0").apply()
        config.edit().putString("colorextraction", "1").apply()
        textview1.text = getString(R.string.app_name) + " (kotlin-ext)"

        if (config.getString("disableblur", "") == "") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                config.edit().putString("disableblur", "0").apply()
            } else {
                config.edit().putString("disableblur", "1").apply()
            }
        }

        pageLoaderInit.tabCount = 3
        viewpager1.adapter = pageLoaderInit
        viewpager1.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    button1.setBackgroundResource(R.drawable.activetab)
                    button2.setBackgroundResource(R.drawable.roundedbgviolent)
                    config.edit().putString("currenttab", "0").apply()
                    try {
                        bottom_nav?.selectedItemId = R.id.page_1
                    } catch (e: Exception) {
                        Log.d("DEBUG", "Bottom nav not found")
                    }
                }
                if (position == 1) {
                    button2.setBackgroundResource(R.drawable.activetab)
                    button1.setBackgroundResource(R.drawable.roundedbgviolent)
                    config.edit().putString("currenttab", "1").apply()
                    try {
                        bottom_nav?.selectedItemId = R.id.page_2
                    } catch (e: Exception) {
                        Log.d("DEBUG", "Bottom nav not found")
                    }
                }
                if (position == 2) {
                    button1.setBackgroundResource(R.drawable.roundedbgviolent)
                    button2.setBackgroundResource(R.drawable.roundedbgviolent)
                    config.edit().putString("currenttab", "2").apply()
                    try {
                        bottom_nav?.selectedItemId = R.id.page_3
                    } catch (e: Exception) {
                        Log.d("DEBUG", "Bottom nav not found")
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        if (navDetector == "1") {
            linear4.visibility = View.GONE
            bottom_nav?.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.page_1 -> {
                        viewpager1.currentItem = 0
                        true
                    }
                    R.id.page_2 -> {
                        viewpager1.currentItem = 1
                        true
                    }
                    R.id.page_3 -> {
                        viewpager1.currentItem = 2
                        true
                    }
                    else -> false
                }
            }
            val activeColor = ContextCompat.getColor(this, R.color.activetab)
            bottom_nav?.itemActiveIndicatorColor = ColorStateList.valueOf(activeColor)
        } else if (navDetector == "2") {
            linear4.visibility = View.GONE
            navview1?.setNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.page_1 -> {
                        viewpager1.currentItem = 0
                        true
                    }
                    R.id.page_2 -> {
                        viewpager1.currentItem = 1
                        true
                    }
                    R.id.page_3 -> {
                        viewpager1.currentItem = 2
                        true
                    }
                    else -> false
                }
            }
        } else {
            linear4.visibility = View.VISIBLE
        }

        viewpager1.clipToOutline = true
    }

    inner class PageLoaderInitFragmentAdapter(context: Context, manager: FragmentManager) :
        FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        var tabCount: Int = 3

        override fun getCount(): Int = tabCount

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> WallpapersFragmentActivity()
                1 -> SettingsKotlinFragment()
                2 -> ConversionStatusFragment()
                else -> Fragment()
            }
        }
    }
}
