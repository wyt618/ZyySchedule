package com.example.zyyschedule.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.example.zyyschedule.R;
import com.example.zyyschedule.activity.AddLabelActivity;
import com.example.zyyschedule.adapter.LabelAdapter;
import com.example.zyyschedule.database.Label;
import com.example.zyyschedule.databinding.AddScheduleBinding;
import com.example.zyyschedule.databinding.ScheduleFragmentBinding;
import com.example.zyyschedule.viewmodel.ScheduleViewModel;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScheduleFragment extends Fragment implements View.OnClickListener {
    private ScheduleFragmentBinding binding;
    private ScheduleViewModel mViewModel;
    private LabelAdapter labelAdapter = new LabelAdapter(R.layout.label_item);
    private AddScheduleBinding addScheduleBinding;
    private AlertDialog labelchoose;
    private View labeldialoghead;
    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.schedule_fragment, container, false);
        binding.ivMainMenu.setOnClickListener(this);
        binding.navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.today:
                    gotoTodayScheduleFragment();
                    break;
                case R.id.inbox:
                    gotoInboxFragment();
                    break;
                case R.id.dates:
                    gotoPersonFragment();
                    break;
                case R.id.add_list:
                    gotoAddLabelActivity();
            }
            return true;
        });
        gotoTodayScheduleFragment();
        return binding.getRoot();
    }

    public void showPopMenu(View view,final int pos){
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.getMenuInflater().inflate(R.menu.item_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                labelAdapter.removeItem(pos);
                return false;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });
        popupMenu.show();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.labelRecyclerview.setLayoutManager(layoutManager);
        binding.labelRecyclerview.setAdapter(labelAdapter);
        mViewModel.getAllLabel().observe(getViewLifecycleOwner(), labels -> {
            labelAdapter.setNewData(labels);
            labelAdapter.notifyDataSetChanged();

        });

        //item点击事件
        labelAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
                Log.i("tag", "onItemClick: ");
            }

        });

        //item长按事件
        labelAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull @NotNull BaseQuickAdapter adapter, @NonNull @NotNull View view, int pos) {
                Log.i("TAG", "onItemLongClick: ");

                showPopMenu(view,pos);
                return false;

            }
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
    private void gotoTodayScheduleFragment(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.replace(R.id.scheduleFragment, new TodayScheduleFragment(), null)
                .commit();
        binding.scheduleTitleBarTitle.setText(R.string.title_today);
        binding.drawerLayout.closeDrawer(Gravity.START);
    }
    @SuppressLint("WrongConstant")
    private void gotoInboxFragment(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.replace(R.id.scheduleFragment, new InboxFragment(), null)
                .commit();
        binding.scheduleTitleBarTitle.setText(R.string.title_local_schedule);
        binding.drawerLayout.closeDrawer(Gravity.START);
    }
    @SuppressLint("WrongConstant")
    private void gotoPersonFragment(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.replace(R.id.scheduleFragment, new PersonFragment(), null)
                .commit();
        binding.scheduleTitleBarTitle.setText(R.string.title_not_classified);
        binding.drawerLayout.closeDrawer(Gravity.START);
    }

    private void gotoAddLabelActivity(){
        Intent intent = new Intent(getActivity(), AddLabelActivity.class);
        startActivity(intent);
    }

}