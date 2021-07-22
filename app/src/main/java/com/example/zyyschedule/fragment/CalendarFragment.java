package com.example.zyyschedule.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zyyschedule.PriorityBean;
import com.example.zyyschedule.R;
import com.example.zyyschedule.adapter.PriorityListAdapter;
import com.example.zyyschedule.databinding.AddScheduleBinding;
import com.example.zyyschedule.databinding.CalendarFragmentBinding;
import com.example.zyyschedule.databinding.PriorityDialogBinding;
import com.example.zyyschedule.databinding.TimepickerDialogBinding;
import com.example.zyyschedule.viewmodel.CalendarViewModel;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;

public class CalendarFragment extends Fragment implements View.OnClickListener, CalendarView.OnCalendarSelectListener {

    private CalendarViewModel vm;
    private CalendarFragmentBinding binding;
    private View dialog;
    private DatePicker datePicker;
    private AlertDialog.Builder builder;
    private AddScheduleBinding addScheduleBinding;
    private TimepickerDialogBinding timepickerbinding;
    private PriorityDialogBinding priorityDialogBinding;
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    private java.util.Calendar time;
    private PriorityBean priorityBean;
    private PriorityListAdapter priorityListAdapter;
    private AlertDialog prioritydialog;


    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        time = java.util.Calendar.getInstance();
        builder = new AlertDialog.Builder(getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.calendar_fragment, container, false);
        dialog = inflater.inflate(R.layout.dialog_date, null);
        addScheduleBinding = DataBindingUtil.inflate(inflater, R.layout.add_schedule, container, false);
        timepickerbinding = DataBindingUtil.inflate(inflater, R.layout.timepicker_dialog, container, false);
        priorityDialogBinding = DataBindingUtil.inflate(inflater, R.layout.priority_dialog, container, false);
        datePicker = dialog.findViewById(R.id.date_picker);
        /**
         * 实现长按日期跳转
         */
        binding.tvMonthDay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (dialog.getParent() != null) {
                    ViewGroup vg = (ViewGroup) dialog.getParent();
                    vg.removeView(dialog);
                }
                builder.setView(dialog)
                        .setTitle(R.string.clendar_dialog_title)
                        .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                binding.calendarView.scrollToCalendar(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                                Toast.makeText(getContext(), datePicker.getYear() + "年" + String.valueOf(datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日", Toast.LENGTH_SHORT).show();
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
        selectYear = binding.calendarView.getCurYear();
        selectMonth = binding.calendarView.getCurMonth();
        selectDay = binding.calendarView.getCurDay();
        vm = new ViewModelProvider(this).get(CalendarViewModel.class);
        binding.flCurrent.setOnClickListener(this);
        binding.tvYear.setText(String.valueOf(binding.calendarView.getCurYear()));
        binding.tvMonthDay.setText(binding.calendarView.getCurMonth() + "月" + binding.calendarView.getCurDay() + "日");
        binding.tvLunar.setText("今日");
        binding.calendarView.setOnCalendarSelectListener(this);
        binding.fabBtn.setOnClickListener(this);
        binding.setVm(vm);
        binding.setLifecycleOwner(this);
        addScheduleBinding.addScheduleSelectTime.setOnClickListener(this);
        addScheduleBinding.textTime.setOnClickListener(this);
        addScheduleBinding.priorityButton.setOnClickListener(this);
        addScheduleBinding.textPriority.setOnClickListener(this);
        addScheduleBinding.setVm(vm);
        addScheduleBinding.setLifecycleOwner(this);
        timepickerbinding.hourPicker.setMaxValue(23);
        timepickerbinding.hourPicker.setMinValue(00);
        timepickerbinding.hourPicker.setValue(time.get(java.util.Calendar.HOUR_OF_DAY));
        timepickerbinding.minePicker.setMinValue(00);
        timepickerbinding.minePicker.setMaxValue(59);
        timepickerbinding.minePicker.setValue(time.get(java.util.Calendar.MINUTE));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_current:
                binding.calendarView.scrollToCurrent();
                break;
            case R.id.fab_btn:
                gotoAddSchedule();
                vm.addScheduleDateAgo(selectYear, selectMonth, selectDay, binding.calendarView.getCurYear(), binding.calendarView.getCurMonth(), binding.calendarView.getCurDay());
                break;
            case R.id.add_schedule_selectTime:
                gotoGetTime();
                break;
            case R.id.textTime:
                gotoGetTime();
                break;
            case R.id.priority_button:
                gotoPriority();
                break;
            case R.id.text_priority:
                gotoPriority();
                break;
        }
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        selectYear = calendar.getYear();
        selectMonth = calendar.getMonth();
        selectDay = calendar.getDay();
        binding.tvLunar.setVisibility(View.VISIBLE);
        binding.tvYear.setVisibility(View.VISIBLE);
        binding.tvMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        binding.tvYear.setText(String.valueOf(calendar.getYear()));
        binding.tvLunar.setText(calendar.getLunar());
    }

    private void gotoAddSchedule() {
        if (addScheduleBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) addScheduleBinding.getRoot().getParent();
            vg.removeView(addScheduleBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setView(addScheduleBinding.getRoot())
                .setTitle(R.string.add_schedule_title);
        builder.create().show();
    }

    private void gotoGetTime() {
        if (timepickerbinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) timepickerbinding.getRoot().getParent();
            vg.removeView(timepickerbinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setView(timepickerbinding.getRoot())
                .setTitle(R.string.add_schedule_timepicker)
                .setNeutralButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String hour;
                        String time;
                        if (timepickerbinding.hourPicker.getValue() < 10) {
                            hour = String.valueOf("0" + timepickerbinding.hourPicker.getValue());
                        } else {
                            hour = String.valueOf(timepickerbinding.hourPicker.getValue());
                        }
                        if (timepickerbinding.minePicker.getValue() < 10) {
                            time = String.valueOf("0" + timepickerbinding.minePicker.getValue());
                        } else {
                            time = String.valueOf(timepickerbinding.minePicker.getValue());
                        }
                        vm.AddScheduleTime.setValue(hour + ":" + time);
                    }
                });

        builder.create().show();
    }

