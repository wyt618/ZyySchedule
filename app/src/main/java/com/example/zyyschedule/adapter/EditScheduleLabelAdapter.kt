package com.example.zyyschedule.adapter

import android.content.res.ColorStateList
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Label

class EditScheduleLabelAdapter(layoutResId: Int) :
    BaseQuickAdapter<Label, BaseViewHolder>(layoutResId) {
    override fun convert(holder: BaseViewHolder, item: Label) {
        holder.setText(R.id.label_title, item.title)
        holder.getView<ConstraintLayout>(R.id.label_background).backgroundTintList =
            item.color?.let {
                ColorStateList.valueOf(it)
            }
    }
}