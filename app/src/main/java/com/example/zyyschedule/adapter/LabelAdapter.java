package com.example.zyyschedule.adapter;

import android.content.Context;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Label;

import java.util.List;

public class LabelAdapter extends BaseQuickAdapter<Label, BaseViewHolder> {
    public LabelAdapter(int layoutResId, @Nullable List<Label> data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, Label item) {
        helper.setText(R.id.label_name,item.getTitle());
        helper.setBackgroundColor(R.id.label_color,item.getColor());
    }
}
