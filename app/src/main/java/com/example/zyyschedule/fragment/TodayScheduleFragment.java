package com.example.zyyschedule.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.zyyschedule.R;
import com.example.zyyschedule.adapter.ScheduleAdapter;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.databinding.FinishScheduleFootBinding;
import com.example.zyyschedule.databinding.FragmentTodayScheduleBinding;
import com.example.zyyschedule.databinding.ScheduleFootBinding;
import com.example.zyyschedule.databinding.ScheduleListFinishHeadBinding;
import com.example.zyyschedule.databinding.ScheduleListHeadBinding;
import com.example.zyyschedule.viewmodel.CalendarViewModel;

import java.util.Calendar;
import java.util.List;

public class TodayScheduleFragment extends Fragment {
    private FragmentTodayScheduleBinding binding;
    private CalendarViewModel vm;
    ScheduleAdapter scheduleAdapter;
    ScheduleAdapter finishScheduleAdapter;
    private List<Schedule> Schedules;
    private List<Schedule> finishSchedules;
    private ScheduleListHeadBinding scheduleHeadBinding;
    private ScheduleFootBinding scheduleFootBinding;
    private ScheduleListFinishHeadBinding scheduleListFinishHeadBinding;
    private FinishScheduleFootBinding finishScheduleFootBinding;
    public TodayScheduleFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_today_schedule,container,false);
        scheduleHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_head, container, false);
        scheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_foot, container, false);
        scheduleListFinishHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_finish_head, container, false);
        finishScheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.finish_schedule_foot, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(CalendarViewModel.class);
        LinearLayoutManager ToDayLayoutManager = new LinearLayoutManager(getContext());
        ToDayLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager FinishLayoutManager = new LinearLayoutManager(getContext());
        FinishLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.todayScheduleList.setLayoutManager(ToDayLayoutManager);
        binding.finishScheduleList.setLayoutManager(FinishLayoutManager);
        scheduleAdapter = new ScheduleAdapter(R.layout.schedule_item);
        scheduleAdapter.setOwner(this);
        scheduleAdapter.setmContext(getContext());
        scheduleAdapter.setHeaderView(scheduleHeadBinding.getRoot());
        scheduleAdapter.addFooterView(scheduleFootBinding.getRoot());
        finishScheduleAdapter = new ScheduleAdapter(R.layout.schedule_item);
        finishScheduleAdapter.setOwner(this);
        finishScheduleAdapter.setmContext(getContext());
        finishScheduleAdapter.setHeaderView(scheduleListFinishHeadBinding.getRoot());
        finishScheduleAdapter.setFooterView(finishScheduleFootBinding.getRoot());
        binding.todayScheduleList.setAdapter(scheduleAdapter);
        binding.finishScheduleList.setAdapter(finishScheduleAdapter);
        scheduleHeadBinding.scheduleListHead.setText(R.string.title_today);
        UpdateScheduleList();
    }
    private void UpdateScheduleList() {
        Calendar calendar = Calendar.getInstance();
        String Day = "%" + calendar.get(Calendar.YEAR) + "-" + ProcessingTime(calendar.get(Calendar.MONTH)+1) + "-" + ProcessingTime(calendar.get(Calendar.DAY_OF_MONTH)) + "%";
        vm.getUnfinishedScheduleOfDay(Day).observe(getViewLifecycleOwner(), schedules -> {
            for (int i = 0; i < schedules.size(); i++) {
                schedules.get(i).setChecked(false);
            }
            if (schedules.size() == 0) {
                scheduleHeadBinding.getRoot().setVisibility(View.GONE);
                scheduleFootBinding.getRoot().setVisibility(View.GONE);
            } else {
                scheduleHeadBinding.getRoot().setVisibility(View.VISIBLE);
                scheduleFootBinding.getRoot().setVisibility(View.VISIBLE);
            }
            scheduleAdapter.setList(schedules);
            Schedules = scheduleAdapter.getData();
        });
        vm.getFinishedScheduleOfDay(Day).observe(getViewLifecycleOwner(), schedules -> {
            for (int i = 0; i < schedules.size(); i++) {
                schedules.get(i).setChecked(true);
            }
            if (schedules.size() == 0) {
                scheduleListFinishHeadBinding.getRoot().setVisibility(View.GONE);
                finishScheduleFootBinding.getRoot().setVisibility(View.GONE);
            } else {
                scheduleListFinishHeadBinding.getRoot().setVisibility(View.VISIBLE);
                finishScheduleFootBinding.getRoot().setVisibility(View.VISIBLE);
            }
            finishScheduleAdapter.setList(schedules);
            finishSchedules = finishScheduleAdapter.getData();
        });
    }

    private String ProcessingTime(int time) {
        String startTime;
        if (time < 10) {
            startTime = "0" + time;
        } else {
            startTime = String.valueOf(time);
        }
        return startTime;
    }
}