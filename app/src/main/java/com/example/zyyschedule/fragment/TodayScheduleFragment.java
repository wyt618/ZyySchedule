package com.example.zyyschedule.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.example.zyyschedule.R;
import com.example.zyyschedule.adapter.ScheduleAdapter;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.databinding.FragmentTodayScheduleBinding;

public class TodayScheduleFragment extends Fragment {
    private FragmentTodayScheduleBinding binding;
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
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager toDayLayoutManager = new LinearLayoutManager(getContext());
        toDayLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.todaySchedule.setLayoutManager(toDayLayoutManager);
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(R.layout.schedule_item);
    }
}