package com.example.zyyschedule.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.zyyschedule.R
import com.example.zyyschedule.broadcastreceiver.NotificationReceiver
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.ActivityMainBinding
import com.example.zyyschedule.viewmodel.MainActivityViewModel
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var firstTime: Long = 0
    private val vm: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val navController: NavController = Navigation.findNavController(this, R.id.fragment)
        val configuration: AppBarConfiguration = AppBarConfiguration.Builder(binding.bottomNavigationView.menu).build()
        NavigationUI.setupActionBarWithNavController(this, navController, configuration)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        vm.getALLUnFinishOfRemind().observe(this, { schedules: List<Schedule> ->
            for (i in schedules.indices) {
                if (schedules[i].remind?.isNotEmpty() == true && !schedules[i].tagRemind) {
                    var labelTitle: String?
                    schedules[i].labelId?.let { labelId ->
                        vm.getLabelTitle(labelId).observe(this) {
                            labelTitle = it
                            setNotificationRemind(schedules[i], labelTitle)
                            schedules[i].id?.let {id-> vm.updateRemindTag(id) }
                        }
                    }
                }
            }
        })
        LiveEventBus
                .get("SomeF_MainA", String::class.java)
                .observe(this, { s: String ->
                    when (s) {
                        "gone_navigation" -> {
                            binding.bottomNavigationView.visibility = View.GONE
                        }
                        "visible_navigation" -> {
                            binding.bottomNavigationView.visibility = View.VISIBLE
                        }
                    }
                })

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_DOWN) {
            val secondTime = System.currentTimeMillis()
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, R.string.exit_message, Toast.LENGTH_SHORT).show()
                firstTime = secondTime
                return true
            } else {
                exitProcess(0)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("SimpleDateFormat")
    private fun setNotificationRemind(schedule: Schedule,labelTitle:String?) {
        val remind = schedule.remind?.split(",")?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date = Date()
        val now = Date()
        for (i in remind?.indices!!) {
            try {
                date = std.parse(remind[i])
            } catch (ignored: Exception) {
            }
            if (date.time > now.time) {
                val gson = Gson()
                val intent = Intent(this, NotificationReceiver::class.java)
                intent.action = "Notification_Receiver"
                intent.putExtra("remindSchedule", gson.toJson(schedule))
                intent.putExtra("PendingIntentCode", schedule.id?.plus(i * 1000))
                intent.putExtra("LabelTitle", labelTitle)
                val sender = schedule.id?.plus(i * 1000)?.let { PendingIntent.getBroadcast(this, it, intent, 0) }
                val am = getSystemService(ALARM_SERVICE) as AlarmManager
                am[AlarmManager.RTC_WAKEUP, date.time] = sender
            }
        }
    }

    override fun onClick(v: View?) {
    }


}