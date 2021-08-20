package com.example.zyyschedule.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Label

class LabelAdapter(layoutResId: Int) : BaseQuickAdapter<Label, BaseViewHolder>(layoutResId) {
    override fun convert(holder: BaseViewHolder, item: Label) {
        holder.setText(R.id.label_name,item.title)
        holder.setBackgroundColor(R.id.label_color_view,item.color)
        holder.setText(R.id.label_id,item.id.toString())
    }
}