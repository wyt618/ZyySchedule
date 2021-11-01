package com.example.zyyschedule.adapter

import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.bean.RemindBean
import com.example.zyyschedule.databinding.RemindListHeadBinding

open class RemindAdapter :
    BaseQuickAdapter<RemindBean, BaseViewHolder>(R.layout.remind_item) {
    private lateinit var remindListHeadBinding: RemindListHeadBinding
    var addRemind: StringBuffer = StringBuffer("无提醒")
    override fun convert(holder: BaseViewHolder, item: RemindBean) {
        holder.setText(R.id.remind_check_box, item.remindTitle)
        val checkBox: AppCompatCheckBox = holder.getView(R.id.remind_check_box)
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = item.remindIsChecked
        checkBox.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            item.remindIsChecked = isChecked
            if (isChecked) {
                remindListHeadBinding.remindHeadBox.isChecked = false
                addRemind.append(",").append(item.remindTitle)
            } else {
                var flag = 0
                for (i in data.indices) {
                    if (!data[i].remindIsChecked) {
                        flag += 1
                    }
                }
                if (flag == data.size) {
                    remindListHeadBinding.remindHeadBox.isChecked = true
                    addRemind = StringBuffer("无提醒")
                }
                addRemind = StringBuffer(addRemind.toString().replace("," + item.remindTitle, ""))
            }
        }
    }


    fun setHeader(binding: RemindListHeadBinding) {
        remindListHeadBinding = binding
    }
}

