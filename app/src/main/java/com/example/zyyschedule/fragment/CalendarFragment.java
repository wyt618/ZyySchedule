package com.example.zyyschedule.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zyyschedule.R;
import com.example.zyyschedule.databinding.CalendarFragmentBinding;
import com.example.zyyschedule.viewmodel.CalendarViewModel;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

public class CalendarFragment extends Fragment implements View.OnClickListener, CalendarView.OnCalendarSelectListener{

    private CalendarViewModel vm;
    private CalendarFragmentBinding binding;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.calendar_fragment, container, false);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(CalendarViewModel.class);
        binding.flCurrent.setOnClickListener(this);
        binding.tvYear.setText(String.valueOf(binding.calendarView.getCurYear()));
        binding.tvMonthDay.setText(binding.calendarView.getCurMonth() + "月" + binding.calendarView.getCurDay() + "日");
        binding.tvLunar.setText("今日");
        binding.calendarView.setOnCalendarSelectListener(this);
        binding.setVm(vm);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_current:
                binding.calendarView.scrollToCurrent();
                break;

        }
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        binding.tvLunar.setVisibility(View.VISIBLE);
        binding.tvYear.setVisibility(View.VISIBLE);
        binding.tvMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        binding.tvYear.setText(String.valueOf(calendar.getYear()));
        binding.tvLunar.setText(calendar.getLunar());
    }
}