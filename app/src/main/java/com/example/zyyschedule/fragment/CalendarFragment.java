package com.example.zyyschedule.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.CheckBox;
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

import java.util.ArrayList;
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
    private StringBuffer addRemind = new StringBuffer("无提醒");


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
        remindListHeadBinding = DataBindingUtil.inflate(inflater,R.layout.remind_list_head,container,false);
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
        addScheduleBinding.setVm(vm);
        addScheduleBinding.setLifecycleOwner(this);
        timepickerbinding.hourPicker.setMaxValue(23);
        timepickerbinding.hourPicker.setMinValue(00);
        timepickerbinding.hourPicker.setValue(time.get(java.util.Calendar.HOUR_OF_DAY));
        timepickerbinding.minePicker.setMinValue(00);
        timepickerbinding.minePicker.setMaxValue(59);
        timepickerbinding.minePicker.setValue(time.get(java.util.Calendar.MINUTE));
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
                if(isChecked){
                    remindListHeadBinding.remindHeadBox.setClickable(false);
                    addRemind = new StringBuffer("无提醒");
                    for(int i = 0;i<remindAdapter.getData().size();i++){
                        remindAdapter.getData().get(i).setRemindisChecked(false);
                        remindAdapter.notifyDataSetChanged();
                    }
                }else{
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


}