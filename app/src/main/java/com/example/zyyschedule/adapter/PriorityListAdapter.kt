package com.example.zyyschedule.adapter


import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.PriorityBean
import com.example.zyyschedule.R

class PriorityListAdapter(layoutResId: Int) : BaseQuickAdapter<PriorityBean, BaseViewHolder>(layoutResId) {
    private lateinit var mContext: Context
    private var textColor: Int = 0
    fun getMContext(context: Context) {
        mContext = context
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun convert(holder: BaseViewHolder, item: PriorityBean) {
        holder.setText(R.id.priority_title, item.priorityTitle)
        val flagDrawable: Drawable? = AppCompatResources.getDrawable(context,R.drawable.priority_flag)
        when (item.priorityType) {
            0 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_null)
                flagDrawable?.setTint(textColor)
            }
            1 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_low)
                flagDrawable?.setTint(textColor)
            }
            2 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_middle)
                flagDrawable?.setTint(textColor)
            }
            3 -> {
                textColor = ContextCompat.getColor(context, R.color.priority_high)
                flagDrawable?.setTint(textColor)
            }
        }
        holder.setTextColor(R.id.priority_title, textColor)

        holder.setImageDrawable(R.id.priority_flag, flagDrawable)
    }


}