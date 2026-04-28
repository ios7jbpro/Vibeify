package com.ios7.vibeify

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.ios7.vibeify.MyClasses.EzTimer
import com.ios7.vibeify.MyClasses.EzTimerLooped
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

open class SettingsKotlinFragment : DialogFragment() {

    private lateinit var linear1: LinearLayout
    private lateinit var textview5: TextView
    private lateinit var textview2: TextView
    private lateinit var textview3: TextView
    private lateinit var switchColorPreviews: SwitchCompat
    private lateinit var switchDisableAnims: SwitchCompat
    private lateinit var switchDisableBlur: SwitchCompat
    private lateinit var listView: ListView
    private lateinit var linear30: LinearLayout
    private lateinit var linearReinitSetup: LinearLayout
    private lateinit var linearManualDebug: LinearLayout
    private lateinit var linearRestartApp: LinearLayout
    private lateinit var linearOptionsContainer: LinearLayout
    private lateinit var textviewManualDebug: TextView
    private lateinit var linearReTip: LinearLayout
    private lateinit var textviewtipsloading: TextView

    private lateinit var config: SharedPreferences
    private var totalTips: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_dialog_fragment, container, false)
        listView = view.findViewById(R.id.listView)
        initialize(savedInstanceState, view)
        initializeLogic()
        optionsDeprecator()
        return view
    }

    private fun noConnectionTerminator() {
        val intent = Intent(activity, NoInternet::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun optionsDeprecator() {
        linearManualDebug.visibility = View.GONE
        linearOptionsContainer.visibility = View.GONE
    }

    private fun initialize(savedInstanceState: Bundle?, view: View) {
        linear1 = view.findViewById(R.id.linear1)
        linear1.clipToOutline = true
        textview5 = view.findViewById(R.id.textview5)
        textview2 = view.findViewById(R.id.textview2)
        textview3 = view.findViewById(R.id.textview3)
        textviewtipsloading = view.findViewById(R.id.textviewtipsloading)
        textview3.visibility = View.GONE
        switchColorPreviews = view.findViewById(R.id.switchColorPreviews)
        switchDisableAnims = view.findViewById(R.id.switchDisableAnims)
        switchDisableBlur = view.findViewById(R.id.switchDisableBlur)
        linear30 = view.findViewById(R.id.linear30)
        linearReinitSetup = view.findViewById(R.id.linearReinitSetup)
        linearManualDebug = view.findViewById(R.id.linearManualDebug)
        linearRestartApp = view.findViewById(R.id.linearRestartApp)
        linearOptionsContainer = view.findViewById(R.id.LinearOptionsContainer)
        textviewManualDebug = view.findViewById(R.id.textviewManualDebug)
        linearReTip = view.findViewById(R.id.linearReTip)
        linearManualDebug.visibility = View.VISIBLE
        linearReTip.visibility = View.GONE
        config = requireContext().getSharedPreferences("config", Activity.MODE_PRIVATE)
    }

    private fun initializeLogic() {
        if (config.getString("colorextraction", "") == "1") {
            switchColorPreviews.isChecked = true
        }
        if (config.getString("disableanims", "") == "1") {
            switchDisableAnims.isChecked = true
        }
        if (config.getString("disableblur", "") == "1") {
            switchDisableBlur.isChecked = true
        }

        switchColorPreviews.setOnCheckedChangeListener { _, isChecked ->
            config.edit().putString("colorextraction", if (isChecked) "1" else "0").apply()
        }
        switchDisableAnims.setOnCheckedChangeListener { _, isChecked ->
            config.edit().putString("disableanims", if (isChecked) "1" else "0").apply()
        }
        switchDisableBlur.setOnCheckedChangeListener { _, isChecked ->
            config.edit().putString("disableblur", if (isChecked) "1" else "0").apply()
        }

        linearReTip.setOnClickListener {
            textviewtipsloading.visibility = View.VISIBLE
            textview3.visibility = View.GONE
            linearReTip.visibility = View.GONE
            tipsLoader()
        }

        if (config.getString("debugMode", "") == "1") {
            textviewManualDebug.text = "Exit forced debug mode"
        }

        textview3.setOnClickListener {
            if (config.getString("debugMode", "") == "1") {
                val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("DEBUG", textview3.text)
                clipboardManager.setPrimaryClip(clipData)
            } else {
                val remoterepo = "https://github.com/j1459863h/wallify-walls/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(remoterepo))
                startActivity(intent)
            }
        }

        linearManualDebug.setOnClickListener {
            Log.d("MANDEBUG", "Launching manual debug enabler")
            val intent = Intent(activity, ManualDebugEnabler::class.java)
            startActivity(intent)
            Log.d("MANDEBUG", "Exiting the app")
            activity?.finish()
        }

        linearRestartApp.setOnClickListener {
            Log.d("RESTART", "Restarting app")
            val intent = Intent(activity, AppRestarterKotlin::class.java)
            startActivity(intent)
            Log.d("MANDEBUG", "Exiting the app")
            activity?.finish()
        }

        try {
            val appName = requireContext().getString(R.string.app_name)
            val versionName = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName
            textview2.text = "$appName $versionName"
        } catch (e: PackageManager.NameNotFoundException) {
        }

        val url = config.getString("repo", "") + "devs.json"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FETCH_ERROR", e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("FETCH_ERROR", "Request failed")
                    return
                }

                val json = response.body?.string() ?: return

                try {
                    val jsonArray = JSONArray(json)
                    val developerList = mutableListOf<Developer>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val name = jsonObject.getString("name")
                        val imageUrl = jsonObject.getString("pfp")
                        var devUrl = ""
                        try {
                            devUrl = jsonObject.getString("url")
                        } catch (e: JSONException) {
                            Log.e("FETCH_ERROR", "Error at position: $i", e)
                        }
                        developerList.add(Developer(name, imageUrl, devUrl))
                    }

                    activity?.runOnUiThread {
                        val adapter = DeveloperAdapter(requireContext(), developerList)
                        listView.adapter = adapter
                        listView.setOnItemClickListener { parent, _, position, _ ->
                            val clickedDeveloper = parent.getItemAtPosition(position) as Developer
                            val devUrl = clickedDeveloper.devUrl
                            if (devUrl.isNullOrEmpty()) {
                                Toast.makeText(context, "No URL found for this developer.", Toast.LENGTH_SHORT).show()
                            } else {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(devUrl))
                                startActivity(intent)
                            }
                        }
                        setListViewHeightBasedOnChildren(listView)
                    }
                } catch (e: JSONException) {
                    Log.e("FETCH_ERROR", "JSON parsing error", e)
                }
            }
        })

        linear30.setOnClickListener {
            val inflater = LayoutInflater.from(requireContext())
            val customView = inflater.inflate(R.layout.abandon_dialog, null)
            val closebtn = customView.findViewById<TextView>(R.id.closebtn)
            val repobtn = customView.findViewById<TextView>(R.id.repobtn)
            repobtn.visibility = View.GONE

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(customView)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            closebtn.setOnClickListener { dialog.dismiss() }
            repobtn.setOnClickListener {
                val uri = Uri.parse("https://github.com/ios7jbpro/wallify")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                dialog.dismiss()
            }
        }

        linearReinitSetup.setOnClickListener {
            val intent = Intent(context, SetupActivity1::class.java)
            startActivity(intent)
        }

        if (config.getString("debugMode", "") == "1") {
            textview2.text = "DEBUG"
            linear30.visibility = View.GONE
            listView.visibility = View.GONE
            textviewtipsloading.visibility = View.GONE
            textview3.visibility = View.VISIBLE
            textview3.text = "Loading debug values...\nStarting a timer..."
            val loopedTimer15 = EzTimerLooped()
            loopedTimer15.start(50) {
                val repoval = config.getString("repo", "")
                val timeoutval = config.getString("timeout", "")
                val colorextractionval = "(enforced on debug)" + config.getString("colorextraction", "")
                val disableanimsval = "(enforced on debug)" + config.getString("disableanims", "")
                val disableblurval = "(enforced on debug)" + config.getString("disableblur", "")
                val setupcompleteval = config.getString("setupcomplete", "")
                val debugmodeval = config.getString("debugMode", "")
                val endOutput = "repo:$repoval\ntimeoutval:$timeoutval\ncolorextraction:$colorextractionval\ndisableanims:$disableanimsval\ndisableblur:$disableblurval\nsetupcomplete:$setupcompleteval\ndebugMode:$debugmodeval\n*USING DEBUG WILL RESET SOME OF THE FLAGS*"
                textview3.text = endOutput
                switchDisableAnims.isChecked = true
                switchColorPreviews.isChecked = true
                switchDisableBlur.isChecked = true
            }
        } else {
            linearReinitSetup.visibility = View.GONE
        }
        tipsLoader()
        textview3.clipToOutline = true
    }

    private fun tipsLoader() {
        textview3.text = "TipsLoader service started.\nWaiting for the remote fetch, this text will update itself...\n\nClick me to see the wallpapers repository"
        val client = OkHttpClient()
        val url = config.getString("repo", "") + "tips/total"
        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    activity?.runOnUiThread {
                        try {
                            textviewtipsloading.text = "Cannot reach tips service"
                            textview3.text = "Cannot reach tips service"
                            noConnectionTerminator()
                        } catch (ex: Exception) {
                            textviewtipsloading.text = "TipsLoader failed. Check logs."
                            textview3.text = "TipsLoader failed. Check logs."
                            Log.e("TipsLoader", "Crash detected: $ex")
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        totalTips = try {
                            responseBody.trim().toInt()
                        } catch (e: NumberFormatException) {
                            5
                        }
                        val randomNum = Random().nextInt(totalTips) + 1
                        val tipRequest = Request.Builder()
                            .url(config.getString("repo", "") + "tips/" + randomNum)
                            .build()

                        client.newCall(tipRequest).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                activity?.runOnUiThread {
                                    textviewtipsloading.text = "Cannot reach tips service"
                                    textview3.text = "Cannot reach tips service"
                                    noConnectionTerminator()
                                }
                            }

                            override fun onResponse(call: Call, response: Response) {
                                if (response.isSuccessful) {
                                    val tipBody = response.body?.string() ?: ""
                                    activity?.runOnUiThread {
                                        textview3.text = tipBody
                                    }
                                }
                            }
                        })
                    }
                    EzTimer.runWithDelay(300) {
                        FetchCommitMessageTask().execute()
                    }
                }
            })
        } catch (e: Exception) {
            textview3.text = "TipsLoader failed. Check logs."
        }
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter ?: return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(
                View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (listAdapter.count - 1))
        listView.layoutParams = params
        listView.requestLayout()
    }

    private inner class FetchCommitMessageTask : AsyncTask<Void?, Void?, String?>() {
        var tipsFailed = false
        override fun doInBackground(vararg params: Void?): String? {
            return try {
                val url = URL("https://api.github.com/repos/ios7jbpro/Vibeify/commits/main")
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Vibeify-App")
                connection.connect()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val jsonBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    jsonBuilder.append(line)
                }
                val json = JSONObject(jsonBuilder.toString())
                val commit = json.getJSONObject("commit")
                tipsFailed = false
                commit.getString("message")
            } catch (e: Exception) {
                e.printStackTrace()
                "Failed to fetch commit message"
            }
        }

        override fun onPostExecute(message: String?) {
            if (!tipsFailed) {
                textview3.text = "${textview3.text}\n-----------------\nLast commit message:$message\n\nClick me to see the wallpapers repository"
                textviewtipsloading.visibility = View.GONE
                textview3.visibility = View.VISIBLE
                linearReTip.visibility = View.VISIBLE
            } else {
                textviewtipsloading.text = "TipsLoader service failed, check logs"
            }
        }
    }
}
