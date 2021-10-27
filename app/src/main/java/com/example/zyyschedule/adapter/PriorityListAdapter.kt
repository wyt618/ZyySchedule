package com.example.zyyschedule.adapter


import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.PriorityBean
import com.example.zyyschedule.R

class PriorityListAdapter(layoutResId: Int) :
    BaseQuickAdapter<PriorityBean, BaseViewHolder>(layoutResId) {
    private lateinit var mContext: Context
    private var textColor: Int = 0
    fun getMContext(context: Context) {
        mContext = context
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun convert(holder: BaseViewHolder, item: PriorityBean) {
        holder.setText(R.id.priority_title, item.priorityTitle)
        val priorityFlag = holder.getView<AppCompatImageView>(R.id.priority_flag)
        when (item.priorityType) {
            0 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_null)
            }
            1 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_low)
            }
            2 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_middle)
            }
            3 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_high)
            }
        }
        holder.setTextColor(R.id.priority_title, textColor)
        priorityFlag.setImageResource(R.drawable.priority_flag)
        priorityFlag.imageTintList = ColorStateList.valueOf(textColor)
    }
}