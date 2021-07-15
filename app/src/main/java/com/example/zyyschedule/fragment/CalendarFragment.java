package com.example.zyyschedule.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.zyyschedule.R;
import com.example.zyyschedule.databinding.CalendarFragmentBinding;
import com.example.zyyschedule.viewmodel.CalendarViewModel;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

public class CalendarFragment extends Fragment implements View.OnClickListener, CalendarView.OnCalendarSelectListener{

    private CalendarViewModel vm;
    private CalendarFragmentBinding binding;
    private View dialog;
    private DatePicker datePicker;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        binding = DataBindingUtil.inflate(inflater,R.layout.calendar_fragment, container, false);
        dialog = inflater.inflate(R.layout.dialog_date,null);
        datePicker = dialog.findViewById(R.id.date_picker);
        /**
         * 实现长按日期跳转
         */
        binding.tvMonthDay.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                if(dialog.getParent()!=null){
                    ViewGroup vg = (ViewGroup)dialog.getParent();
                    vg.removeView(dialog);
                }
                builder.setView(dialog)
                        .setTitle(R.string.clendar_dialog_title)
                        .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                binding.calendarView.scrollToCalendar(datePicker.getYear(),datePicker.getMonth()+1,datePicker.getDayOfMonth());
                                Toast.makeText(getContext(),datePicker.getYear()+"年"+String.valueOf(datePicker.getMonth()+1)+"月"+datePicker.getDayOfMonth()+"日",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                return true;
            }
        });

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