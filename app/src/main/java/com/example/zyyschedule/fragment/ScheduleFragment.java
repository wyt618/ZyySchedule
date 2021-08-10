package com.example.zyyschedule.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
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

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.schedule_fragment, container, false);
        return binding.getRoot();
    }



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
           view.findViewById(R.id.trash).setVisibility(View.VISIBLE);


        });

        labelAdapter.addChildClickViewIds(R.id.trash);
        // 设置子控件点击监听
        labelAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                if (view.getId() == R.id.trash) {

                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("删除标签");
                    builder.setMessage("您标签内的所有日程将被删除。");
                    builder.setPositiveButton("删除",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    List<Label> labels = (List<Label>) adapter.getData();
                                    vm.deleteLabel(labels.get(position));
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog=builder.create();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.delete_dialog);
                    dialog.show();
                    WindowManager m = getActivity().getWindowManager();
                    DisplayMetrics d = new DisplayMetrics();
                    m.getDefaultDisplay().getMetrics(d);
                    WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
                    p.width = d.widthPixels / 3;
                    p.height = d.heightPixels / 5;
                    dialog.getWindow().setAttributes(p);
                }
            }

        });

        //item长按事件
        labelAdapter.setOnItemLongClickListener((adapter, view, pos) -> {
                    view.findViewById(R.id.trash).setVisibility(View.VISIBLE);
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
        ft.replace(R.id.scheduleFragment, new LabelFragment(), null)
                .commit();
        binding.scheduleTitleBarTitle.setText(R.string.title_not_classified);
        binding.drawerLayout.closeDrawer(Gravity.START);
    }

    private void gotoAddLabelActivity() {
        Intent intent = new Intent(getActivity(), AddLabelActivity.class);
        startActivity(intent);
    }

}