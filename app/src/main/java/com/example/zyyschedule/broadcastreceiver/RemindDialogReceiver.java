package com.example.zyyschedule.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.databinding.RemindGlobalDialogBinding;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

public class RemindDialogReceiver extends BroadcastReceiver {
    private RemindGlobalDialogBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        Gson gson = new Gson();
        String strSchedule = intent.getStringExtra("remindSchedule");
        Schedule schedule = gson.fromJson(strSchedule, Schedule.class);
        WindowManager systemService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.remind_global_dialog, null, false);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //这个地方一定要这样设置，不然要么是布局之外不能接受事件，要么是布局里面和返回按键接收不到事件。
        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        // 设置窗口宽度和高度
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.format = PixelFormat.RGBA_8888;//设置WindowManager背景透明
        binding.getRoot().setOnTouchListener((v, event) -> {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Rect rect = new Rect();
            binding.remindDialog.getGlobalVisibleRect(rect);
            if (!rect.contains(x, y)) {
                systemService.removeView(binding.getRoot());
            }
            binding.getRoot().performClick();
            return true;
        });//设置点击移除提醒弹窗
        binding.getRoot().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                systemService.removeView(binding.getRoot());
                return true;
            }
            return false;
        });//设置back退出弹窗
        Drawable flagDrawable = ContextCompat.getDrawable(context, R.drawable.priority_flag);
        if (flagDrawable != null) {
            switch (schedule.getPriority()) {
                case 0:
                    flagDrawable.setTint(ContextCompat.getColor(context, R.color.priority_null));
                    break;
                case 1:
                    flagDrawable.setTint(ContextCompat.getColor(context, R.color.priority_low));
                    break;
                case 2:
                    flagDrawable.setTint(ContextCompat.getColor(context, R.color.priority_middle));
                    break;
                case 3:
                    flagDrawable.setTint(ContextCompat.getColor(context, R.color.priority_high));
                    break;
            }
        }
        binding.priorityIcon.setImageDrawable(flagDrawable);
        if (schedule.getPriority() != 0) {
            binding.priorityIcon.setVisibility(View.VISIBLE);
        }
        binding.date.setText(checkNowDay(schedule.getStarttime()));
        systemService.addView(binding.getRoot(), params);
    }

    private String checkNowDay(String scheduleDate) {
        StringBuilder dateText = new StringBuilder();
        Date nowDay = new Date();
        Calendar calendar = Calendar.getInstance();//日历对象
        calendar.setTime(nowDay);
        if (calendar.get(Calendar.YEAR) != Integer.parseInt(scheduleDate.substring(0, 4))) {
            dateText.append(scheduleDate.substring(0, 4)).append("年");
        }
        if (calendar.get(Calendar.MONTH) + 1 == Integer.parseInt(scheduleDate.substring(5, 7))
                && calendar.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(scheduleDate.substring(8, 10))) {
            dateText.append("今天，");
        } else {
            dateText.append(scheduleDate.substring(5, 7)).append("月").append(scheduleDate.substring(8, 10)).append("日,");
        }
        dateText.append(scheduleDate.substring(scheduleDate.length() - 8, scheduleDate.length() - 3));
        return dateText.toString();
    }
}
