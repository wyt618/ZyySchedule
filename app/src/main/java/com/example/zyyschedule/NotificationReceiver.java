package com.example.zyyschedule;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.zyyschedule.database.Schedule;
import com.google.gson.Gson;

import java.util.Calendar;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {
    int  NOTIFICATION_CODE = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        NOTIFICATION_CODE++;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Gson gson = new Gson();
        String strSchedule = intent.getStringExtra("remind_schedule");
        Schedule schedule = gson.fromJson(strSchedule, Schedule.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0);
        if(intent.getAction().equals("Notification_Receiver")){
              Notification.Builder notification = new Notification.Builder(context);
              notification.setAutoCancel(true);
              notification.setSmallIcon(R.drawable.notification_icon);
              notification.setContentTitle(schedule.getTitle());
              notification.setContentText(schedule.getDetailed());
              notification.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
              notification.setWhen(calendar.getTimeInMillis());
              notificationManager.notify(NOTIFICATION_CODE, notification.build());
              Log.i("label",schedule.getTitle());
        }
    }
}
