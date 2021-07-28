package com.example.zyyschedule.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zyyschedule.PriorityBean;
import com.example.zyyschedule.R;

import java.util.List;

public class PriorityListAdapter extends BaseQuickAdapter<PriorityBean, BaseViewHolder> {
    private Context context;

    public PriorityListAdapter(int layoutResId, @Nullable List<PriorityBean> data) {
        super(layoutResId, data);
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, PriorityBean item) {
        helper.setText(R.id.priority_title, item.getPrioritytitle());
        if (item.getPrioritytype() == 0) {
            helper.setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_null));
            helper.setImageResource(R.id.priority_flag, R.drawable.priority_flag_null);
        } else if (item.getPrioritytype() == 1) {
            helper.setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_low));
            helper.setImageResource(R.id.priority_flag, R.drawable.priority_flag_low);
        } else if (item.getPrioritytype() == 2) {
            helper.setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_middle));
            helper.setImageResource(R.id.priority_flag, R.drawable.priority_flag_middle);
        } else if (item.getPrioritytype() == 3) {
            helper.setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_high));
            helper.setImageResource(R.id.priority_flag, R.drawable.priority_flag_high);
        }

    }
}
