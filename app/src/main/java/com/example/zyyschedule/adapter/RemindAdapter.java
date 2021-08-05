package com.example.zyyschedule.adapter;


import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.zyyschedule.R;
import com.example.zyyschedule.RemindBean;
import com.example.zyyschedule.databinding.RemindListHeadBinding;


public class RemindAdapter extends BaseQuickAdapter<RemindBean, BaseViewHolder> {
    private RemindListHeadBinding remindListHeadBinding;
    public StringBuffer addRemind = new StringBuffer("无提醒");

    public RemindAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, RemindBean item) {
        helper.setText(R.id.remind_check_box, item.getRemindtitle());
        CheckBox checkBox = helper.getView(R.id.remind_check_box);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(item.isRemindisChecked());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setRemindisChecked(isChecked);
            if (isChecked) {
                remindListHeadBinding.remindHeadBox.setChecked(false);
                addRemind.append("," + item.getRemindtitle());
            } else {
                int flag = 0;
                for (int i = 0; i < getData().size(); i++) {
                    if (!getData().get(i).isRemindisChecked()) {
                        flag = flag + 1;
                    }
                }
                if (flag == getData().size()) {
                    remindListHeadBinding.remindHeadBox.setChecked(true);
                    addRemind = new StringBuffer("无提醒");
                }
                addRemind = new StringBuffer(addRemind.toString().replace("," + item.getRemindtitle(), ""));
            }
        });
    }


    public void setHeader(RemindListHeadBinding binding) {
        this.remindListHeadBinding = binding;
    }


}
