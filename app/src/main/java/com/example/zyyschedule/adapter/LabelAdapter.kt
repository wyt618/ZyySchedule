package com.example.zyyschedule.adapter

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Label

class LabelAdapter(layoutResId: Int) : BaseQuickAdapter<Label, BaseViewHolder>(layoutResId) {
    override fun convert(holder: BaseViewHolder, item: Label) {
        holder.setText(R.id.label_name,item.title)
        holder.setText(R.id.label_id,item.id.toString())
        val imageView:ImageView =  holder.itemView.findViewById(R.id.imageView)
        ContextCompat.getDrawable(context,R.drawable.ic_schedule_24)?.let {
            DrawableCompat.setTint(it,item.color as Int)
            imageView.setImageDrawable(it)
        }
    }
}