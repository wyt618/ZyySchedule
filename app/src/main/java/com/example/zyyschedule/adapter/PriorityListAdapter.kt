package com.example.zyyschedule.adapter


import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.PriorityBean
import com.example.zyyschedule.R

class PriorityListAdapter(layoutResId: Int) : BaseQuickAdapter<PriorityBean, BaseViewHolder>(layoutResId) {
    private lateinit var mContext: Context
    private  var textColor: Int =0
    fun getMContext(context: Context) {
        mContext = context
    }

    override fun convert(holder: BaseViewHolder, item: PriorityBean) {
        holder.setText(R.id.priority_title, item.priorityTitle)
        val flagDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.priority_flag)
        when (item.priorityType) {
            0 ->  {
                textColor =  ContextCompat.getColor(context, R.color.priority_null)
                flagDrawable?.let {
                    DrawableCompat.setTint(it,textColor)
                }
                }
            1 ->  {
                textColor = ContextCompat.getColor(context, R.color.priority_low)
                flagDrawable?.let {
                    DrawableCompat.setTint(it,textColor)
                }
            }
            2 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_middle)
                flagDrawable?.let {
                    DrawableCompat.setTint(it,textColor)
                }
            }
            3 -> {
                textColor =  ContextCompat.getColor(context, R.color.priority_high)
                flagDrawable?.let {
                    DrawableCompat.setTint(it,textColor)
                }
            }
        }
        holder.setTextColor(R.id.priority_title, textColor)
        holder.setImageDrawable(R.id.priority_flag,flagDrawable)
    }


}