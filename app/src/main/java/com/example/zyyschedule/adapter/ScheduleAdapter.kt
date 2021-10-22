package com.example.zyyschedule.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.viewmodel.CalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "CAST_NEVER_SUCCEEDS")
class ScheduleAdapter(layoutResId: Int) :BaseQuickAdapter<Schedule, BaseViewHolder>(layoutResId),DraggableModule {
    private lateinit var owner: ViewModelStoreOwner
    private lateinit var vm: CalendarViewModel
    private lateinit var date: Date
    var otherDate: List<Schedule>? = null
    var pitchOnNumber: MutableLiveData<Int> = MutableLiveData(0)

    fun setOwner(owner: ViewModelStoreOwner) {
        this.owner = owner
    }

    @SuppressLint("SimpleDateFormat", "RestrictedApi")
    override fun convert(holder: BaseViewHolder, item: Schedule) {
        val now = Date()
        val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            date = std.parse(item.startTime)
        } catch (e: Exception) {
            Log.i("ScheduleAdapter", "日程开始时间转化失败:$e")
        }

        vm = ViewModelProvider(owner).get(CalendarViewModel::class.java)
        holder.setText(R.id.schedule_title, item.title)
        holder.setText(R.id.delete_radio_button, item.title)
        holder.setText(
            R.id.schedule_date, item.startTime?.substring(0, 4) + "年"
                    + item.startTime?.substring(5, 7)?.toInt() + "月"
                    + item.startTime?.substring(8, 10)?.toInt() + "日"
        )
        holder.setText(
            R.id.schedule_time,
            item.startTime?.substring(item.startTime!!.length - 8, item.startTime!!.length - 3)
        )
        val radioButton: AppCompatRadioButton = holder.getView(R.id.delete_radio_button)
        radioButton.setOnCheckedChangeListener(null)
        radioButton.isChecked = item.isEditorChecked
        radioButton.setOnClickListener {
            var number = 0
            item.isEditorChecked = !item.isEditorChecked
            radioButton.isChecked = item.isEditorChecked
            for (i in data) {
                if (i.isEditorChecked) {
                    number += 1
                }
            }
            otherDate?.let {
                for (i in it) {
                    if (i.isEditorChecked) {
                        number += 1
                    }
                }
            }
            pitchOnNumber.value = number
        }

        val checkBox: AppCompatCheckBox = holder.getView(R.id.schedule_state)
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = item.isChecked
        checkBox.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            item.isChecked = isChecked
            if (isChecked) {
                item.state = "1"
            } else {
                item.state = "0"
            }
            vm.changeStateSchedule(item)
            vm.getScheduleDayOfTag()
        }

        if (item.state.equals("1")) {
            holder.setTextColor(
                R.id.schedule_title,
                ContextCompat.getColor(context, R.color.color_schedule_grey)
            )
        } else {
            holder.setTextColor(R.id.schedule_title, Color.BLACK)
            if (date.time <= now.time) {
                holder.setTextColor(R.id.schedule_time, Color.RED)
                holder.setTextColor(R.id.schedule_date, Color.RED)
            } else {
                holder.setTextColor(R.id.schedule_time, Color.BLACK)
                holder.setTextColor(R.id.schedule_date, Color.BLACK)
            }
        }

        if (item.isEditor) {
            checkBox.visibility = View.GONE
            radioButton.visibility = View.VISIBLE
            holder.setVisible(R.id.schedule_title,false)
        } else {
            checkBox.visibility = View.VISIBLE
            radioButton.visibility = View.GONE
            holder.setVisible(R.id.schedule_title,true)
        }

        when (item.priority) {
            0 -> checkBox.supportButtonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.priority_null))
            1 -> checkBox.supportButtonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.priority_low))
            2 -> checkBox.supportButtonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.priority_middle))
            3 -> checkBox.supportButtonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.priority_high))
        }

    }

}