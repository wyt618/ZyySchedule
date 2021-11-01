package com.example.zyyschedule.adapter


import android.content.res.ColorStateList
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.bean.PriorityBean
import com.example.zyyschedule.R

class PriorityListAdapter :
    BaseQuickAdapter<PriorityBean, BaseViewHolder>(R.layout.priority_item) {
    override fun convert(holder: BaseViewHolder, item: PriorityBean) {
        holder.setText(R.id.priority_title, item.priorityTitle)
        val priorityFlag = holder.getView<AppCompatImageView>(R.id.priority_flag)
        holder.setTextColor(R.id.priority_title, item.priorityColor)
        priorityFlag.setImageResource(R.drawable.priority_flag)
        priorityFlag.imageTintList = ColorStateList.valueOf(item.priorityColor)
    }
}