package com.example.zyyschedule.adapter


import android.content.Context
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.PriorityBean
import com.example.zyyschedule.R

class PriorityListAdapter(layoutResId: Int) : BaseQuickAdapter<PriorityBean, BaseViewHolder>(layoutResId) {
    private lateinit var mContext: Context

    public fun getMContext(context: Context) {
        mContext = context
    }

    override fun convert(holder: BaseViewHolder, item: PriorityBean) {
        holder.setText(R.id.priority_title, item.priorityTitle)
        when (item.priorityType) {
            0 -> holder.apply {
                    setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_null))
                    setImageResource(R.id.priority_flag,R.drawable.priority_flag_null)
                }
            1 -> holder.apply {
                setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_low))
                setImageResource(R.id.priority_flag,R.drawable.priority_flag_low)
            }
            2 -> holder.apply {
                setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_middle))
                setImageResource(R.id.priority_flag,R.drawable.priority_flag_middle)
            }
            3 -> holder.apply {
                setTextColor(R.id.priority_title, ContextCompat.getColor(context, R.color.priority_high))
                setImageResource(R.id.priority_flag,R.drawable.priority_flag_high)
            }
        }
    }


}