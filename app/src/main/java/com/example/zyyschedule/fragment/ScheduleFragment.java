package com.example.zyyschedule.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.zyyschedule.R;
import com.example.zyyschedule.databinding.ScheduleFragmentBinding;
import com.example.zyyschedule.viewmodel.ScheduleViewModel;
import com.google.android.material.navigation.NavigationView;

public class ScheduleFragment extends Fragment implements View.OnClickListener {
    private ScheduleFragmentBinding binding;
    private ScheduleViewModel mViewModel;

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.schedule_fragment, container, false);
        binding.ivMainMenu.setOnClickListener(this);
        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.today:
                        gotoTodayScheduleFragment();
                        break;

                }
                return true;
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMainMenu:
                binding.drawerLayout.openDrawer(Gravity.START);
                break;
        }
    }

    private void gotoTodayScheduleFragment() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.replace(R.id.scheduleFragment, new TodayScheduleFragment(), null)
                .commit();
    }


}