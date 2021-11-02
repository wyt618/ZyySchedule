package com.example.zyyschedule.adapter

import android.content.res.ColorStateList
import android.os.Build
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Label

class LabelAdapter : BaseQuickAdapter<Label, BaseViewHolder>(R.layout.label_item) {
    var labelTitles: String = "无标签"
    var labelIds: String = "~0~"
    private lateinit var loadFragment: String

    fun setLoadFragment(loadFragment: String) {
        this.loadFragment = loadFragment
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun convert(holder: BaseViewHolder, item: Label) {
        holder.setText(R.id.label_click, item.title)
        holder.setText(R.id.label_id, item.id.toString())
        holder.setText(R.id.label_name, item.title)
        val imageView: ImageView = holder.itemView.findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.button_bg_press)
        imageView.imageTintList = ColorStateList.valueOf(item.color as Int)
        val labelChoose: AppCompatCheckBox = holder.itemView.findViewById(R.id.label_click)
        labelChoose.setOnCheckedChangeListener(null)
        labelChoose.setOnCheckedChangeListener { _: CompoundButton, isCheck: Boolean ->
            if (isCheck) {
                labelTitles += ",${item.title}"
                labelIds += "${item.id}~"
            } else {
                labelTitles = labelTitles.replace(",${item.title}", "")
                labelIds = labelIds.replace("~${item.id}~", "~")
            }
        }
        if (loadFragment == "CalendarFragment") {
            holder.setVisible(R.id.label_click, true)
            holder.setVisible(R.id.label_name, false)
        }

        if (loadFragment == "ScheduleFragment") {
            holder.setVisible(R.id.label_click, false)
            holder.setVisible(R.id.label_name, true)
        }
    }

}