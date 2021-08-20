package com.example.zyyschedule.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var vm: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =  DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()
        vm = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        val navController:NavController = Navigation.findNavController(this, R.id.fragment)
        val configuration: AppBarConfiguration=AppBarConfiguration.Builder(binding.bottomNavigationView.menu).build()
        NavigationUI.setupActionBarWithNavController(this, navController, configuration)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        vm.allUnFinishOfRemind.observe(this, { schedules: List<Schedule> ->
            for (i in schedules.indices) {
                if (schedules[i].remind.isNotEmpty() && !schedules[i].isTagRemind) {
                    setNotificationRemind(schedules[i])
                    vm.updateRemindTag(schedules[i].id)
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
    private fun setNotificationRemind(schedule: Schedule){
        val remind = schedule.remind.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()
        val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date = Date()
        val now = Date()
        for(i in remind.indices ){
            try {
                date = std.parse(remind[i])
            }catch (ignored: Exception){
            }
            if (date.time>now.time){
                val gson = Gson()
                val intent = Intent(this, NotificationReceiver::class.java)
                intent.action ="Notification_Receiver"
                intent.putExtra("remindSchedule", gson.toJson(schedule))
                intent.putExtra("PendingIntentCode", schedule.id + i * 1000)
                val sender = PendingIntent.getBroadcast(this, schedule.id + i * 1000, intent, 0)
                val am = getSystemService(ALARM_SERVICE) as AlarmManager
                am[AlarmManager.RTC_WAKEUP, date.time] = sender
            }
        }
    }

}