package com.example.zyyschedule.adapter;


import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.zyyschedule.R;
import com.example.zyyschedule.RemindBean;



public class RemindAdapter extends BaseQuickAdapter<RemindBean, BaseViewHolder> {
    public RemindAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, RemindBean item) {
        helper.setText(R.id.remind_check_box,item.getRemindtitle());
        CheckBox checkBox = helper.getView(R.id.remind_check_box);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(item.isRemindisChecked());
        helper.setOnCheckedChangeListener(R.id.remind_check_box, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setRemindisChecked(isChecked);
                if(isChecked){

                }else{

                }
            }
        });
    }


    private void clearRemind(){
        if(mData.get(0).isRemindisChecked()){
            for(int i = 1;i<mData.size();i++){
                mData.get(i).setRemindisChecked(false);
            }
        }


    }

}
