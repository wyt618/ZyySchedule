package com.example.zyyschedule;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.zyyschedule.database.Schedule;
import com.google.gson.Gson;

import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(context);
        Gson gson = new Gson();
        Date date = (Date) intent.getSerializableExtra("notification_time");
        String strSchedule = intent.getStringExtra("remind_schedule");
        Schedule schedule = gson.fromJson(strSchedule, Schedule.class);
        if(intent.getAction().equals("Notification_Receiver")){
              notification.setAutoCancel(true);
              notification.setSmallIcon(R.drawable.notification_icon);
              notification.setContentTitle(schedule.getTitle() );
              notification.setContentText(schedule.getDetailed());
              notification.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
              notification.setWhen(date.getTime());
              notificationManager.notify(0, notification.build());
            Log.i("label",schedule.getTitle());
        }
    }
}
