package com.example.zyyschedule.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.zyyschedule.R;
import com.example.zyyschedule.activity.AddLabelActivity;
import com.example.zyyschedule.adapter.LabelAdapter;
import com.example.zyyschedule.database.Label;
import com.example.zyyschedule.databinding.ScheduleFragmentBinding;
import com.example.zyyschedule.viewmodel.ScheduleViewModel;

import java.util.List;

public class ScheduleFragment extends Fragment implements View.OnClickListener {
    private ScheduleFragmentBinding binding;
    private ScheduleViewModel vm;
    private LabelAdapter labelAdapter = new LabelAdapter(R.layout.label_item);
    private View labelItemEditorButton ;

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.schedule_fragment, container, false);
        labelItemEditorButton = inflater.inflate(R.layout.label_item_editor_button,null);
        return binding.getRoot();
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gotoTodayScheduleFragment();
        vm = new ViewModelProvider(this).get(ScheduleViewModel.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.labelRecyclerview.setLayoutManager(layoutManager);
        binding.labelRecyclerview.setAdapter(labelAdapter);
        binding.ivMainMenu.setOnClickListener(this);
        binding.drawerLayout.setOnClickListener(this);
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.today:
                    gotoTodayScheduleFragment();
                    break;
                case R.id.inbox:
                    gotoLocalFragment();
                    break;
                case R.id.dates:
                    gotoLabelFragment();
                    break;
                case R.id.add_list:
                    gotoAddLabelActivity();
            }
            return true;
        });
        vm.getAllLabel().observe(getViewLifecycleOwner(), labels -> {
            labelAdapter.setList(labels);
            labelAdapter.notifyDataSetChanged();

        });

        //item点击事件
        labelAdapter.setOnItemClickListener((adapter, view, position) -> {
            TextView labelName = view.findViewById(R.id.label_name);
            TextView labelId = view.findViewById(R.id.label_id);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_NONE);
            ft.replace(R.id.scheduleFragment, new LabelFragment(Integer.parseInt(labelId.getText().toString())), null)
                    .commit();
            binding.scheduleTitleBarTitle.setText(labelName.getText());
            binding.drawerLayout.closeDrawer(Gravity.START);
        });



        //item长按事件
        labelAdapter.setOnItemLongClickListener((adapter, view, pos) -> {
            if (labelItemEditorButton.getParent() != null) {
                ViewGroup vg = (ViewGroup) labelItemEditorButton.getParent();
                vg.removeView(labelItemEditorButton);
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(labelItemEditorButton);
            AlertDialog deleteDialog = builder.create();
            deleteDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            deleteDialog.show();
            deleteDialog.getWindow().setLayout(66,66);
            WindowManager.LayoutParams params = deleteDialog.getWindow().getAttributes();
            WindowManager m = getActivity().getWindowManager();
            DisplayMetrics d = new DisplayMetrics();
            m.getDefaultDisplay().getMetrics(d);
            params.x = view.getWidth()-21;
            params.y = -d.heightPixels / 2+view.getTop()+427;
            deleteDialog.getWindow().setAttributes(params);
            deleteDialog.getWindow().setGravity(Gravity.START);
            labelItemEditorButton.findViewById(R.id.delete_button).setOnClickListener(v -> {
                deleteDialog.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setTitle("删除标签");
                builder1.setMessage("您标签内的所有日程将被删除。");
                builder1.setPositiveButton("删除",
                        (dialogInterface, i) -> {
                            List<Label> labels = (List<Label>) adapter.getData();
                            vm.deleteLabel(labels.get(pos));
                            adapter.notifyDataSetChanged();
                        })
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder1.create();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.delete_dialog);
                dialog.show();
                WindowManager m1 = getActivity().getWindowManager();
                DisplayMetrics d1 = new DisplayMetrics();
                m1.getDefaultDisplay().getMetrics(d1);
                WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
                p.width = d1.widthPixels / 3;
                p.height = d1.heightPixels / 5;
                dialog.getWindow().setAttributes(p);
            });
            deleteDialog.setOnDismissListener(dialog -> binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED));
            return false;
        });

    }



    @SuppressLint({"WrongConstant", "NonConstantResourceId"})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivMainMenu) {
            binding.drawerLayout.openDrawer(Gravity.START);
        }
    }

    @SuppressLint("WrongConstant")
    private void gotoTodayScheduleFragment() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.replace(R.id.scheduleFragment, new TodayScheduleFragment(), null)
                .commit();
        binding.scheduleTitleBarTitle.setText(R.string.title_today);
        binding.drawerLayout.closeDrawer(Gravity.START);
    }

    @SuppressLint("WrongConstant")
    private void gotoLocalFragment() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.replace(R.id.scheduleFragment, new LocalFragment(), null)
                .commit();
        binding.scheduleTitleBarTitle.setText(R.string.title_local_schedule);
        binding.drawerLayout.closeDrawer(Gravity.START);
    }

    @SuppressLint("WrongConstant")
    private void gotoLabelFragment() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.replace(R.id.scheduleFragment, new LabelFragment(0), null)
                .commit();
        binding.scheduleTitleBarTitle.setText(R.string.title_not_classified);
        binding.drawerLayout.closeDrawer(Gravity.START);
    }

    private void gotoAddLabelActivity() {
        Intent intent = new Intent(getActivity(), AddLabelActivity.class);
        startActivity(intent);
    }

}