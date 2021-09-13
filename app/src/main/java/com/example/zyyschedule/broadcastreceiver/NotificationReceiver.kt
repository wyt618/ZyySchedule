package com.example.zyyschedule.broadcastreceiver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.blankj.utilcode.util.NotificationUtils
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Schedule
import com.google.gson.Gson


class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("LaunchActivityFromNotification", "UnspecifiedImmutableFlag")
    override fun onReceive(context: Context?, intent: Intent?) {
        val gson = Gson()
        val strSchedule = intent?.getStringExtra("remindSchedule")
        val schedule: Schedule = gson.fromJson(strSchedule, Schedule::class.java)
        if (intent?.action?.equals("Notification_Receiver") == true) {
            val remindDialogReceiver = Intent("RemindDialogReceiver")
            remindDialogReceiver.putExtra("remindSchedule", intent.getStringExtra("remindSchedule"))
            remindDialogReceiver.putExtra("LabelTitle", intent.getStringExtra("LabelTitle"))
            val toRemindDialog = PendingIntent.getBroadcast(context, intent.getIntExtra("PendingIntentCode", 0) + 10000, remindDialogReceiver, FLAG_UPDATE_CURRENT)
            schedule.id?.let { id ->
                NotificationUtils.notify(id) { param ->
                    param.setAutoCancel(false)
                    param.setSmallIcon(R.drawable.notification_icon)
                    param.setContentTitle(schedule.title)
                    param.setContentText(schedule.detailed)
                    param.setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
                    param.setContentIntent(toRemindDialog)
                }
            }
            NotificationUtils.setNotificationBarVisibility(true)
            context?.sendBroadcast(remindDialogReceiver)
        }
    }
}
