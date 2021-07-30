package com.example.zyyschedule.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.zyyschedule.R;
import com.example.zyyschedule.activity.AddLabelActivity;
import com.example.zyyschedule.adapter.LabelAdapter;
import com.example.zyyschedule.adapter.PriorityListAdapter;
import com.example.zyyschedule.adapter.RemindAdapter;
import com.example.zyyschedule.database.Label;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.databinding.AddScheduleBinding;
import com.example.zyyschedule.databinding.AllLabelDialogBinding;
import com.example.zyyschedule.databinding.CalendarFragmentBinding;
import com.example.zyyschedule.databinding.PriorityDialogBinding;
import com.example.zyyschedule.databinding.RemindDialogBinding;
import com.example.zyyschedule.databinding.RemindListHeadBinding;
import com.example.zyyschedule.databinding.TimepickerDialogBinding;
import com.example.zyyschedule.viewmodel.CalendarViewModel;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private PriorityListAdapter priorityListAdapter;
    private AlertDialog prioritydialog;
    private AllLabelDialogBinding labelBinding;
    private RemindDialogBinding remindDialogBinding;
    private final LabelAdapter labelAdapter = new LabelAdapter(R.layout.label_item);
    private final RemindAdapter remindAdapter = new RemindAdapter(R.layout.remind_item);
    private AlertDialog labelchoose;
    private View labeldialoghead;
    private AlertDialog addscheule;
    private AlertDialog remindDialog;
    private RemindListHeadBinding remindListHeadBinding;


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
        labeldialoghead = inflater.inflate(R.layout.label_dialog_head, null);
        addScheduleBinding = DataBindingUtil.inflate(inflater, R.layout.add_schedule, container, false);
        timepickerbinding = DataBindingUtil.inflate(inflater, R.layout.timepicker_dialog, container, false);
        priorityDialogBinding = DataBindingUtil.inflate(inflater, R.layout.priority_dialog, container, false);
        labelBinding = DataBindingUtil.inflate(inflater, R.layout.all_label_dialog, container, false);
        remindDialogBinding = DataBindingUtil.inflate(inflater, R.layout.remind_dialog, container, false);
        remindListHeadBinding = DataBindingUtil.inflate(inflater, R.layout.remind_list_head, container, false);
        datePicker = dialog.findViewById(R.id.date_picker);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        timepickerbinding.hourPicker.setMaxValue(23);
        timepickerbinding.hourPicker.setMinValue(00);
        timepickerbinding.hourPicker.setValue(00);
        timepickerbinding.minePicker.setMinValue(00);
        timepickerbinding.minePicker.setMaxValue(59);
        timepickerbinding.minePicker.setValue(00);
        labelBinding.labelList.setLayoutManager(layoutManager);
        labelBinding.labelList.setAdapter(labelAdapter);
        LinearLayoutManager remindLayoutManager = new LinearLayoutManager(getContext());
        remindLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        remindDialogBinding.remindChooseList.setLayoutManager(remindLayoutManager);
        remindDialogBinding.remindChooseList.setAdapter(remindAdapter);
        ArrayList remindlist = vm.RemindListData();
        remindAdapter.setNewData(remindlist);
        remindAdapter.setHeader(remindListHeadBinding);
        remindAdapter.setHeaderView(remindListHeadBinding.getRoot());
        labeldialoghead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddLabelActivity.class);
                startActivity(intent);
            }
        });

        //对标签数据进行监听，刷新标签选择对话框，对话框点击事件
        vm.getAllLabel().observe(getViewLifecycleOwner(), new Observer<List<Label>>() {
            @Override
            public void onChanged(List<Label> labels) {
                labelAdapter.setNewData(labels);
                labelAdapter.notifyDataSetChanged();
                labelAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        TextView labelname = view.findViewById(R.id.label_name);
                        TextView labelid = view.findViewById(R.id.label_id);
                        addScheduleBinding.scheduleLabelId.setText(labelid.getText());
                        vm.label.setValue(labelname.getText().toString());
                        labelchoose.dismiss();
                    }
                });
                if (labeldialoghead.getParent() != null) {
                    ViewGroup vg = (ViewGroup) labeldialoghead.getParent();
                    vg.removeView(labeldialoghead);
                }
                labelAdapter.addHeaderView(labeldialoghead);
            }
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
        remindListHeadBinding.remindHeadBox.setChecked(true);
        remindListHeadBinding.remindHeadBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    remindListHeadBinding.remindHeadBox.setClickable(false);
                    remindAdapter.addRemind = new StringBuffer("无提醒");
                    for (int i = 0; i < remindAdapter.getData().size(); i++) {
                        remindAdapter.getData().get(i).setRemindisChecked(false);
                        remindAdapter.notifyDataSetChanged();
                    }
                } else {
                    remindListHeadBinding.remindHeadBox.setClickable(true);
                }
            }
        });
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
            case R.id.label_button:
                gotoAllLabel();
                break;
            case R.id.schedule_label:
                gotoAllLabel();
                break;
            case R.id.remind_button:
                gotoAddRemind();
                break;
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
        binding.fabBtn.setVisibility(View.GONE);
        if (addScheduleBinding.editText.getText().toString().trim().isEmpty()) {
            addScheduleBinding.sendSchedule.setClickable(false);
        } else {
            addScheduleBinding.sendSchedule.setClickable(true);
        }
        if (addScheduleBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) addScheduleBinding.getRoot().getParent();
            vg.removeView(addScheduleBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setView(addScheduleBinding.getRoot());
        addscheule = builder.create();
        addscheule.show();
        Window window = addscheule.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = addscheule.getWindow().getAttributes();
        p.width = d.getWidth();
        addscheule.getWindow().setAttributes(p);
        addscheule.getWindow().setBackgroundDrawableResource(R.drawable.add_schedule);
        addscheule.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                binding.fabBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void gotoGetTime() {
        timepickerbinding.hourPicker.setValue(time.get(java.util.Calendar.HOUR_OF_DAY));
        timepickerbinding.minePicker.setValue(time.get(java.util.Calendar.MINUTE));
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
                        vm.AddScheduleTime.setValue(ProcessingTime(timepickerbinding.hourPicker.getValue()) + ":" + ProcessingTime(timepickerbinding.minePicker.getValue()));
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(addScheduleBinding.textTime.getText().toString().equals("00:00")){
                            timepickerbinding.hourPicker.setValue(00);
                            timepickerbinding.minePicker.setValue(00);
                            vm.AddScheduleTime.setValue(ProcessingTime(timepickerbinding.hourPicker.getValue()) + ":" + ProcessingTime(timepickerbinding.minePicker.getValue()));
                        }else{
                            timepickerbinding.hourPicker.setValue(Integer.parseInt(vm.AddScheduleTime.getValue().substring(0,2)));
                            timepickerbinding.minePicker.setValue(Integer.parseInt(vm.AddScheduleTime.getValue().substring(3)));
                            vm.AddScheduleTime.setValue(ProcessingTime(timepickerbinding.hourPicker.getValue()) + ":" + ProcessingTime(timepickerbinding.minePicker.getValue()));
                        }
                    }
                });
        builder.create().show();
    }

    private void gotoPriority() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        priorityDialogBinding.priorityList.setLayoutManager(layoutManager);
        ArrayList prioritydata = vm.PriorityListData();
        priorityListAdapter = new PriorityListAdapter(R.layout.priority_item, prioritydata);
        priorityListAdapter.setContext(getContext());
        priorityListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TextView text = view.findViewById(R.id.priority_title);
                ImageView flag = view.findViewById(R.id.priority_flag);
                addScheduleBinding.textPriority.setTextColor(text.getTextColors());
                vm.priority.setValue(text.getText().toString());
                vm.priorityid.setValue(position);
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
        builder.setTitle(R.string.priority_dialog_title)
                .setView(priorityDialogBinding.getRoot());
        prioritydialog = builder.create();
        prioritydialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        prioritydialog.show();
    }

    private void gotoAllLabel() {
        if (labelBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) labelBinding.getRoot().getParent();
            vg.removeView(labelBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.label_dialog_title)
                .setView(labelBinding.getRoot());
        labelchoose = builder.create();
        labelchoose.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        labelchoose.show();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = labelchoose.getWindow().getAttributes();
        p.width = d.getWidth() / 3;
        p.height = d.getHeight() / 2;
        labelchoose.getWindow().setAttributes(p);
    }

    private void gotoAddRemind() {
        remindListHeadBinding.remindHeadBox.setClickable(false);
        if (remindDialogBinding.getRoot().getParent() != null) {
            ViewGroup vg = (ViewGroup) remindDialogBinding.getRoot().getParent();
            vg.removeView(remindDialogBinding.getRoot());
        }
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.remind_dialog_title)
                .setView(remindDialogBinding.getRoot())
                .setPositiveButton(R.string.dialog_button_finish, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (remindAdapter.addRemind.toString().equals("无提醒")) {
                            addScheduleBinding.remindText.setText(remindAdapter.addRemind.toString());
                        } else {
                            addScheduleBinding.remindText.setText(remindAdapter.addRemind.substring(4, remindAdapter.addRemind.length()));
                        }
                    }
                })
                .setNeutralButton(R.string.dialog_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        remindDialog = builder.create();
        remindDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_background);
        remindDialog.show();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = remindDialog.getWindow().getAttributes();
        p.width = d.getWidth() / 3;
        p.height = d.getHeight() / 2;
        remindDialog.getWindow().setAttributes(p);
    }

    private void AddSchedule() {
        Schedule schedule = new Schedule();
        String starttime = selectYear + "-" + selectMonth + "-" + selectDay + " " + vm.AddScheduleTime.getValue();
        schedule.setStarttime(starttime);
        schedule.setEndtime(null);
        schedule.setRemind(RemindChangeTime());


        schedule.setTitle(addScheduleBinding.editText.getText().toString());
        Log.i("label", starttime + " " + addScheduleBinding.editText.getText().toString() + " " + RemindChangeTime());

    }

    //将提醒字符转化为时间字符
    private String RemindChangeTime() {
        String remindtime = new String();
        remindtime = remindAdapter.addRemind.toString();
        if (remindtime.equals("无提醒")) {
            remindtime = new String();
        } else {
            remindtime = remindtime.replace("无提醒", "");
            remindtime = remindtime.replace(",准时", RemindToTime(1) + ",");
            remindtime = remindtime.replace(",提前1分钟", RemindToTime(2) + ",");
            remindtime = remindtime.replace(",提前5分钟", RemindToTime(3) + ",");
            remindtime = remindtime.replace(",提前10分钟", RemindToTime(4) + ",");
            remindtime = remindtime.replace(",提前15分钟", RemindToTime(5) + ",");
            remindtime = remindtime.replace(",提前20分钟", RemindToTime(6) + ",");
        }

        return remindtime;
    }

    //处理时分的方法
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
    private String RemindToTime(int i) {
        String remindtime;
        Date date = new Date();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        remindtime = selectYear + "-" + selectMonth + "-" + selectDay + " " +addScheduleBinding.textTime.getText() + ":" + "00";
        SimpleDateFormat std = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.setTime(date);
        try {
            date = std.parse(remindtime);
        } catch (Exception e) {
        }
        if (i == 1) {
            remindtime = std.format(date);
        } else if (i == 2) {
            date.setTime( date.getTime()-1000*60);
            remindtime = std.format(date);
        }else if(i==3){
            date.setTime( date.getTime()-5*1000*60);
            remindtime = std.format(date);
        }else if(i==4){
            date.setTime( date.getTime()-10*1000*60);
            remindtime = std.format(date);
        }else if(i==5){
            date.setTime( date.getTime()-15*1000*60);
            remindtime = std.format(date);
        }else if(i==6){
            date.setTime( date.getTime()-20*1000*60);
            remindtime = std.format(date);
        }


        return remindtime;
    }


}