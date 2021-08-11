package com.example.zyyschedule.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.viewmodel.CalendarViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleAdapter extends BaseQuickAdapter<Schedule, BaseViewHolder> {
    private ViewModelStoreOwner owner;
    private CalendarViewModel vm;
    private Context mContext;
    private Date date;

    public ScheduleAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setOwner(ViewModelStoreOwner owner) {
        this.owner = owner;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void convert(BaseViewHolder helper, Schedule item) {

        Date now = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat std = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = std.parse(item.getStarttime());
        } catch (Exception ignored) {
        }

        vm = new ViewModelProvider(owner).get(CalendarViewModel.class);
        helper.setText(R.id.schedule_title, item.getTitle());
        helper.setText(R.id.delete_radio_button, item.getTitle());
        helper.setText(R.id.schedule_date,item.getStarttime().substring(0,4)+"年"
        +Integer.parseInt(item.getStarttime().substring(5,7))+"月"
                +Integer.parseInt(item.getStarttime().substring(8,10))+"日"
        );
        helper.setText(R.id.schedule_time, item.getStarttime().substring(item.getStarttime().length() - 8, item.getStarttime().length() - 3));
        RadioButton radioButton = helper.getView(R.id.delete_radio_button);
        radioButton.setOnCheckedChangeListener(null);
        radioButton.setChecked(item.getEditorChecked());
        CheckBox checkBox = helper.getView(R.id.schedule_title);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(item.getChecked());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            if (isChecked) {
                item.setState("1");
            } else {
                item.setState("0");
            }
            vm.ChangeStateSchedule(item);
        });

        radioButton.setOnClickListener(v -> {
            if (item.getEditorChecked()) {
                item.setEditorChecked(false);
            } else {
                item.setEditorChecked(true);
            }
            radioButton.setChecked(item.getEditorChecked());
        });


        if (item.getState().equals("1")) {
            helper.setTextColor(R.id.schedule_title, ContextCompat.getColor(mContext, R.color.color_schedule_grey));
        } else {
            helper.setTextColor(R.id.schedule_title, Color.BLACK);
            if (date.getTime() <= now.getTime()) {
                helper.setTextColor(R.id.schedule_time, Color.RED);
                helper.setTextColor(R.id.schedule_date,Color.RED);
            } else {
                helper.setTextColor(R.id.schedule_time, Color.BLACK);
                helper.setTextColor(R.id.schedule_date,Color.BLACK);
            }
        }

        if (item.getEditor()) {
            checkBox.setVisibility(View.GONE);
            radioButton.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.VISIBLE);
            radioButton.setVisibility(View.GONE);
        }


    }
}