    private void gotoPriority() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        priorityDialogBinding.priorityList.setLayoutManager(layoutManager);
        ArrayList prioritydata = PriorityListData();
        priorityListAdapter = new PriorityListAdapter(R.layout.priority_item, prioritydata);
        priorityListAdapter.setContext(getContext());
        priorityListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TextView text = view.findViewById(R.id.priority_title);
                ImageView flag = view.findViewById(R.id.priority_flag);
                addScheduleBinding.textPriority.setTextColor(text.getTextColors());
                addScheduleBinding.textPriority.setText(text.getText());
                addScheduleBinding.priorityButton.setImageDrawable((flag.getDrawable()));
                prioritydialog.dismiss();
            }
        });
        priorityDialogBinding.priorityList.setAdapter(priorityListAdapter);
        if (priorityDialogBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) priorityDialogBinding.getRoot().getParent();
            vg.removeView(priorityDialogBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("设置优先级")
                .setView(priorityDialogBinding.getRoot());
        prioritydialog = builder.create();
        prioritydialog.show();
    }

    private ArrayList PriorityListData() {
        ArrayList ary = new ArrayList<>();
        priorityBean = new PriorityBean();
        priorityBean.setPrioritytitle("！无优先级");
        priorityBean.setPrioritytype(1);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPrioritytitle("！低优先级");
        priorityBean.setPrioritytype(2);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPrioritytitle("！中优先级");
        priorityBean.setPrioritytype(3);
        ary.add(priorityBean);
        priorityBean = new PriorityBean();
        priorityBean.setPrioritytitle("！高优先级");
        priorityBean.setPrioritytype(4);
        ary.add(priorityBean);
        return ary;
    }


}