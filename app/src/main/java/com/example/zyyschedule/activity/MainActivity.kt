package com.example.zyyschedule.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
class MainActivity : AppCompatActivity() {
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
        vm.getALLUnFinishOfRemind()?.observe(this, { schedules: List<Schedule> ->
            for (i in schedules.indices) {
                if (schedules[i].remind?.isNotEmpty() == true && !schedules[i].tagRemind) {
                    setNotificationRemind(schedules[i])
                    schedules[i].id?.let { vm.updateRemindTag(it) }
                }
            }
        })

        LiveEventBus
                .get("some_key", String::class.java)
                .observe(this, { s: String ->
                    if (s == "gone_navigation") {
                        binding.bottomNavigationView.visibility = View.GONE
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
    private fun setNotificationRemind(schedule: Schedule) {
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
                val sender = schedule.id?.plus(i * 1000)?.let { PendingIntent.getBroadcast(this, it, intent, 0) }
                val am = getSystemService(ALARM_SERVICE) as AlarmManager
                am[AlarmManager.RTC_WAKEUP, date.time] = sender
            }
        }
    }

}