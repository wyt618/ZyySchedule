package com.example.zyyschedule.adapter;

import android.content.Context;
import android.widget.CheckBox;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.viewmodel.CalendarViewModel;

public class ScheduleAdapter extends BaseQuickAdapter<Schedule, BaseViewHolder> {
    private ViewModelStoreOwner owner;
    private CalendarViewModel vm;
    private Context mContext;

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
        vm = new ViewModelProvider(owner).get(CalendarViewModel.class);
        helper.setText(R.id.schedule_title, item.getTitle());
        helper.setText(R.id.schedule_time, item.getStarttime().substring(item.getStarttime().length()-5));
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
        if (item.getState().equals("1")) {
            helper.setTextColor(R.id.schedule_title, ContextCompat.getColor(mContext, R.color.color_schedule_grey));
        } else {
            helper.setTextColor(R.id.schedule_title, ContextCompat.getColor(mContext, R.color.textColor));
        }
    }
}
