package com.example.zyyschedule.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.zyyschedule.PriorityBean;
import com.example.zyyschedule.R;

import java.util.List;

public class PriorityListAdapter extends BaseQuickAdapter<PriorityBean, BaseViewHolder> {
    private Context context;

    public PriorityListAdapter(int layoutResId, @Nullable List<PriorityBean> data) {
        super(layoutResId, data);
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, PriorityBean item) {
        helper.setText(R.id.priority_title, item.getPriorityTitle());
        if (item.getPriorityType() == 0) {
            helper.setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_null));
            helper.setImageResource(R.id.priority_flag, R.drawable.priority_flag_null);
        } else if (item.getPriorityType() == 1) {
            helper.setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_low));
            helper.setImageResource(R.id.priority_flag, R.drawable.priority_flag_low);
        } else if (item.getPriorityType() == 2) {
            helper.setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_middle));
            helper.setImageResource(R.id.priority_flag, R.drawable.priority_flag_middle);
        } else if (item.getPriorityType() == 3) {
            helper.setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_high));
            helper.setImageResource(R.id.priority_flag, R.drawable.priority_flag_high);
        }

    }
}
