package com.example.zyyschedule.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.zyyschedule.R;
import com.example.zyyschedule.database.Label;




public class LabelAdapter extends BaseQuickAdapter<Label, BaseViewHolder> {
    public LabelAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void removeItem(int pos){
        this.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    protected void convert(BaseViewHolder helper, Label item) {
        helper.setText(R.id.label_name,item.getTitle());
        helper.setBackgroundColor(R.id.label_color_view,item.getColor());
        helper.setText(R.id.label_id,String.valueOf(item.getId()));
    }

}
