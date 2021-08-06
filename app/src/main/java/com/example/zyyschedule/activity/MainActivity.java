package com.example.zyyschedule.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.zyyschedule.NotificationReceiver;
import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.databinding.ActivityMainBinding;
import com.example.zyyschedule.viewmodel.MainActivityViewModel;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private long firstTime = 0;
    private MainActivityViewModel vm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        vm = new ViewModelProvider(this).get(MainActivityViewModel.class);
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(binding.bottomNavigationView.getMenu()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, configuration);
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        vm.getALLUnFinishOfRemind().observe(this, schedules -> {
            for(int i=0;i<schedules.size();i++){
                if(!schedules.get(i).getRemind().isEmpty()){
                    setNotificationRemind(schedules.get(i));
                }
            }
        });

    }

    //双击退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, R.string.exit_message, Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setNotificationRemind(Schedule schedule){
        String[] remind = schedule.getRemind().split(",");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat std = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Date now = new Date();
        for (String s : remind) {
            try {
                date = std.parse(s);
            } catch (Exception ignored) {
            }
            if (date.getTime() > now.getTime()) {
                Gson gson = new Gson();
                Intent intent = new Intent(this, NotificationReceiver.class);
                intent.setAction("Notification_Receiver");
                intent.putExtra("remind_schedule", gson.toJson(schedule));
                intent.putExtra("notification_time", date);
                PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, date.getTime(), sender);
                Log.i("label", date.toString());
                Log.i("label", gson.toJson(schedule));
            }
        }
    }
}