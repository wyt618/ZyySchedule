package com.example.zyyschedule.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NotificationUtils
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
import com.jeremyliao.liveeventbus.LiveEventBus
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NotifyDataSetChanged")
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        builder = AlertDialog.Builder(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.calendar_fragment, container, false)
        dateJumpDialog = DataBindingUtil.inflate(inflater, R.layout.dialog_date, container, false)
        labelDialogHeadBinding =
            DataBindingUtil.inflate(inflater, R.layout.label_dialog_head, container, false)
        addScheduleBinding =
            DataBindingUtil.inflate(inflater, R.layout.add_schedule, container, false)
        timePickerBinding =
            DataBindingUtil.inflate(inflater, R.layout.timepicker_dialog, container, false)
        priorityDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.priority_dialog, container, false)
        labelBinding =
            DataBindingUtil.inflate(inflater, R.layout.all_label_dialog, container, false)
        remindDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.remind_dialog, container, false)
        remindListHeadBinding =
            DataBindingUtil.inflate(inflater, R.layout.remind_list_head, container, false)
        scheduleListHeadBinding =
            DataBindingUtil.inflate(inflater, R.layout.schedule_list_head, container, false)
        scheduleFootBinding =
            DataBindingUtil.inflate(inflater, R.layout.schedule_foot, container, false)
        scheduleListFinishHeadBinding =
            DataBindingUtil.inflate(inflater, R.layout.schedule_list_finish_head, container, false)
        finishScheduleFootBinding =
            DataBindingUtil.inflate(inflater, R.layout.finish_schedule_foot, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        time = java.util.Calendar.getInstance()
        selectYear = binding.calendarView.curYear
        selectMonth = binding.calendarView.curMonth
        selectDay = binding.calendarView.curDay
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.flCurrent.setOnClickListener(this)
        binding.tvYear.text = binding.calendarView.curYear.toString()
        binding.tvMonthDay.text =
            binding.calendarView.curMonth.toString() + "月" + binding.calendarView.curDay + "日"
        binding.tvLunar.text = "今日"
        binding.calendarView.setOnCalendarSelectListener(this)
        binding.fabBtn.setOnClickListener(this)
        binding.goBack.setOnClickListener(this)
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
        binding.deleteButton.setOnClickListener(this)
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
        val remindList = vm.remindListData(requireContext())
        remindAdapter.setList(remindList)
        remindAdapter.setHeader(remindListHeadBinding)
        remindAdapter.setHeaderView(remindListHeadBinding.root)
        scheduleListHeadBinding.scheduleListHead.text =
            selectMonth.toString() + "月" + selectDay + "日"
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
        scheduleListHeadBinding.scheduleListHead.text =
            selectMonth.toString() + "月" + selectDay + "日"
        enabledFalse()
        labelDialogHeadBinding.root.setOnClickListener {
            val intent = Intent(activity, AddLabelActivity::class.java)
            startActivity(intent)
        }
        //编辑模式下选中的监听
        scheduleAdapter.pitchOnNumber.observe(viewLifecycleOwner, {
            binding.goBackText.text = "选中${it}项"
            if (it > 0) {
                enabledTrue()
            } else {
                enabledFalse()
            }
        })
        finishScheduleAdapter.pitchOnNumber.observe(viewLifecycleOwner, {
            binding.goBackText.text = "选中${it}项"
            if (it > 0) {
                enabledTrue()
            } else {
                enabledFalse()
            }
        })
        //对标签数据进行监听，刷新标签选择对话框，对话框点击事件
        vm.getAllLabel().observe(viewLifecycleOwner, { labels: List<Label>? ->
            labelAdapter.setList(labels)
            if (labels != null) {
                labelAdapter.notifyItemChanged(labels.size)
            }
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
                .setTitle(R.string.calendar_dialog_title)
                .setPositiveButton(R.string.dialog_button_ok) { _, _ ->
                    binding.calendarView.scrollToCalendar(
                        dateJumpDialog.datePicker.year,
                        dateJumpDialog.datePicker.month + 1,
                        dateJumpDialog.datePicker.dayOfMonth
                    )
                    Toast.makeText(
                        context,
                        dateJumpDialog.datePicker.year.toString() + "年" + (dateJumpDialog.datePicker.month + 1) + "月" + dateJumpDialog.datePicker.dayOfMonth + "日",
                        Toast.LENGTH_SHORT
                    ).show()
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
            binding.rlTool.visibility = View.GONE
            binding.editTool.visibility = View.VISIBLE
            binding.editorLayout.visibility = View.VISIBLE
            LiveEventBus
                .get("SomeF_MainA", String::class.java)
                .post("gone_navigation")
            binding.fabBtn.visibility = View.GONE
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
            binding.rlTool.visibility = View.GONE
            binding.editTool.visibility = View.VISIBLE
            binding.editorLayout.visibility = View.VISIBLE
            LiveEventBus
                .get("SomeF_MainA", String::class.java)
                .post("gone_navigation")
            binding.fabBtn.visibility = View.GONE
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


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.fl_current -> binding.calendarView.scrollToCurrent()
                R.id.fab_btn -> {
                    gotoAddSchedule()
                    vm.addScheduleDateAgo(
                        selectYear,
                        selectMonth,
                        selectDay,
                        binding.calendarView.curYear,
                        binding.calendarView.curMonth,
                        binding.calendarView.curDay
                    )
                }
                R.id.add_schedule_selectTime, R.id.textTime -> gotoGetTime()
                R.id.priority_button, R.id.text_priority -> gotoPriority()
                R.id.label_button, R.id.schedule_label -> gotoChooseLabel()
                R.id.remind_button, R.id.remind_text -> gotoAddRemind()
                R.id.send_schedule -> addSchedule()
                R.id.go_back -> exitEditor()
                R.id.delete_button -> gotoDeleteDialog()
            }
        }
    }

    override fun onCalendarOutOfRange(calendar: Calendar?) {}


    private fun exitEditor() {
        binding.fabBtn.visibility = View.VISIBLE
        LiveEventBus
            .get("SomeF_MainA", String::class.java)
            .post("visible_navigation")
        binding.editorLayout.visibility = View.GONE
        binding.editTool.visibility = View.GONE
        binding.rlTool.visibility = View.VISIBLE
        for (i in mSchedules.indices) {
            mSchedules[i].isEditor = false
        }
        for (i in mFinishSchedules.indices) {
            mFinishSchedules[i].isEditor = false
        }
        scheduleAdapter.notifyDataSetChanged()
        finishScheduleAdapter.notifyDataSetChanged()
    }

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
        scheduleListHeadBinding.scheduleListHead.text =
            selectMonth.toString() + "月" + selectDay + "日"
        scheduleListHeadBinding.scheduleListHead.visibility = View.VISIBLE
        updateScheduleList()
        setCalendarTag()
        exitEditor()
    }

    //添加日程悬浮窗显示
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gotoAddSchedule() {
        addScheduleBinding.editText.text = null
        addScheduleBinding.priorityButton.setImageResource(R.drawable.priority_flag)
        addScheduleBinding.textPriority.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.priority_null
            )
        )
        vm.priority.postValue(getString(R.string.priority_null_text))
        vm.priorityid.postValue(0)
        vm.label.postValue(getString(R.string.title_not_classified))
        addScheduleBinding.scheduleLabelId.text = "0"
        vm.addScheduleTime.postValue("00:00")
        vm.remindText.postValue("无提醒")
        remindAdapter.addRemind = StringBuffer("无提醒")

        binding.fabBtn.visibility = View.GONE
        addScheduleBinding.sendSchedule.isClickable =
            addScheduleBinding.editText.text.toString().trim().isNotEmpty()
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
        val d = requireContext().resources!!.displayMetrics
        val p = addSchedule.window!!.attributes
        p.width = d.widthPixels
        addSchedule.window!!.attributes = p
        addSchedule.window!!.setBackgroundDrawableResource(R.drawable.add_schedule)
        addSchedule.setOnDismissListener { binding.fabBtn.visibility = View.VISIBLE }
        addScheduleBinding.editText.requestFocus()
        val imm: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS)
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
            .setTitle(R.string.add_schedule_timePicker)
            .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(
                R.string.dialog_button_ok
            ) { _, _ ->
                vm.addScheduleTime.setValue(
                    processingTime(timePickerBinding.hourPicker.value) + ":" + processingTime(
                        timePickerBinding.minePicker.value
                    )
                )
            }
            .setOnDismissListener {
                if (addScheduleBinding.textTime.text.toString() == "00:00") {
                    timePickerBinding.hourPicker.value = 0
                    timePickerBinding.minePicker.value = 0
                } else {
                    timePickerBinding.hourPicker.value =
                        Objects.requireNonNull(vm.addScheduleTime.value!!.substring(0, 2).toInt())
                    timePickerBinding.minePicker.value =
                        vm.addScheduleTime.value!!.substring(3).toInt()
                }
                vm.addScheduleTime.setValue(
                    processingTime(timePickerBinding.hourPicker.value) + ":" + processingTime(
                        timePickerBinding.minePicker.value
                    )
                )
            }
        builder.create().show()
    }

    //选择优先级
    private fun gotoPriority() {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        priorityDialogBinding.priorityList.layoutManager = layoutManager
        val priorityData = vm.priorityListData(requireContext())
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
        val d = requireContext().resources!!.displayMetrics
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
                    addScheduleBinding.remindText.text =
                        remindAdapter.addRemind.substring(4, remindAdapter.addRemind.length)
                }
                getNotification()
            }
            .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
        remindDialog = builder.create()
        remindDialog.window!!.setBackgroundDrawableResource(R.drawable.dialog_background)
        remindDialog.show()
        val d = requireContext().resources!!.displayMetrics
        val p = remindDialog.window!!.attributes
        p.width = d.widthPixels / 3
        p.height = d.heightPixels / 2
        remindDialog.window!!.attributes = p
    }

    //新增日程到数据库
    private fun addSchedule() {
        val schedule = Schedule()
        val startTime =
            selectYear.toString() + "-" + processingTime(selectMonth) + "-" + processingTime(
                selectDay
            ) + " " + vm.addScheduleTime.value + ":00"
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
                Toast.makeText(context, "抱歉，有" + remindCheck + "条提醒因为超出当前时间无效", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    //将提醒字符转化为时间字符
    private fun remindChangeTime(): String {
        var remindTime: String
        remindTime = remindAdapter.addRemind.toString()
        if (remindTime == "无提醒") {
            remindTime = ""
        } else {
            vm.getDateForRemindToTime(
                selectYear,
                selectMonth,
                selectDay,
                addScheduleBinding.textTime.text.toString()
            )
            remindTime = remindTime.replace("无提醒", "")
            remindTime = remindTime.replace(",准时", vm.remindToTime(1) + ",")
            remindTime = remindTime.replace(",提前1分钟", vm.remindToTime(2) + ",")
            remindTime = remindTime.replace(",提前5分钟", vm.remindToTime(3) + ",")
            remindTime = remindTime.replace(",提前10分钟", vm.remindToTime(4) + ",")
            remindTime = remindTime.replace(",提前15分钟", vm.remindToTime(5) + ",")
            remindTime = remindTime.replace(",提前20分钟", vm.remindToTime(6) + ",")
            remindTime = remindTime.replace(",提前25分钟", vm.remindToTime(7) + ",")
            remindTime = remindTime.replace(",提前30分钟", vm.remindToTime(8) + ",")
            remindTime = remindTime.replace(",提前45分钟", vm.remindToTime(9) + ",")
            remindTime = remindTime.replace(",提前1个小时", vm.remindToTime(10) + ",")
            remindTime = remindTime.replace(",提前2个小时", vm.remindToTime(11) + ",")
            remindTime = remindTime.replace(",提前3个小时", vm.remindToTime(12) + ",")
            remindTime = remindTime.replace(",提前12个小时", vm.remindToTime(13) + ",")
            remindTime = remindTime.replace(",提前1天", vm.remindToTime(14) + ",")
            remindTime = remindTime.replace(",提前2天", vm.remindToTime(15) + ",")
        }
        return remindTime
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
        return remindCheck - 1
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
                LiveEventBus
                    .get("SomeF_MainA", String::class.java)
                    .post("visible_navigation")
                binding.editorLayout.visibility = View.GONE
                binding.editTool.visibility = View.GONE
                binding.rlTool.visibility = View.VISIBLE
                setCalendarTag()
            }
            .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    //遍历日期设置日历标记
    private fun setCalendarTag() {
        val map: MutableMap<String, Calendar> = HashMap()
        vm.getScheduleDayOfTag().observe(viewLifecycleOwner, { strings: List<String> ->
            for (i in strings.indices) {
                map[getSchemeCalendar(
                    strings[i].substring(0, 4).toInt(),
                    strings[i].substring(5, 7).toInt(),
                    strings[i].substring(8, 10).toInt()
                ).toString()] = getSchemeCalendar(
                    strings[i].substring(0, 4).toInt(),
                    strings[i].substring(5, 7).toInt(),
                    strings[i].substring(8, 10).toInt()
                )
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
        val day =
            "%" + selectYear + "-" + processingTime(selectMonth) + "-" + processingTime(selectDay) + "%"
        vm.getUnfinishedScheduleOfDay(day)
            .observe(viewLifecycleOwner, { schedules: List<Schedule> ->
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
        vm.getFinishedScheduleOfDay(day).observe(viewLifecycleOwner, { schedules: List<Schedule> ->
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
            finishScheduleAdapter.otherDate = scheduleAdapter.data
            scheduleAdapter.otherDate = finishScheduleAdapter.data
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


    //当通知未开启时弹出框
    private fun getNotification() {
        if (!NotificationUtils.areNotificationsEnabled()) {
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

    //控制编辑栏按钮可以点击
    private fun enabledTrue() {
        binding.deleteButton.isClickable = true
        binding.moreButton.isClickable = true
        binding.labelButton.isClickable = true
        binding.timeButton.isClickable = true
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_outline_24)?.let {
            DrawableCompat.setTint(it, Color.BLACK)
            binding.deleteButton.setImageDrawable(it)
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_more_24)?.let {
            DrawableCompat.setTint(it, Color.BLACK)
            binding.moreButton.setImageDrawable(it)
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_schedule_24)?.let {
            DrawableCompat.setTint(it, Color.BLACK)
            binding.labelButton.setImageDrawable(it)
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_toolbar)?.let {
            DrawableCompat.setTint(it, Color.BLACK)
            binding.timeButton.setImageDrawable(it)
        }
    }

    //控制编辑栏按钮不可点击
    private fun enabledFalse() {
        binding.deleteButton.isClickable = false
        binding.moreButton.isClickable = false
        binding.labelButton.isClickable = false
        binding.timeButton.isClickable = false
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_delete_outline_24)?.let {
            DrawableCompat.setTint(it, Color.GRAY)
            binding.deleteButton.setImageDrawable(it)
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_more_24)?.let {
            DrawableCompat.setTint(it, Color.GRAY)
            binding.moreButton.setImageDrawable(it)
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_schedule_24)?.let {
            DrawableCompat.setTint(it, Color.GRAY)
            binding.labelButton.setImageDrawable(it)
        }
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_calendar_toolbar)?.let {
            DrawableCompat.setTint(it, Color.GRAY)
            binding.timeButton.setImageDrawable(it)
        }
    }

}