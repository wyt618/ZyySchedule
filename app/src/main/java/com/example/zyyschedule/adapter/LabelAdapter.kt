package com.example.zyyschedule.adapter

import android.content.res.ColorStateList
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Label

class LabelAdapter(layoutResId: Int) : BaseQuickAdapter<Label, BaseViewHolder>(layoutResId) {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun convert(holder: BaseViewHolder, item: Label) {
        holder.setText(R.id.label_name, item.title)
        holder.setText(R.id.label_id, item.id.toString())
        val imageView: ImageView = holder.itemView.findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.ic_schedule_24)
        imageView.imageTintList = ColorStateList.valueOf(item.color as Int)
    }
}