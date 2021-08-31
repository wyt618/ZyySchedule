package com.example.zyyschedule.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.zyyschedule.R
import com.example.zyyschedule.activity.AddLabelActivity
import com.example.zyyschedule.adapter.LabelAdapter
import com.example.zyyschedule.adapter.PriorityListAdapter
import com.example.zyyschedule.adapter.RemindAdapter
import com.example.zyyschedule.adapter.ScheduleAdapter
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.*
import com.example.zyyschedule.viewmodel.CalendarViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.Calendar.Scheme
import com.haibin.calendarview.CalendarView
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CalendarFragment : Fragment(), View.OnClickListener, CalendarView.OnCalendarSelectListener {
    private val vm: CalendarViewModel by viewModels()
    private lateinit var binding: CalendarFragmentBinding
    private lateinit var dateJumpDialog: DialogDateBinding
    private lateinit var addScheduleBinding: AddScheduleBinding
    private lateinit var timePickerBinding: TimepickerDialogBinding
    private lateinit var priorityDialogBinding: PriorityDialogBinding
    private lateinit var remindListHeadBinding: RemindListHeadBinding
    private lateinit var scheduleListHeadBinding: ScheduleListHeadBinding
    private lateinit var scheduleFootBinding: ScheduleFootBinding
    private lateinit var scheduleListFinishHeadBinding: ScheduleListFinishHeadBinding
    private lateinit var finishScheduleFootBinding: FinishScheduleFootBinding
    private lateinit var labelBinding: AllLabelDialogBinding
    private lateinit var remindDialogBinding: RemindDialogBinding
    private lateinit var labelDialogHeadBinding: LabelDialogHeadBinding

    private lateinit var finishScheduleAdapter: ScheduleAdapter
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var priorityListAdapter: PriorityListAdapter
    private var labelAdapter = LabelAdapter(R.layout.label_item)
    private var remindAdapter = RemindAdapter(R.layout.remind_item)

    private lateinit var priorityDialog: AlertDialog
    private lateinit var labelChoose: AlertDialog
    private lateinit var addSchedule: AlertDialog
    private lateinit var remindDialog: AlertDialog
    private lateinit var builder: AlertDialog.Builder

    private lateinit var mSchedules: List<Schedule>
    private lateinit var mFinishSchedules: List<Schedule>
    private var selectYear: Int = 0
    private var selectMonth: Int = 0
    private var selectDay: Int = 0
    private lateinit var time: java.util.Calendar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        builder = AlertDialog.Builder(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.calendar_fragment, container, false)
        dateJumpDialog = DataBindingUtil.inflate(inflater, R.layout.dialog_date, container, false)
        labelDialogHeadBinding = DataBindingUtil.inflate(inflater, R.layout.label_dialog_head, container, false)
        addScheduleBinding = DataBindingUtil.inflate(inflater, R.layout.add_schedule, container, false)
        timePickerBinding = DataBindingUtil.inflate(inflater, R.layout.timepicker_dialog, container, false)
        priorityDialogBinding = DataBindingUtil.inflate(inflater, R.layout.priority_dialog, container, false)
        labelBinding = DataBindingUtil.inflate(inflater, R.layout.all_label_dialog, container, false)
        remindDialogBinding = DataBindingUtil.inflate(inflater, R.layout.remind_dialog, container, false)
        remindListHeadBinding = DataBindingUtil.inflate(inflater, R.layout.remind_list_head, container, false)
        scheduleListHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_head, container, false)
        scheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_foot, container, false)
        scheduleListFinishHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_finish_head, container, false)
        finishScheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.finish_schedule_foot, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        time = java.util.Calendar.getInstance()
        selectYear = binding.calendarView.curYear
        selectMonth = binding.calendarView.curMonth
        selectDay = binding.calendarView.curDay
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.flCurrent.setOnClickListener(this)
        binding.tvYear.text = binding.calendarView.curYear.toString()
        binding.tvMonthDay.text = binding.calendarView.curMonth.toString() + "月" + binding.calendarView.curDay + "日"
        binding.tvLunar.text = "今日"
        binding.calendarView.setOnCalendarSelectListener(this)
        binding.fabBtn.setOnClickListener(this)
        binding.vm = vm
        binding.lifecycleOwner = this
        addScheduleBinding.addScheduleSelectTime.setOnClickListener(this)
        addScheduleBinding.textTime.setOnClickListener(this)
        addScheduleBinding.priorityButton.setOnClickListener(this)
        addScheduleBinding.textPriority.setOnClickListener(this)
        addScheduleBinding.labelButton.setOnClickListener(this)
        addScheduleBinding.scheduleLabel.setOnClickListener(this)
        addScheduleBinding.remindButton.setOnClickListener(this)
        addScheduleBinding.remindText.setOnClickListener(this)
        addScheduleBinding.sendSchedule.setOnClickListener(this)
        addScheduleBinding.vm = vm
        addScheduleBinding.lifecycleOwner = this
        timePickerBinding.hourPicker.maxValue = 23
        timePickerBinding.hourPicker.minValue = 0
        timePickerBinding.hourPicker.value = 0
        timePickerBinding.minePicker.minValue = 0
        timePickerBinding.minePicker.maxValue = 59
        timePickerBinding.minePicker.value = 0
        labelBinding.labelList.layoutManager = layoutManager
        labelBinding.labelList.adapter = labelAdapter
        val remindLayoutManager = LinearLayoutManager(context)
        remindLayoutManager.orientation = LinearLayoutManager.VERTICAL
        remindDialogBinding.remindChooseList.layoutManager = remindLayoutManager
        remindDialogBinding.remindChooseList.adapter = remindAdapter
        val remindList = vm.remindListData()
        remindAdapter.setList(remindList)
        remindAdapter.setHeader(remindListHeadBinding)
        remindAdapter.setHeaderView(remindListHeadBinding.root)
        scheduleListHeadBinding.scheduleListHead.text = selectMonth.toString() + "月" + selectDay + "日"
        val scheduleLayoutManager = LinearLayoutManager(context)
        scheduleLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.scheduleList.layoutManager = scheduleLayoutManager
        val finishScheduleLayoutManager = LinearLayoutManager(context)
        finishScheduleLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.finishScheduleList.layoutManager = finishScheduleLayoutManager
        finishScheduleAdapter = ScheduleAdapter(R.layout.schedule_item)
        finishScheduleAdapter.setOwner(this)
        finishScheduleAdapter.addHeaderView(scheduleListFinishHeadBinding.root)
        finishScheduleAdapter.addFooterView(finishScheduleFootBinding.root)
        finishScheduleAdapter.setEmptyView(R.layout.schedule_empty)
        scheduleAdapter = ScheduleAdapter(R.layout.schedule_item)
        scheduleAdapter.addHeaderView(scheduleListHeadBinding.root)
        scheduleAdapter.addFooterView(scheduleFootBinding.root)
        scheduleAdapter.setEmptyView(R.layout.schedule_empty)
        scheduleAdapter.setOwner(this)
        binding.scheduleList.adapter = scheduleAdapter
        binding.finishScheduleList.adapter = finishScheduleAdapter
        scheduleListHeadBinding.scheduleListHead.text = selectMonth.toString() + "月" + selectDay + "日"
        labelDialogHeadBinding.root.setOnClickListener {
            val intent = Intent(activity, AddLabelActivity::class.java)
            startActivity(intent)
        }
        scheduleListHeadBinding.scheduleDeleteBack.setOnClickListener {
            scheduleListHeadBinding.deleteSchedule.visibility = View.GONE
            scheduleListHeadBinding.scheduleListHead.visibility = View.VISIBLE
            scheduleListHeadBinding.scheduleDeleteBack.visibility = View.GONE
            scheduleListFinishHeadBinding.deleteSchedule.visibility = View.GONE
            scheduleListFinishHeadBinding.scheduleListFinish.visibility = View.VISIBLE
            scheduleListFinishHeadBinding.scheduleDeleteBack.visibility = View.GONE
            binding.fabBtn.visibility = View.VISIBLE
            for (i in mSchedules.indices) {
                mSchedules[i].isEditor = false
            }
            for (i in mFinishSchedules.indices) {
                mFinishSchedules[i].isEditor = false
            }
            scheduleAdapter.notifyDataSetChanged()
            finishScheduleAdapter.notifyDataSetChanged()
        }
        scheduleListFinishHeadBinding.scheduleDeleteBack.setOnClickListener {
            scheduleListFinishHeadBinding.deleteSchedule.visibility = View.GONE
            scheduleListFinishHeadBinding.scheduleListFinish.visibility = View.VISIBLE
            scheduleListFinishHeadBinding.scheduleDeleteBack.visibility = View.GONE
            scheduleListHeadBinding.deleteSchedule.visibility = View.GONE
            scheduleListHeadBinding.scheduleListHead.visibility = View.VISIBLE
            scheduleListHeadBinding.scheduleDeleteBack.visibility = View.GONE
            binding.fabBtn.visibility = View.VISIBLE
            for (i in mSchedules.indices) {
                mSchedules[i].isEditor = false
            }
            for (i in mFinishSchedules.indices) {
                mFinishSchedules[i].isEditor = false
            }
            scheduleAdapter.notifyDataSetChanged()
            finishScheduleAdapter.notifyDataSetChanged()
        }
        scheduleListHeadBinding.deleteSchedule.setOnClickListener { gotoDeleteDialog() }
        scheduleListFinishHeadBinding.deleteSchedule.setOnClickListener { gotoDeleteDialog() }

        //对标签数据进行监听，刷新标签选择对话框，对话框点击事件
        vm.getAllLabel()!!.observe(viewLifecycleOwner, { labels: List<Label>? ->
            labelAdapter.setList(labels)
            labelAdapter.notifyDataSetChanged()
            labelAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, view: View, _: Int ->
                val labelName = view.findViewById<TextView>(R.id.label_name)
                val labelId = view.findViewById<TextView>(R.id.label_id)
                addScheduleBinding.scheduleLabelId.text = labelId.text
                vm.label.value = labelName.text.toString()
                labelChoose.dismiss()
            }
            if (labelDialogHeadBinding.root.parent != null) {
                val vg = labelDialogHeadBinding.root.parent as ViewGroup
                vg.removeView(labelDialogHeadBinding.root)
            }
            labelAdapter.addHeaderView(labelDialogHeadBinding.root)
        })


        //设置新增日程对话框有内容时唤醒按钮
        addScheduleBinding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    addScheduleBinding.sendSchedule.setImageResource(R.drawable.ic_baseline_send_click_24)
                    addScheduleBinding.sendSchedule.isClickable = true
                } else {
                    addScheduleBinding.sendSchedule.setImageResource(R.drawable.ic_baseline_send_24)
                    addScheduleBinding.sendSchedule.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        //实现长按日期跳转
        binding.tvMonthDay.setOnLongClickListener {
            if (dateJumpDialog.root.parent != null) {
                val vg = dateJumpDialog.root.parent as ViewGroup
                vg.removeView(dateJumpDialog.root)
            }
            builder.setView(dateJumpDialog.root)
                    .setTitle(R.string.clendar_dialog_title)
                    .setPositiveButton(R.string.dialog_button_ok) { _, _ ->
                        binding.calendarView.scrollToCalendar(dateJumpDialog.datePicker.year, dateJumpDialog.datePicker.month + 1, dateJumpDialog.datePicker.dayOfMonth)
                        Toast.makeText(context, dateJumpDialog.datePicker.year.toString() + "年" + (dateJumpDialog.datePicker.month + 1) + "月" + dateJumpDialog.datePicker.dayOfMonth + "日", Toast.LENGTH_SHORT).show()
                    }
                    .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
            builder.create().show()
            true
        }

        remindListHeadBinding.remindHeadBox.isChecked = true
        remindListHeadBinding.remindHeadBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                remindListHeadBinding.remindHeadBox.isClickable = false
                remindAdapter.addRemind = StringBuffer("无提醒")
                for (i in remindAdapter.data.indices) {
                    remindAdapter.data[i].remindIsChecked = false
                    remindAdapter.notifyDataSetChanged()
                }
            } else {
                remindListHeadBinding.remindHeadBox.isClickable = true
            }
        }
        //完成和未完成日程item长按事件
        scheduleAdapter.setOnItemLongClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, _: Int ->
