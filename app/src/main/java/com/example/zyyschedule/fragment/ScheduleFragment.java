package com.example.zyyschedule.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.zyyschedule.R;
import com.example.zyyschedule.databinding.ScheduleFragmentBinding;
import com.example.zyyschedule.viewmodel.ScheduleViewModel;

public class ScheduleFragment extends Fragment {
    private ScheduleFragmentBinding binding;
    private ScheduleViewModel mViewModel;

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.schedule_fragment,container,false);

        binding.ivMainMenu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ivMainMenu:
                    binding.drawerLayout.openDrawer(Gravity.START);
                    break;
                }
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        // TODO: Use the ViewModel
    }

}