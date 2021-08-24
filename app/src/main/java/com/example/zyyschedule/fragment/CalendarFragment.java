package com.example.zyyschedule.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.zyyschedule.PriorityBean;
import com.example.zyyschedule.R;
import com.example.zyyschedule.RemindBean;
import com.example.zyyschedule.activity.AddLabelActivity;
import com.example.zyyschedule.adapter.LabelAdapter;
import com.example.zyyschedule.adapter.PriorityListAdapter;
import com.example.zyyschedule.adapter.RemindAdapter;
import com.example.zyyschedule.adapter.ScheduleAdapter;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.databinding.AddScheduleBinding;
import com.example.zyyschedule.databinding.AllLabelDialogBinding;
import com.example.zyyschedule.databinding.CalendarFragmentBinding;
import com.example.zyyschedule.databinding.FinishScheduleFootBinding;
import com.example.zyyschedule.databinding.PriorityDialogBinding;
import com.example.zyyschedule.databinding.RemindDialogBinding;
import com.example.zyyschedule.databinding.RemindListHeadBinding;
import com.example.zyyschedule.databinding.ScheduleFootBinding;
import com.example.zyyschedule.databinding.ScheduleListFinishHeadBinding;
import com.example.zyyschedule.databinding.ScheduleListHeadBinding;
import com.example.zyyschedule.databinding.TimepickerDialogBinding;
import com.example.zyyschedule.viewmodel.CalendarViewModel;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CalendarFragment extends Fragment implements View.OnClickListener, CalendarView.OnCalendarSelectListener {

    private CalendarViewModel vm;
    private CalendarFragmentBinding binding;
    private View dialog;
    private DatePicker datePicker;
    private AlertDialog.Builder builder;
    private AddScheduleBinding addScheduleBinding;
    private TimepickerDialogBinding timePickerBinding;
    private PriorityDialogBinding priorityDialogBinding;
    private int selectYear;
    private int selectMonth;
    private int selectDay;
    private java.util.Calendar time;
    private PriorityListAdapter priorityListAdapter;
    private AlertDialog priorityDialog;
    private AllLabelDialogBinding labelBinding;
    private RemindDialogBinding remindDialogBinding;
    private final LabelAdapter labelAdapter = new LabelAdapter(R.layout.label_item);
    private final RemindAdapter remindAdapter = new RemindAdapter(R.layout.remind_item);
    private AlertDialog labelChoose;
    private View labelDialogHead;
    private AlertDialog addSchedule;
    private AlertDialog remindDialog;
    private RemindListHeadBinding remindListHeadBinding;
    private ScheduleAdapter scheduleAdapter;
    private ScheduleListHeadBinding scheduleListHeadBinding;
    private ScheduleAdapter finishScheduleAdapter;
    private ScheduleFootBinding scheduleFootBinding;
    private ScheduleListFinishHeadBinding scheduleListFinishHeadBinding;
    private FinishScheduleFootBinding finishScheduleFootBinding;
    private List<Schedule> Schedules;
    private List<Schedule> finishSchedules;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getContext());
        binding = DataBindingUtil.inflate(inflater, R.layout.calendar_fragment, container, false);
        dialog = inflater.inflate(R.layout.dialog_date, null);
        labelDialogHead = inflater.inflate(R.layout.label_dialog_head, null);
        addScheduleBinding = DataBindingUtil.inflate(inflater, R.layout.add_schedule, container, false);
        timePickerBinding = DataBindingUtil.inflate(inflater, R.layout.timepicker_dialog, container, false);
        priorityDialogBinding = DataBindingUtil.inflate(inflater, R.layout.priority_dialog, container, false);
        labelBinding = DataBindingUtil.inflate(inflater, R.layout.all_label_dialog, container, false);
        remindDialogBinding = DataBindingUtil.inflate(inflater, R.layout.remind_dialog, container, false);
        remindListHeadBinding = DataBindingUtil.inflate(inflater, R.layout.remind_list_head, container, false);
        scheduleListHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_head, container, false);
        scheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_foot, container, false);
        scheduleListFinishHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_finish_head, container, false);
        finishScheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.finish_schedule_foot, container, false);
        datePicker = dialog.findViewById(R.id.date_picker);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        time = java.util.Calendar.getInstance();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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
        addScheduleBinding.labelButton.setOnClickListener(this);
        addScheduleBinding.scheduleLabel.setOnClickListener(this);
        addScheduleBinding.remindButton.setOnClickListener(this);
        addScheduleBinding.remindText.setOnClickListener(this);
        addScheduleBinding.sendSchedule.setOnClickListener(this);
        addScheduleBinding.setVm(vm);
        addScheduleBinding.setLifecycleOwner(this);
        timePickerBinding.hourPicker.setMaxValue(23);
        timePickerBinding.hourPicker.setMinValue(0);
        timePickerBinding.hourPicker.setValue(0);
        timePickerBinding.minePicker.setMinValue(0);
        timePickerBinding.minePicker.setMaxValue(59);
        timePickerBinding.minePicker.setValue(0);
        labelBinding.labelList.setLayoutManager(layoutManager);
        labelBinding.labelList.setAdapter(labelAdapter);
        LinearLayoutManager remindLayoutManager = new LinearLayoutManager(getContext());
        remindLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        remindDialogBinding.remindChooseList.setLayoutManager(remindLayoutManager);
        remindDialogBinding.remindChooseList.setAdapter(remindAdapter);
        ArrayList<RemindBean> remindlist = vm.remindListData();
        remindAdapter.setList(remindlist);
        remindAdapter.setHeader(remindListHeadBinding);
        remindAdapter.setHeaderView(remindListHeadBinding.getRoot());
        scheduleListHeadBinding.scheduleListHead.setText(selectMonth + "月" + selectDay + "日");
        LinearLayoutManager scheduleLayoutManager = new LinearLayoutManager(getContext());
        scheduleLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.scheduleList.setLayoutManager(scheduleLayoutManager);
        LinearLayoutManager finishscheduleLayoutManager = new LinearLayoutManager(getContext());
        finishscheduleLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.finishScheduleList.setLayoutManager(finishscheduleLayoutManager);
        finishScheduleAdapter = new ScheduleAdapter(R.layout.schedule_item);
        finishScheduleAdapter.setOwner(this);
        finishScheduleAdapter.setmContext(getContext());
        finishScheduleAdapter.addHeaderView(scheduleListFinishHeadBinding.getRoot());
        finishScheduleAdapter.addFooterView(finishScheduleFootBinding.getRoot());
        finishScheduleAdapter.setEmptyView(R.layout.schedule_empty);
        scheduleAdapter = new ScheduleAdapter(R.layout.schedule_item);
        scheduleAdapter.addHeaderView(scheduleListHeadBinding.getRoot());
        scheduleAdapter.addFooterView(scheduleFootBinding.getRoot());
        scheduleAdapter.setEmptyView(R.layout.schedule_empty);
        scheduleAdapter.setOwner(this);
        scheduleAdapter.setmContext(getContext());
        binding.scheduleList.setAdapter(scheduleAdapter);
        binding.finishScheduleList.setAdapter(finishScheduleAdapter);
        scheduleListHeadBinding.scheduleListHead.setText(selectMonth + "月" + selectDay + "日");
        labelDialogHead.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddLabelActivity.class);
            startActivity(intent);
        });
        scheduleListHeadBinding.scheduleDeleteBack.setOnClickListener(v -> {
            scheduleListHeadBinding.deleteSchedule.setVisibility(View.GONE);
            scheduleListHeadBinding.scheduleListHead.setVisibility(View.VISIBLE);
            scheduleListHeadBinding.scheduleDeleteBack.setVisibility(View.GONE);
            binding.fabBtn.setVisibility(View.VISIBLE);
            for (int i = 0; i < Schedules.size(); i++) {
                Schedules.get(i).setEditor(false);
            }
            for (int i = 0; i < finishSchedules.size(); i++) {
                finishSchedules.get(i).setEditor(false);
            }
            scheduleAdapter.notifyDataSetChanged();
            finishScheduleAdapter.notifyDataSetChanged();
        });
        scheduleListFinishHeadBinding.scheduleDeleteBack.setOnClickListener(v -> {
            scheduleListFinishHeadBinding.deleteSchedule.setVisibility(View.GONE);
            scheduleListFinishHeadBinding.scheduleListFinish.setVisibility(View.VISIBLE);
            scheduleListFinishHeadBinding.scheduleDeleteBack.setVisibility(View.GONE);
            binding.fabBtn.setVisibility(View.VISIBLE);
            for (int i = 0; i < Schedules.size(); i++) {
                Schedules.get(i).setEditor(false);
            }
            for (int i = 0; i < finishSchedules.size(); i++) {
                finishSchedules.get(i).setEditor(false);
            }
            scheduleAdapter.notifyDataSetChanged();
            finishScheduleAdapter.notifyDataSetChanged();
        });
        scheduleListHeadBinding.deleteSchedule.setOnClickListener(v -> gotoDeleteDialog());
        scheduleListFinishHeadBinding.deleteSchedule.setOnClickListener(v -> gotoDeleteDialog());


        //对标签数据进行监听，刷新标签选择对话框，对话框点击事件
        vm.getAllLabel().observe(getViewLifecycleOwner(), labels -> {
            labelAdapter.setList(labels);
            labelAdapter.notifyDataSetChanged();
            labelAdapter.setOnItemClickListener((adapter, view, position) -> {
                TextView labelname = view.findViewById(R.id.label_name);
                TextView labelid = view.findViewById(R.id.label_id);
                addScheduleBinding.scheduleLabelId.setText(labelid.getText());
                vm.getLabel().setValue(labelname.getText().toString());
                labelChoose.dismiss();
            });
            if (labelDialogHead.getParent() != null) {
                ViewGroup vg = (ViewGroup) labelDialogHead.getParent();
                vg.removeView(labelDialogHead);
            }
            labelAdapter.addHeaderView(labelDialogHead);
        });

        //设置新增日程对话框有内容时唤醒按钮
        addScheduleBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    addScheduleBinding.sendSchedule.setImageResource(R.drawable.ic_baseline_send_click_24);
                    addScheduleBinding.sendSchedule.setClickable(true);
                } else {
                    addScheduleBinding.sendSchedule.setImageResource(R.drawable.ic_baseline_send_24);
                    addScheduleBinding.sendSchedule.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //实现长按日期跳转
        binding.tvMonthDay.setOnLongClickListener(v -> {
            if (dialog.getParent() != null) {
                ViewGroup vg = (ViewGroup) dialog.getParent();
                vg.removeView(dialog);
            }
            builder.setView(dialog)
                    .setTitle(R.string.clendar_dialog_title)
                    .setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                        binding.calendarView.scrollToCalendar(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                        Toast.makeText(getContext(), datePicker.getYear() + "年" + (datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日", Toast.LENGTH_SHORT).show();
                    })
                    .setNeutralButton(R.string.dialog_button_cancel, (dialog, which) -> dialog.dismiss());
            builder.create().show();
            return true;
        });
        remindListHeadBinding.remindHeadBox.setChecked(true);
        remindListHeadBinding.remindHeadBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                remindListHeadBinding.remindHeadBox.setClickable(false);
                remindAdapter.setAddRemind(new StringBuffer("无提醒"));
                for (int i = 0; i < remindAdapter.getData().size(); i++) {
                    remindAdapter.getData().get(i).setRemindIsChecked(false);
                    remindAdapter.notifyDataSetChanged();
                }
            } else {
                remindListHeadBinding.remindHeadBox.setClickable(true);
            }
        });
        //完成和未完成日程item长按事件
        scheduleAdapter.setOnItemLongClickListener((adapter, view, position) -> {
//            LiveEventBus
//                    .get("some_key")
//                    .post("gone_navigation");
            binding.fabBtn.setVisibility(View.GONE);
            scheduleListHeadBinding.deleteSchedule.setVisibility(View.VISIBLE);
            scheduleListHeadBinding.scheduleListHead.setVisibility(View.GONE);
            scheduleListHeadBinding.scheduleDeleteBack.setVisibility(View.VISIBLE);
            for (int i = 0; i < Schedules.size(); i++) {
                Schedules.get(i).setEditor(true);
            }
            for (int i = 0; i < finishSchedules.size(); i++) {
                finishSchedules.get(i).setEditor(true);
            }
            adapter.notifyDataSetChanged();
            finishScheduleAdapter.notifyDataSetChanged();
            return true;
        });
        finishScheduleAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            binding.fabBtn.setVisibility(View.GONE);
            scheduleListHeadBinding.deleteSchedule.setVisibility(View.VISIBLE);
            scheduleListHeadBinding.scheduleListHead.setVisibility(View.GONE);
            scheduleListHeadBinding.scheduleDeleteBack.setVisibility(View.VISIBLE);
            if (scheduleAdapter.getData().size() == 0) {
                scheduleListFinishHeadBinding.deleteSchedule.setVisibility(View.VISIBLE);
                scheduleListFinishHeadBinding.scheduleListFinish.setVisibility(View.GONE);
                scheduleListFinishHeadBinding.scheduleDeleteBack.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < Schedules.size(); i++) {
                Schedules.get(i).setEditor(true);
            }
            for (int i = 0; i < finishSchedules.size(); i++) {
                finishSchedules.get(i).setEditor(true);
            }
            adapter.notifyDataSetChanged();
            scheduleAdapter.notifyDataSetChanged();
            return true;
        });
        UpdateScheduleList();
        setCalendarTag();
    }


    @SuppressLint("NonConstantResourceId")
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
            case R.id.textTime:
                gotoGetTime();
                break;
            case R.id.priority_button:
            case R.id.text_priority:
                gotoPriority();
                break;
            case R.id.label_button:
            case R.id.schedule_label:
                gotoAllLabel();
                break;
            case R.id.remind_button:
            case R.id.remind_text:
                gotoAddRemind();
                break;
            case R.id.send_schedule:
                AddSchedule();
                break;
        }
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
    }

    private void setCalendarTag() {
        int redColor = 0xFF0000;
        Map<String, Calendar> map = new HashMap<>();
        vm.getScheduleDayOfTag().observe(getViewLifecycleOwner(), strings -> {
            for (int i = 0; i < strings.size(); i++) {
                map.put(getSchemeCalendar(Integer.parseInt(strings.get(i).substring(0, 4)), Integer.parseInt(strings.get(i).substring(5, 7)), Integer.parseInt(strings.get(i).substring(8, 10)), redColor).toString(),
                        getSchemeCalendar(Integer.parseInt(strings.get(i).substring(0, 4)), Integer.parseInt(strings.get(i).substring(5, 7)), Integer.parseInt(strings.get(i).substring(8, 10)), redColor));
            }
            binding.calendarView.setSchemeDate(map);
        });
    }

    //为日历添加标记
    private Calendar getSchemeCalendar(int year, int month, int day, int color) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }


    @SuppressLint("SetTextI18n")
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
        scheduleListHeadBinding.scheduleListHead.setText(selectMonth + "月" + selectDay + "日");
        scheduleListHeadBinding.deleteSchedule.setVisibility(View.GONE);
        scheduleListHeadBinding.scheduleListHead.setVisibility(View.VISIBLE);
        scheduleListHeadBinding.scheduleDeleteBack.setVisibility(View.GONE);
        UpdateScheduleList();
        setCalendarTag();
    }

    private void gotoAddSchedule() {
        binding.fabBtn.setVisibility(View.GONE);
        addScheduleBinding.sendSchedule.setClickable(!addScheduleBinding.editText.getText().toString().trim().isEmpty());
        if (addScheduleBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) addScheduleBinding.getRoot().getParent();
            vg.removeView(addScheduleBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setView(addScheduleBinding.getRoot());
        addSchedule = builder.create();
        addSchedule.show();
        Window window = addSchedule.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager m = getActivity().getWindowManager();
        DisplayMetrics d = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(d);
        WindowManager.LayoutParams p = addSchedule.getWindow().getAttributes();
        p.width = d.widthPixels;
        addSchedule.getWindow().setAttributes(p);
        addSchedule.getWindow().setBackgroundDrawableResource(R.drawable.add_schedule);
        addSchedule.setOnDismissListener(dialog -> binding.fabBtn.setVisibility(View.VISIBLE));
    }

    private void gotoGetTime() {
        time = java.util.Calendar.getInstance();
        timePickerBinding.hourPicker.setValue(time.get(java.util.Calendar.HOUR_OF_DAY));
        timePickerBinding.minePicker.setValue(time.get(java.util.Calendar.MINUTE));
        if (timePickerBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) timePickerBinding.getRoot().getParent();
            vg.removeView(timePickerBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setView(timePickerBinding.getRoot())
                .setTitle(R.string.add_schedule_timepicker)
                .setNeutralButton(R.string.dialog_button_cancel, (dialog, which) -> dialog.dismiss())
                .setPositiveButton(R.string.dialog_button_ok, (dialog, which) ->
                        vm.getAddScheduleTime().setValue(ProcessingTime(timePickerBinding.hourPicker.getValue()) + ":" + ProcessingTime(timePickerBinding.minePicker.getValue()))
                )
                .setOnDismissListener(dialog -> {
                    if (addScheduleBinding.textTime.getText().toString().equals("00:00")) {
                        timePickerBinding.hourPicker.setValue(0);
                        timePickerBinding.minePicker.setValue(0);
                    } else {
                        timePickerBinding.hourPicker.setValue(Integer.parseInt(Objects.requireNonNull(vm.getAddScheduleTime().getValue()).substring(0, 2)));
                        timePickerBinding.minePicker.setValue(Integer.parseInt(vm.getAddScheduleTime().getValue().substring(3)));
                    }
                    vm.getAddScheduleTime().setValue(ProcessingTime(timePickerBinding.hourPicker.getValue()) + ":" + ProcessingTime(timePickerBinding.minePicker.getValue()));
                });
        builder.create().show();
    }

    private void gotoPriority() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        priorityDialogBinding.priorityList.setLayoutManager(layoutManager);
        ArrayList<PriorityBean> priorityData = vm.priorityListData();
        priorityListAdapter = new PriorityListAdapter(R.layout.priority_item);
        priorityListAdapter.setList(priorityData);
        priorityListAdapter.getMContext(getContext());
        priorityListAdapter.setOnItemClickListener((adapter, view, position) -> {
            TextView text = view.findViewById(R.id.priority_title);
            ImageView flag = view.findViewById(R.id.priority_flag);
            addScheduleBinding.textPriority.setTextColor(text.getTextColors());
            vm.getPriority().setValue(text.getText().toString());
            vm.getPriorityid().setValue(position);
            addScheduleBinding.priorityButton.setImageDrawable((flag.getDrawable()));
            priorityDialog.dismiss();
        });
        priorityDialogBinding.priorityList.setAdapter(priorityListAdapter);
        if (priorityDialogBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) priorityDialogBinding.getRoot().getParent();
            vg.removeView(priorityDialogBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.priority_dialog_title)
                .setView(priorityDialogBinding.getRoot());
        priorityDialog = builder.create();
        priorityDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        priorityDialog.show();
    }

    private void gotoAllLabel() {
        if (labelBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) labelBinding.getRoot().getParent();
            vg.removeView(labelBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.label_dialog_title)
                .setView(labelBinding.getRoot());
        labelChoose = builder.create();
        labelChoose.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        labelChoose.show();
        WindowManager m = getActivity().getWindowManager();
        DisplayMetrics d = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(d);
        WindowManager.LayoutParams p = labelChoose.getWindow().getAttributes();
        p.width = d.widthPixels / 3;
        p.height = d.heightPixels / 2;
        labelChoose.getWindow().setAttributes(p);
    }

    private void gotoAddRemind() {
        int flag = 0;
        for (int i = 0; i < remindAdapter.getData().size(); i++) {
            if (!remindAdapter.getData().get(i).getRemindIsChecked()) {
                flag = flag + 1;
            }
        }
        remindListHeadBinding.remindHeadBox.setClickable(flag != remindAdapter.getData().size());

        if (remindDialogBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) remindDialogBinding.getRoot().getParent();
            vg.removeView(remindDialogBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.remind_dialog_title)
                .setView(remindDialogBinding.getRoot())
                .setPositiveButton(R.string.dialog_button_finish, (dialog, which) -> {
                    if (remindAdapter.getAddRemind().toString().equals("无提醒")) {
                        addScheduleBinding.remindText.setText(remindAdapter.getAddRemind().toString());
                    } else {
                        addScheduleBinding.remindText.setText(remindAdapter.getAddRemind().substring(4, remindAdapter.getAddRemind().length()));
                    }
                    getNotification();
                })
                .setNeutralButton(R.string.dialog_button_cancel, (dialog, which) -> dialog.dismiss());
        remindDialog = builder.create();
        remindDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        remindDialog.show();
        WindowManager m = getActivity().getWindowManager();
        DisplayMetrics d = new DisplayMetrics();
        m.getDefaultDisplay().getMetrics(d);
        WindowManager.LayoutParams p = remindDialog.getWindow().getAttributes();
        p.width = d.widthPixels / 3;
        p.height = d.heightPixels / 2;
        remindDialog.getWindow().setAttributes(p);
    }

    //新增日程到数据库
    private void AddSchedule() {
        Schedule schedule = new Schedule();
        String starttime = selectYear + "-" + ProcessingTime(selectMonth) + "-" + ProcessingTime(selectDay) + " " + vm.getAddScheduleTime().getValue() + ":00";
        schedule.setStartTime(starttime);
        schedule.setEndTime(null);
        schedule.setRemind(RemindChangeTime());
        schedule.setTitle(addScheduleBinding.editText.getText().toString());
        schedule.setDetailed(null);
        schedule.setState("0");
        schedule.setPriority(Integer.parseInt(addScheduleBinding.priorityId.getText().toString()));
        if (addScheduleBinding.scheduleLabelId.getText().toString().trim().isEmpty()) {
            schedule.setLabelId(0);
        } else {
            schedule.setLabelId(Integer.parseInt(addScheduleBinding.scheduleLabelId.getText().toString().trim()));
        }
        vm.insertSchedule(schedule);
        UpdateScheduleList();
        addSchedule.dismiss();
        if (!schedule.getRemind().isEmpty()) {
            int RemindCheck = CheckRemindTime(schedule.getRemind());
            if (RemindCheck > 0) {
                Toast.makeText(getContext(), "抱歉，有" + RemindCheck + "条提醒因为超出当前时间无效", Toast.LENGTH_LONG).show();
            }
        }


    }

    //将提醒字符转化为时间字符
    private String RemindChangeTime() {
        String remindtime;
        remindtime = remindAdapter.getAddRemind().toString();
        if (remindtime.equals("无提醒")) {
            remindtime = "";
        } else {
            remindtime = remindtime.replace("无提醒", "");
            remindtime = remindtime.replace(",准时", RemindToTime(1) + ",");
            remindtime = remindtime.replace(",提前1分钟", RemindToTime(2) + ",");
            remindtime = remindtime.replace(",提前5分钟", RemindToTime(3) + ",");
            remindtime = remindtime.replace(",提前10分钟", RemindToTime(4) + ",");
            remindtime = remindtime.replace(",提前15分钟", RemindToTime(5) + ",");
            remindtime = remindtime.replace(",提前20分钟", RemindToTime(6) + ",");
            remindtime = remindtime.replace(",提前25分钟", RemindToTime(7) + ",");
            remindtime = remindtime.replace(",提前30分钟", RemindToTime(8) + ",");
            remindtime = remindtime.replace(",提前45分钟", RemindToTime(9) + ",");
            remindtime = remindtime.replace(",提前1个小时", RemindToTime(10) + ",");
            remindtime = remindtime.replace(",提前2个小时", RemindToTime(11) + ",");
            remindtime = remindtime.replace(",提前3个小时", RemindToTime(12) + ",");
            remindtime = remindtime.replace(",提前12个小时", RemindToTime(13) + ",");
            remindtime = remindtime.replace(",提前1天", RemindToTime(14) + ",");
            remindtime = remindtime.replace(",提前2天", RemindToTime(15) + ",");
            remindtime = remindtime.replace(",提前1周", RemindToTime(16) + ",");
        }

        return remindtime;
    }

    //处理日期数字 例如6月转为06，7分钟转化为07
    private String ProcessingTime(int time) {
        String strtime;
        if (time < 10) {
            strtime = "0" + time;
        } else {
            strtime = String.valueOf(time);
        }
        return strtime;
    }


    //处理提醒与时间的方法
    private String RemindToTime(int remindType) {
        String remindtime;
        Date date = new Date();
        remindtime = selectYear + "-" + selectMonth + "-" + selectDay + " " + addScheduleBinding.textTime.getText() + ":" + "00";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat std = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = std.parse(remindtime);
        } catch (Exception ignored) {
        }
        if (remindType == 1) {
            remindtime = std.format(date);
        } else if (remindType == 2) {
            date.setTime(date.getTime() - 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 3) {
            date.setTime(date.getTime() - 5 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 4) {
            date.setTime(date.getTime() - 10 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 5) {
            date.setTime(date.getTime() - 15 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 6) {
            date.setTime(date.getTime() - 20 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 7) {
            date.setTime(date.getTime() - 25 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 8) {
            date.setTime(date.getTime() - 30 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 9) {
            date.setTime(date.getTime() - 45 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 10) {
            date.setTime(date.getTime() - 60 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 11) {
            date.setTime(date.getTime() - 2 * 60 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 12) {
            date.setTime(date.getTime() - 3 * 60 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 13) {
            date.setTime(date.getTime() - 12 * 60 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 14) {
            date.setTime(date.getTime() - 24 * 60 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 15) {
            date.setTime(date.getTime() - 2 * 24 * 60 * 60 * 1000);
            remindtime = std.format(date);
        } else if (remindType == 16) {
            date.setTime(date.getTime() - 7 * 24 * 60 * 60 * 1000);
            remindtime = std.format(date);
        }
        return remindtime;
    }


    private void UpdateScheduleList() {
        String Day = "%" + selectYear + "-" + ProcessingTime(selectMonth) + "-" + ProcessingTime(selectDay) + "%";
        vm.getUnfinishedScheduleOfDay(Day).observe(getViewLifecycleOwner(), schedules -> {
            for (int i = 0; i < schedules.size(); i++) {
                schedules.get(i).setChecked(false);
            }
            if (schedules.size() == 0) {
                scheduleListHeadBinding.getRoot().setVisibility(View.GONE);
                scheduleFootBinding.getRoot().setVisibility(View.GONE);
            } else {
                scheduleListHeadBinding.getRoot().setVisibility(View.VISIBLE);
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

    //删除日程的对话框
    public void gotoDeleteDialog() {
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete_schedule_title)
                .setMessage(R.string.delete_schedule_message)
                .setPositiveButton(R.string.dialog_button_ok, (dialog, which) -> {
                    for (int i = 0; i < Schedules.size(); i++) {
                        if (Schedules.get(i).isEditorChecked()) {
                            vm.deleteSchedule(Schedules.get(i));
                        }
                    }
                    for (int i = 0; i < finishSchedules.size(); i++) {
                        if (finishSchedules.get(i).isEditorChecked()) {
                            vm.deleteSchedule(finishSchedules.get(i));
                        }
                    }
                    dialog.dismiss();
                    UpdateScheduleList();
                    binding.fabBtn.setVisibility(View.VISIBLE);
                    scheduleListFinishHeadBinding.deleteSchedule.setVisibility(View.GONE);
                    scheduleListFinishHeadBinding.scheduleListFinish.setVisibility(View.VISIBLE);
                    scheduleListFinishHeadBinding.scheduleDeleteBack.setVisibility(View.GONE);
                    scheduleListHeadBinding.deleteSchedule.setVisibility(View.GONE);
                    scheduleListHeadBinding.scheduleListHead.setVisibility(View.VISIBLE);
                    scheduleListHeadBinding.scheduleDeleteBack.setVisibility(View.GONE);
                    setCalendarTag();
                })
                .setNeutralButton(R.string.dialog_button_cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private int CheckRemindTime(String reminds) {
        int RemindCheck = 0;
        Date now = new Date();
        Date date = new Date();
        String[] str = reminds.split(",");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat std = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String s : str) {
            try {
                date = std.parse(s);
            } catch (Exception ignored) {
            }
            if (date.getTime() < now.getTime()) {
                RemindCheck++;
            }
        }
        return RemindCheck;
    }

    //检测通知是否开启的方法
    public static boolean isNotificationEnabled(Context context) {
        boolean isOpened;
        try {
            isOpened = NotificationManagerCompat.from(context).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            isOpened = false;
        }
        return isOpened;
    }

    //当通知未开启时弹出框
    private void getNotification() {
        if (!isNotificationEnabled(getActivity())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setTitle(R.string.notify_authority_dialog_title)
                    .setMessage(R.string.notify_authority_dialog_message)
                    .setNegativeButton(R.string.dialog_button_cancel, (dialog, which) -> dialog.cancel())
                    .setPositiveButton(R.string.notify_authority_dialog_ok_button, (dialog, which) -> {
                        dialog.cancel();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                        startActivity(intent);
                    });
            builder.create().show();
        }
    }
}