//            LiveEventBus
//                    .get("some_key")
//                    .post("gone_navigation");
            binding.fabBtn.visibility = View.GONE
            scheduleListHeadBinding.deleteSchedule.visibility = View.VISIBLE
            scheduleListHeadBinding.scheduleListHead.visibility = View.GONE
            scheduleListHeadBinding.scheduleDeleteBack.visibility = View.VISIBLE
            for (i in mSchedules.indices) {
                mSchedules[i].isEditor = true
            }
            for (i in mFinishSchedules.indices) {
                mFinishSchedules[i].isEditor = true
            }
            adapter.notifyDataSetChanged()
            finishScheduleAdapter.notifyDataSetChanged()
            true
        }
        finishScheduleAdapter.setOnItemLongClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, _: Int ->
            binding.fabBtn.visibility = View.GONE
            scheduleListHeadBinding.deleteSchedule.visibility = View.VISIBLE
            scheduleListHeadBinding.scheduleListHead.visibility = View.GONE
            scheduleListHeadBinding.scheduleDeleteBack.visibility = View.VISIBLE
            if (scheduleAdapter.data.size == 0) {
                scheduleListFinishHeadBinding.deleteSchedule.visibility = View.VISIBLE
                scheduleListFinishHeadBinding.scheduleListFinish.visibility = View.GONE
                scheduleListFinishHeadBinding.scheduleDeleteBack.visibility = View.VISIBLE
            }
            for (i in mSchedules.indices) {
                mSchedules[i].isEditor = true
            }
            for (i in mFinishSchedules.indices) {
                mFinishSchedules[i].isEditor = true
            }
            adapter.notifyDataSetChanged()
            scheduleAdapter.notifyDataSetChanged()
            true
        }
        updateScheduleList()
        setCalendarTag()
    }


    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fl_current -> binding.calendarView.scrollToCurrent()
                R.id.fab_btn -> {
                    gotoAddSchedule()
                    vm.addScheduleDateAgo(selectYear, selectMonth, selectDay, binding.calendarView.curYear, binding.calendarView.curMonth, binding.calendarView.curDay)
                }
                R.id.add_schedule_selectTime, R.id.textTime -> gotoGetTime()
                R.id.priority_button, R.id.text_priority -> gotoPriority()
                R.id.label_button, R.id.schedule_label -> gotoChooseLabel()
                R.id.remind_button, R.id.remind_text -> gotoAddRemind()
                R.id.send_schedule -> addSchedule()
            }
        }
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {}

    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        selectYear = calendar!!.year
        selectMonth = calendar.month
        selectDay = calendar.day
        binding.tvLunar.visibility = View.VISIBLE
        binding.tvYear.visibility = View.VISIBLE
        binding.tvMonthDay.text = calendar.month.toString() + "月" + calendar.day + "日"
        binding.tvYear.text = calendar.year.toString()
        binding.tvLunar.text = calendar.lunar
        scheduleListHeadBinding.scheduleListHead.text = selectMonth.toString() + "月" + selectDay + "日"
        scheduleListHeadBinding.deleteSchedule.visibility = View.GONE
        scheduleListHeadBinding.scheduleListHead.visibility = View.VISIBLE
        scheduleListHeadBinding.scheduleDeleteBack.visibility = View.GONE
        updateScheduleList()
        setCalendarTag()
    }

    //添加日程悬浮窗显示
    private fun gotoAddSchedule() {
        binding.fabBtn.visibility = View.GONE
        addScheduleBinding.sendSchedule.isClickable = addScheduleBinding.editText.text.toString().trim().isNotEmpty()
        if (addScheduleBinding.root.parent != null) {
            val vg = addScheduleBinding.root.parent as ViewGroup
            vg.removeView(addScheduleBinding.root)
        }
        builder = AlertDialog.Builder(context)
        builder.setView(addScheduleBinding.root)
        addSchedule = builder.create()
        addSchedule.show()
        val window = addSchedule.window
        window!!.setGravity(Gravity.BOTTOM)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val d =  requireContext().resources!!.displayMetrics
        val p = addSchedule.window!!.attributes
        p.width = d.widthPixels
        addSchedule.window!!.attributes = p
        addSchedule.window!!.setBackgroundDrawableResource(R.drawable.add_schedule)
        addSchedule.setOnDismissListener { binding.fabBtn.visibility = View.VISIBLE }
    }

    //选择时间对话框
    private fun gotoGetTime() {
        time = java.util.Calendar.getInstance()
        timePickerBinding.hourPicker.value = time[java.util.Calendar.HOUR_OF_DAY]
        timePickerBinding.minePicker.value = time[java.util.Calendar.MINUTE]
        if (timePickerBinding.root.parent != null) {
            val vg = timePickerBinding.root.parent as ViewGroup
            vg.removeView(timePickerBinding.root)
        }
        builder = AlertDialog.Builder(context)
        builder.setView(timePickerBinding.root)
                .setTitle(R.string.add_schedule_timepicker)
                .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.dialog_button_ok
                ) { _, _ -> vm.addScheduleTime.setValue(processingTime(timePickerBinding.hourPicker.value) + ":" + processingTime(timePickerBinding.minePicker.value)) }
                .setOnDismissListener {
                    if (addScheduleBinding.textTime.text.toString() == "00:00") {
                        timePickerBinding.hourPicker.value = 0
                        timePickerBinding.minePicker.value = 0
                    } else {
                        timePickerBinding.hourPicker.value = Objects.requireNonNull(vm.addScheduleTime.value)!!.substring(0, 2).toInt()
                        timePickerBinding.minePicker.value = vm.addScheduleTime.value!!.substring(3).toInt()
                    }
                    vm.addScheduleTime.setValue(processingTime(timePickerBinding.hourPicker.value) + ":" + processingTime(timePickerBinding.minePicker.value))
                }
        builder.create().show()
    }

    //选择优先级
    private fun gotoPriority() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        priorityDialogBinding.priorityList.layoutManager = layoutManager
        val priorityData = vm.priorityListData()
        priorityListAdapter = PriorityListAdapter(R.layout.priority_item)
        priorityListAdapter.setList(priorityData)
        priorityListAdapter.getMContext(requireContext())
        priorityListAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, view: View, position: Int ->
            val text = view.findViewById<TextView>(R.id.priority_title)
            val flag = view.findViewById<ImageView>(R.id.priority_flag)
            addScheduleBinding.textPriority.setTextColor(text.textColors)
            vm.priority.value = text.text.toString()
            vm.priorityid.value = position
            addScheduleBinding.priorityButton.setImageDrawable(flag.drawable)
            priorityDialog.dismiss()
        }
        priorityDialogBinding.priorityList.adapter = priorityListAdapter
        if (priorityDialogBinding.root.parent != null) {
            val vg = priorityDialogBinding.root.parent as ViewGroup
            vg.removeView(priorityDialogBinding.root)
        }
        builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.priority_dialog_title)
                .setView(priorityDialogBinding.root)
        priorityDialog = builder.create()
        priorityDialog.window!!.setBackgroundDrawableResource(R.drawable.dialog_background)
        priorityDialog.show()
    }

    //选择标签对话框
    private fun gotoChooseLabel() {
        if (labelBinding.root.parent != null) {
            val vg = labelBinding.root.parent as ViewGroup
            vg.removeView(labelBinding.root)
        }
        builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.label_dialog_title)
                .setView(labelBinding.root)
        labelChoose = builder.create()
        labelChoose.window!!.setBackgroundDrawableResource(R.drawable.dialog_background)
        labelChoose.show()
        val d =  requireContext().resources!!.displayMetrics
        val p = labelChoose.window!!.attributes
        p.width = d.widthPixels / 3
        p.height = d.heightPixels / 2
        labelChoose.window!!.attributes = p
    }

    //选择提醒
    private fun gotoAddRemind() {
        var flag = 0
        for (i in remindAdapter.data.indices) {
            if (!remindAdapter.data[i].remindIsChecked) {
                flag += 1
            }
        }
        remindListHeadBinding.remindHeadBox.isClickable = flag != remindAdapter.data.size
        if (remindDialogBinding.root.parent != null) {
            val vg = remindDialogBinding.root.parent as ViewGroup
            vg.removeView(remindDialogBinding.root)
        }
        builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.remind_dialog_title)
                .setView(remindDialogBinding.root)
                .setPositiveButton(R.string.dialog_button_finish) { _, _ ->
                    if (remindAdapter.addRemind.toString() == "无提醒") {
                        addScheduleBinding.remindText.text = remindAdapter.addRemind.toString()
                    } else {
                        addScheduleBinding.remindText.text = remindAdapter.addRemind.substring(4, remindAdapter.addRemind.length)
                    }
                    getNotification()
                }
                .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
        remindDialog = builder.create()
        remindDialog.window!!.setBackgroundDrawableResource(R.drawable.dialog_background)
        remindDialog.show()
        val d =  requireContext().resources!!.displayMetrics
        val p = remindDialog.window!!.attributes
        p.width = d.widthPixels / 3
        p.height = d.heightPixels / 2
        remindDialog.window!!.attributes = p
    }

    //新增日程到数据库
    private fun addSchedule() {
        val schedule = Schedule()
        val startTime = selectYear.toString() + "-" + processingTime(selectMonth) + "-" + processingTime(selectDay) + " " + vm.addScheduleTime.value + ":00"
        schedule.startTime = startTime
        schedule.endTime = null
        schedule.remind = remindChangeTime()
        schedule.title = addScheduleBinding.editText.text.toString()
        schedule.detailed = null
        schedule.state = "0"
        schedule.priority = addScheduleBinding.priorityId.text.toString().toInt()
        if (addScheduleBinding.scheduleLabelId.text.toString().trim().isEmpty()) {
            schedule.labelId = 0
        } else {
            schedule.labelId = addScheduleBinding.scheduleLabelId.text.toString().trim().toInt()
        }
        vm.insertSchedule(schedule)
        updateScheduleList()
        addSchedule.dismiss()
        if (schedule.remind!!.isNotEmpty()) {
            val remindCheck: Int = checkRemindTime(schedule.remind!!)
            if (remindCheck > 0) {
                Toast.makeText(context, "抱歉，有" + remindCheck + "条提醒因为超出当前时间无效", Toast.LENGTH_LONG).show()
            }
        }
    }

    //将提醒字符转化为时间字符
    private fun remindChangeTime(): String {
        var remindtime: String
        remindtime = remindAdapter.addRemind.toString()
        if (remindtime == "无提醒") {
            remindtime = ""
        } else {
            remindtime = remindtime.replace("无提醒", "")
            remindtime = remindtime.replace(",准时", remindToTime(1) + ",")
            remindtime = remindtime.replace(",提前1分钟", remindToTime(2) + ",")
            remindtime = remindtime.replace(",提前5分钟", remindToTime(3) + ",")
            remindtime = remindtime.replace(",提前10分钟", remindToTime(4) + ",")
            remindtime = remindtime.replace(",提前15分钟", remindToTime(5) + ",")
            remindtime = remindtime.replace(",提前20分钟", remindToTime(6) + ",")
            remindtime = remindtime.replace(",提前25分钟", remindToTime(7) + ",")
            remindtime = remindtime.replace(",提前30分钟", remindToTime(8) + ",")
            remindtime = remindtime.replace(",提前45分钟", remindToTime(9) + ",")
            remindtime = remindtime.replace(",提前1个小时", remindToTime(10) + ",")
            remindtime = remindtime.replace(",提前2个小时", remindToTime(11) + ",")
            remindtime = remindtime.replace(",提前3个小时", remindToTime(12) + ",")
            remindtime = remindtime.replace(",提前12个小时", remindToTime(13) + ",")
            remindtime = remindtime.replace(",提前1天", remindToTime(14) + ",")
            remindtime = remindtime.replace(",提前2天", remindToTime(15) + ",")
        }
        return remindtime
    }

    //处理提醒与时间的方法
    private fun remindToTime(remindType: Int): String {
        var remindtime: String
        var date = Date()
        remindtime = selectYear.toString() + "-" + selectMonth + "-" + selectDay + " " + addScheduleBinding.textTime.text + ":" + "00"
        @SuppressLint("SimpleDateFormat") val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            date = std.parse(remindtime)
        } catch (ignored: Exception) {
        }
        when (remindType) {
            1 -> {
                remindtime = std.format(date)
            }
            2 -> {
                date.time = date.time - 60 * 1000
                remindtime = std.format(date)
            }
            3 -> {
                date.time = date.time - 5 * 60 * 1000
                remindtime = std.format(date)
            }
            4 -> {
                date.time = date.time - 10 * 60 * 1000
                remindtime = std.format(date)
            }
            5 -> {
                date.time = date.time - 15 * 60 * 1000
                remindtime = std.format(date)
            }
            6 -> {
                date.time = date.time - 20 * 60 * 1000
                remindtime = std.format(date)
            }
            7 -> {
                date.time = date.time - 25 * 60 * 1000
                remindtime = std.format(date)
            }
            8 -> {
                date.time = date.time - 30 * 60 * 1000
                remindtime = std.format(date)
            }
            9 -> {
                date.time = date.time - 45 * 60 * 1000
                remindtime = std.format(date)
            }
            10 -> {
                date.time = date.time - 60 * 60 * 1000
                remindtime = std.format(date)
            }
            11 -> {
                date.time = date.time - 2 * 60 * 60 * 1000
                remindtime = std.format(date)
            }
            12 -> {
                date.time = date.time - 3 * 60 * 60 * 1000
                remindtime = std.format(date)
            }
            13 -> {
                date.time = date.time - 12 * 60 * 60 * 1000
                remindtime = std.format(date)
            }
            14 -> {
                date.time = date.time - 24 * 60 * 60 * 1000
                remindtime = std.format(date)
            }
            15 -> {
                date.time = date.time - 2 * 24 * 60 * 60 * 1000
                remindtime = std.format(date)
            }
            16 -> {
                date.time = date.time - 7 * 24 * 60 * 60 * 1000
                remindtime = std.format(date)
            }
        }
        return remindtime
    }

    //检测提醒是否过期
    private fun checkRemindTime(reminds: String): Int {
        var remindCheck = 0
        val now = Date()
        var date = Date()
        val str = reminds.split(",").toTypedArray()
        @SuppressLint("SimpleDateFormat") val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        for (s in str) {
            try {
                date = std.parse(s)
            } catch (ignored: java.lang.Exception) {
            }
            if (date.time < now.time) {
                remindCheck++
            }
        }
        return remindCheck
    }

    //删除日程的对话框
    private fun gotoDeleteDialog() {
        builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.delete_schedule_title)
                .setMessage(R.string.delete_schedule_message)
                .setPositiveButton(R.string.dialog_button_ok) { dialog, _ ->
                    for (i in mSchedules.indices) {
                        if (mSchedules[i].isEditorChecked) {
                            vm.deleteSchedule(mSchedules[i])
                        }
                    }
                    for (i in mFinishSchedules.indices) {
                        if (mFinishSchedules[i].isEditorChecked) {
                            vm.deleteSchedule(mFinishSchedules[i])
                        }
                    }
                    dialog.dismiss()
                    updateScheduleList()
                    binding.fabBtn.visibility = View.VISIBLE
                    scheduleListFinishHeadBinding.deleteSchedule.visibility = View.GONE
                    scheduleListFinishHeadBinding.scheduleListFinish.visibility = View.VISIBLE
                    scheduleListFinishHeadBinding.scheduleDeleteBack.visibility = View.GONE
                    scheduleListHeadBinding.deleteSchedule.visibility = View.GONE
                    scheduleListHeadBinding.scheduleListHead.visibility = View.VISIBLE
                    scheduleListHeadBinding.scheduleDeleteBack.visibility = View.GONE
                    setCalendarTag()
                }
                .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    //遍历日期设置日历标记
    private fun setCalendarTag() {
        val map: MutableMap<String, Calendar> = HashMap()
        vm.getScheduleDayOfTag()!!.observe(viewLifecycleOwner, { strings: List<String> ->
            for (i in strings.indices) {
                map[getSchemeCalendar(strings[i].substring(0, 4).toInt(), strings[i].substring(5, 7).toInt(), strings[i].substring(8, 10).toInt()).toString()] = getSchemeCalendar(strings[i].substring(0, 4).toInt(), strings[i].substring(5, 7).toInt(), strings[i].substring(8, 10).toInt())
            }
            binding.calendarView.setSchemeDate(map)
        })
    }

    //为日历添加标记
    private fun getSchemeCalendar(year: Int, month: Int, day: Int): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = 0xFF0000//如果单独标记颜色、则会使用这个颜色
        calendar.addScheme(Scheme())
        calendar.addScheme(-0xff7800, "假")
        calendar.addScheme(-0xff7800, "节")
        return calendar
    }

    //添加日程列表
    private fun updateScheduleList() {
        val day = "%" + selectYear + "-" + processingTime(selectMonth) + "-" + processingTime(selectDay) + "%"
        vm.getUnfinishedScheduleOfDay(day)!!.observe(viewLifecycleOwner, { schedules: List<Schedule> ->
            for (i in schedules.indices) {
                schedules[i].isChecked = false
            }
            if (schedules.isEmpty()) {
                scheduleListHeadBinding.root.visibility = View.GONE
                scheduleFootBinding.root.visibility = View.GONE
            } else {
                scheduleListHeadBinding.root.visibility = View.VISIBLE
                scheduleFootBinding.root.visibility = View.VISIBLE
            }
            scheduleAdapter.setList(schedules)
            mSchedules = scheduleAdapter.data
        })
        vm.getFinishedScheduleOfDay(day)!!.observe(viewLifecycleOwner, { schedules: List<Schedule> ->
            for (i in schedules.indices) {
                schedules[i].isChecked = true
            }
            if (schedules.isEmpty()) {
                scheduleListFinishHeadBinding.root.visibility = View.GONE
                finishScheduleFootBinding.root.visibility = View.GONE
            } else {
                scheduleListFinishHeadBinding.root.visibility = View.VISIBLE
                finishScheduleFootBinding.root.visibility = View.VISIBLE
            }
            finishScheduleAdapter.setList(schedules)
            mFinishSchedules = finishScheduleAdapter.data
        })
    }

    //处理日期数字 例如6月转为06，7分钟转化为07
    private fun processingTime(time: Int): String {
        return if (time < 10) {
            "0$time"
        } else {
            time.toString()
        }
    }


    //检测通知是否开启的方法
    private fun isNotificationEnabled(): Boolean {
        return try {
                NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                false
            }
    }

    //当通知未开启时弹出框
    private fun getNotification() {
        if (!isNotificationEnabled()) {
            val builder = AlertDialog.Builder(context)
                    .setCancelable(true)
                    .setTitle(R.string.notify_authority_dialog_title)
                    .setMessage(R.string.notify_authority_dialog_message)
                    .setNegativeButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.cancel() }
                    .setPositiveButton(R.string.notify_authority_dialog_ok_button) { dialog, _ ->
                        dialog.cancel()
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.data = Uri.parse("package:" + requireActivity().packageName)
                        startActivity(intent)
                    }
            builder.create().show()
        }
    }

}