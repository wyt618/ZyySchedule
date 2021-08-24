package com.example.zyyschedule.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zyyschedule.R;
import com.example.zyyschedule.adapter.ScheduleAdapter;
import com.example.zyyschedule.database.Schedule;
import com.example.zyyschedule.databinding.FinishScheduleFootBinding;
import com.example.zyyschedule.databinding.FragmentLabelBinding;
import com.example.zyyschedule.databinding.ScheduleFootBinding;
import com.example.zyyschedule.databinding.ScheduleListFinishHeadBinding;
import com.example.zyyschedule.databinding.ScheduleListHeadBinding;
import com.example.zyyschedule.viewmodel.ScheduleViewModel;

import java.util.List;

public class LabelFragment extends Fragment {
    private  int labelId;
    private FragmentLabelBinding binding;
    private ScheduleViewModel vm;
    private ScheduleAdapter scheduleAdapter;
    private ScheduleAdapter finishScheduleAdapter;
    private List<Schedule> Schedules;
    private List<Schedule> finishSchedules;
    private ScheduleListHeadBinding scheduleHeadBinding;
    private ScheduleFootBinding scheduleFootBinding;
    private ScheduleListFinishHeadBinding scheduleListFinishHeadBinding;
    private FinishScheduleFootBinding finishScheduleFootBinding;
    private AlertDialog.Builder builder;

    public LabelFragment(int labelId) {
        this.labelId = labelId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding  = DataBindingUtil.inflate(inflater,R.layout.fragment_label,container,false);
        scheduleHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_head, container, false);
        scheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_foot, container, false);
        scheduleListFinishHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_finish_head, container, false);
        finishScheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.finish_schedule_foot, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(ScheduleViewModel.class);
        builder = new AlertDialog.Builder(getContext());
        LinearLayoutManager labelSchedule =  new LinearLayoutManager(getContext());
        labelSchedule.setOrientation(LinearLayoutManager.VERTICAL);
        LinearLayoutManager finishSchedule =  new LinearLayoutManager(getContext());
        finishSchedule.setOrientation(LinearLayoutManager.VERTICAL);
        binding.labelScheduleList.setLayoutManager(labelSchedule);
        binding.finishScheduleList.setLayoutManager(finishSchedule);
        scheduleAdapter = new ScheduleAdapter(R.layout.schedule_item_local);
        scheduleAdapter.setOwner(this);
        scheduleAdapter.setHeaderView(scheduleHeadBinding.getRoot());
        scheduleAdapter.addFooterView(scheduleFootBinding.getRoot());
        finishScheduleAdapter = new ScheduleAdapter(R.layout.schedule_item_local);
        finishScheduleAdapter.setOwner(this);
        finishScheduleAdapter.setHeaderView(scheduleListFinishHeadBinding.getRoot());
        finishScheduleAdapter.setFooterView(finishScheduleFootBinding.getRoot());
        binding.labelScheduleList.setAdapter(scheduleAdapter);
        binding.finishScheduleList.setAdapter(finishScheduleAdapter);
        scheduleHeadBinding.scheduleListHead.setText(R.string.local_schedule_head);
        scheduleHeadBinding.scheduleDeleteBack.setOnClickListener(v -> {
            scheduleHeadBinding.deleteSchedule.setVisibility(View.GONE);
            scheduleHeadBinding.scheduleListHead.setVisibility(View.VISIBLE);
            scheduleHeadBinding.scheduleDeleteBack.setVisibility(View.GONE);
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
            for (int i = 0; i < Schedules.size(); i++) {
                Schedules.get(i).setEditor(false);
            }
            for (int i = 0; i < finishSchedules.size(); i++) {
                finishSchedules.get(i).setEditor(false);
            }
            scheduleAdapter.notifyDataSetChanged();
            finishScheduleAdapter.notifyDataSetChanged();
        });
        scheduleHeadBinding.deleteSchedule.setOnClickListener(v -> gotoDeleteDialog());
        scheduleListFinishHeadBinding.deleteSchedule.setOnClickListener(v -> gotoDeleteDialog());
        //完成和未完成日程item长按事件
        scheduleAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            scheduleHeadBinding.deleteSchedule.setVisibility(View.VISIBLE);
            scheduleHeadBinding.scheduleListHead.setVisibility(View.GONE);
            scheduleHeadBinding.scheduleDeleteBack.setVisibility(View.VISIBLE);
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
            scheduleHeadBinding.deleteSchedule.setVisibility(View.VISIBLE);
            scheduleHeadBinding.scheduleListHead.setVisibility(View.GONE);
            scheduleHeadBinding.scheduleDeleteBack.setVisibility(View.VISIBLE);
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
    }

    public void gotoDeleteDialog() {
        builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.delete_schedule_message)
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
                    scheduleListFinishHeadBinding.deleteSchedule.setVisibility(View.GONE);
                    scheduleListFinishHeadBinding.scheduleListFinish.setVisibility(View.VISIBLE);
                    scheduleListFinishHeadBinding.scheduleDeleteBack.setVisibility(View.GONE);
                    scheduleHeadBinding.deleteSchedule.setVisibility(View.GONE);
                    scheduleHeadBinding.scheduleListHead.setVisibility(View.VISIBLE);
                    scheduleHeadBinding.scheduleDeleteBack.setVisibility(View.GONE);
                })
                .setNeutralButton(R.string.dialog_button_cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void UpdateScheduleList() {
        vm.getUFScheduleOfLabel(labelId).observe(getViewLifecycleOwner(), schedules -> {
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
        vm.getFScheduleOfLabel(labelId).observe(getViewLifecycleOwner(), schedules -> {
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


}