package com.example.zyyschedule.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Schedule;
import com.google.gson.Gson;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Gson gson = new Gson();
        String strSchedule = intent.getStringExtra("remindSchedule");
        Schedule schedule = gson.fromJson(strSchedule, Schedule.class);
        if (intent.getAction().equals("Notification_Receiver")) {
            Notification.Builder notification = new Notification.Builder(context);
            notification.setAutoCancel(false);
            notification.setSmallIcon(R.drawable.notification_icon);
            notification.setContentTitle(schedule.getTitle());
            notification.setContentText(schedule.getDetailed());
            notification.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            Intent remindDialogReceiver = new Intent("com.example.zyyschedule.broadcastreceiver.RemindDialogReceiver");
            remindDialogReceiver.putExtra("remindSchedule", intent.getStringExtra("remindSchedule"));
            PendingIntent toRemindDialog = PendingIntent.getBroadcast(context, intent.getIntExtra("PendingIntentCode",0)+10000, remindDialogReceiver, 0);
            notification.setContentIntent(toRemindDialog);
            notificationManager.notify(schedule.getId(), notification.build());
            context.sendBroadcast(remindDialogReceiver);
        }
    }

}
