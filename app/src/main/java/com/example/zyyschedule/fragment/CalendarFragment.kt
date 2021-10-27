package com.example.zyyschedule.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils.dp2px
import com.blankj.utilcode.util.NotificationUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.adapter.*
import com.example.zyyschedule.broadcastreceiver.NotificationReceiver
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.*
import com.example.zyyschedule.viewmodel.CalendarViewModel
import com.google.gson.Gson
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.Calendar.Scheme
import com.haibin.calendarview.CalendarView
import com.jeremyliao.liveeventbus.LiveEventBus
import com.library.flowlayout.FlowLayoutManager
import com.library.flowlayout.SpaceItemDecoration
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n", "NotifyDataSetChanged")
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
    private lateinit var labelLongClickBinding: LabelLongClickPopupWindowBinding
    private lateinit var labelItemFootBinding: LabbelItemFootBinding
    private lateinit var finishScheduleAdapter: ScheduleAdapter
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var priorityListAdapter: PriorityListAdapter
    private var labelAdapter = LabelAdapter(R.layout.label_item)
    private var remindAdapter = RemindAdapter()
    private var editScheduleLabelAdapter = EditScheduleLabelAdapter()

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
    val day: MutableLiveData<String> = MutableLiveData(
        "%" + selectYear + "-" + processingTime(selectMonth) + "-" + processingTime(selectDay) + "%"
    )
    private lateinit var time: java.util.Calendar
    private var editSchedule: MutableLiveData<Schedule> = MutableLiveData()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        builder = AlertDialog.Builder(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.calendar_fragment, container, false)
        dateJumpDialog = DataBindingUtil.inflate(inflater, R.layout.dialog_date, container, false)
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
        labelLongClickBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.label_long_click_popup_window,
                container,
                false
            )
        labelItemFootBinding =
            DataBindingUtil.inflate(inflater, R.layout.labbel_item_foot, container, false)
        return binding.root
    }


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
        binding.deleteButton.setOnClickListener(this)
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
        labelItemFootBinding.insertLabelText.setOnClickListener(this)
        addScheduleBinding.vm = vm
        addScheduleBinding.lifecycleOwner = this
        timePickerBinding.hourPicker.maxValue = 23
        timePickerBinding.hourPicker.minValue = 0
        timePickerBinding.hourPicker.value = 0
        timePickerBinding.minePicker.minValue = 0
        timePickerBinding.minePicker.maxValue = 59
        timePickerBinding.minePicker.value = 0
        labelBinding.labelList.layoutManager = layoutManager
        labelAdapter.setLoadFragment("CalendarFragment")
        labelAdapter.addFooterView(labelItemFootBinding.root)
        labelBinding.labelList.adapter = labelAdapter
        val remindLayoutManager = LinearLayoutManager(context)
        remindLayoutManager.orientation = LinearLayoutManager.VERTICAL
        remindDialogBinding.remindChooseList.layoutManager = remindLayoutManager
        remindDialogBinding.remindChooseList.adapter = remindAdapter
        val remindList = vm.remindListData()
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
        //设置日程列表侧滑和拖拽
        scheduleAdapter.draggableModule.isSwipeEnabled = true
        scheduleAdapter.draggableModule.isDragEnabled = true
        scheduleAdapter.draggableModule.setOnItemSwipeListener(object : OnItemSwipeListener {
            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                Thread.sleep(400)
                scheduleAdapter.data[pos].state = "1"
                vm.changeStateSchedule(scheduleAdapter.data[pos])
            }

            override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

            override fun onItemSwipeMoving(
                canvas: Canvas?,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                isCurrentlyActive: Boolean
            ) {
            }
        })
        finishScheduleAdapter.draggableModule.isSwipeEnabled = true
        finishScheduleAdapter.draggableModule.isDragEnabled = true
        finishScheduleAdapter.draggableModule.setOnItemSwipeListener(object : OnItemSwipeListener {
            override fun onItemSwipeStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                Thread.sleep(400)
                finishScheduleAdapter.data[pos].state = "0"
                vm.changeStateSchedule(finishScheduleAdapter.data[pos])
            }

            override fun clearView(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder?, pos: Int) {}

            override fun onItemSwipeMoving(
                canvas: Canvas?,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                isCurrentlyActive: Boolean
            ) {
            }
        })

        //绑定adapter
        binding.scheduleList.adapter = scheduleAdapter
        binding.finishScheduleList.adapter = finishScheduleAdapter
        scheduleListHeadBinding.scheduleListHead.text =
            selectMonth.toString() + "月" + selectDay + "日"
        enabledFalse()
        //编辑模式下选中的监听
        scheduleAdapter.pitchOnNumber.observe(viewLifecycleOwner, {
            finishScheduleAdapter.pitchOnNumber.value?.let { fNumber ->
                val number = it + fNumber
                if (number > 0) {
                    enabledTrue()
                } else {
                    enabledFalse()
                }
                binding.goBackText.text = "选中${number}项"
            }
        })
        finishScheduleAdapter.pitchOnNumber.observe(viewLifecycleOwner, {
            scheduleAdapter.pitchOnNumber.value?.let{ ufNumber ->
            val number = it + ufNumber
            if (number > 0) {
                enabledTrue()
            } else {
                enabledFalse()
            }
            binding.goBackText.text = "选中${number}项"
            }
        })
        //对标签数据进行监听，刷新标签选择对话框，对话框点击事件
        vm.getAllLabel().observe(viewLifecycleOwner, { labels: List<Label>? ->
            labelAdapter.setList(labels)
            if (labels != null) {
                labelAdapter.notifyItemChanged(labels.size)
            }
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
                    remindAdapter.notifyItemChanged(i + 1)
                }
            } else {
                remindListHeadBinding.remindHeadBox.isClickable = true
            }
        }
        //完成和未完成日程item长按事件
        scheduleAdapter.setOnItemLongClickListener { adapter, _, _ ->
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
        finishScheduleAdapter.setOnItemLongClickListener { adapter, _, _ ->
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
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)


        //完成和未完成日程item点击事件
        finishScheduleAdapter.setOnItemClickListener { _, _, position ->
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
            editSchedule.value = mFinishSchedules[position]
            editSchedule.value?.let { it ->
                val (color, text) = editTimeText(it.startTime)
                binding.editDate.text = text
                binding.editDate.setTextColor(color)
                when (it.state) {
                    "1" -> binding.editState.isChecked = true
                    "0" -> binding.editState.isChecked = false
                }
                binding.editTitle.setText(it.title)
                binding.editDetailed.setText(it.detailed)
                binding.editFlag.setImageResource(R.drawable.priority_flag)
                var imageColor = 0
                when (it.priority) {
                    0 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_null)
                    1 -> imageColor = ContextCompat.getColor(requireContext(), R.color.priority_low)
                    2 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_middle)
                    3 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_high)
                }
                binding.editFlag.imageTintList = ColorStateList.valueOf(imageColor)
                //处理多标签显示
                if (!it.labelId.equals("~0~")) {
                    val labelIds = it.labelId?.split("~")?.dropWhile { labelId ->
                        labelId.isEmpty()
                    }?.toTypedArray()
                    labelIds?.let {
                        val labelList: ArrayList<Label> = arrayListOf()
                        for (i in labelIds.indices) {
                            if (labelIds[i] != "" && labelIds[i] != "0") {
                                vm.getLabelTitle(labelIds[i]).observe(viewLifecycleOwner) { label ->
                                    labelList.add(label)
                                    editScheduleLabelAdapter.setList(labelList)
                                }
                            }
                        }
                    }
                } else {
                    editScheduleLabelAdapter.setList(null)
                }
            }
        }
        scheduleAdapter.setOnItemClickListener { _, _, position ->
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
            editSchedule.value = mSchedules[position]
            editSchedule.value?.let {
                val (color, text) = editTimeText(it.startTime)
                binding.editDate.text = text
                binding.editDate.setTextColor(color)
                when (it.state) {
                    "1" -> binding.editState.isChecked = true
                    "0" -> binding.editState.isChecked = false
                }
                binding.editTitle.setText(it.title)
                binding.editDetailed.setText(it.detailed)
                binding.editFlag.setImageResource(R.drawable.priority_flag)
                var imageColor = 0
                when (it.priority) {
                    0 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_null)
                    1 -> imageColor = ContextCompat.getColor(requireContext(), R.color.priority_low)
                    2 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_middle)
                    3 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_high)
                }
                binding.editFlag.imageTintList = ColorStateList.valueOf(imageColor)
                //处理多标签显示
                if (!it.labelId.equals("~0~")) {
                    val labelIds = it.labelId?.split("~")?.dropWhile { labelId ->
                        labelId.isEmpty()
                    }?.toTypedArray()
                    labelIds?.let {
                        val labelList: ArrayList<Label> = arrayListOf()
                        for (i in labelIds.indices) {
                            if (labelIds[i] != "" && labelIds[i] != "0") {
                                vm.getLabelTitle(labelIds[i]).observe(viewLifecycleOwner) { label ->
                                    labelList.add(label)
                                    editScheduleLabelAdapter.setList(labelList)
                                }
                            }
                        }
                    }
                } else {
                    editScheduleLabelAdapter.setList(null)
                }
            }
        }
        //编辑状态的日程数据更新
        editSchedule.observe(viewLifecycleOwner) {
            if (it.state == "0") {
                it.tagRemind = false
                LiveEventBus
                    .get("SomeF_MainA", String::class.java)
                    .post("update_remind")
            }
            vm.updateSchedule(it)
        }

        binding.editState.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            editSchedule.value?.let {
                if (isChecked) {
                    it.state = "1"
                } else {
                    it.state = "0"
                }
                editSchedule.value = editSchedule.value
            }
        }

        binding.editTitle.addTextChangedListener { text ->
            editSchedule.value?.let {
                it.title = text.toString()
            }
            editSchedule.value = editSchedule.value
        }

        binding.editDetailed.addTextChangedListener { text ->
            editSchedule.value?.let {
                it.detailed = text.toString()
            }
            editSchedule.value = editSchedule.value
        }

        editScheduleLabelAdapter.setOnItemLongClickListener { _: BaseQuickAdapter<Any?, BaseViewHolder>, item: View, _: Int ->
            val labelWindow =
                PopupWindow(
                    labelLongClickBinding.root, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    true
                )
            labelWindow.isTouchable = true
            labelWindow.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.button_bg_press
                )
            )

            val location = IntArray(2)
            item.getLocationOnScreen(location)
            labelWindow.showAtLocation(
                binding.editLabel, Gravity.START and Gravity.TOP,
                location[0], location[1] - item.height - 5
            )
            true
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                scheduleAdapter.draggableModule.isSwipeEnabled = false
                scheduleAdapter.draggableModule.isDragEnabled = false
                finishScheduleAdapter.draggableModule.isSwipeEnabled = false
                finishScheduleAdapter.draggableModule.isDragEnabled = false
                binding.divider.visibility = View.VISIBLE
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
                LiveEventBus
                    .get("SomeF_MainA", String::class.java)
                    .post("gone_navigation")
            }

            override fun onDrawerClosed(drawerView: View) {
                binding.divider.visibility = View.GONE
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                if (binding.editorLayout.visibility == View.GONE) {
                    LiveEventBus
                        .get("SomeF_MainA", String::class.java)
                        .post("visible_navigation")
                }
                scheduleAdapter.draggableModule.isSwipeEnabled = true
                scheduleAdapter.draggableModule.isDragEnabled = true
                finishScheduleAdapter.draggableModule.isSwipeEnabled = true
                finishScheduleAdapter.draggableModule.isDragEnabled = true
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
        //对弹出选择标签的对话框editText进行监听
        labelBinding.labelAddEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                val labelText: MutableLiveData<String> = MutableLiveData(text.toString())
                labelText.observe(viewLifecycleOwner) {
                    if (it.trim().isEmpty()) {
                        vm.getAllLabel().observe(viewLifecycleOwner) { labelList ->
                            labelAdapter.setList(labelList)
                            labelAdapter.notifyDataSetChanged()
                        }
                        labelItemFootBinding.root.visibility = View.GONE
                    } else {
                        vm.checkLabelTFI(it).observe(viewLifecycleOwner) { count ->
                            if (count > 0) {
                                labelItemFootBinding.root.visibility = View.GONE
                            } else {
                                labelItemFootBinding.root.visibility = View.VISIBLE
                                labelItemFootBinding.insertLabelText.text = "创建\"${it}\""
                            }
                        }
                        vm.fuzzyLabelTitle("%$it%").observe(viewLifecycleOwner) { labels ->
                            labelAdapter.setList(labels)
                            labelAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
        val editViewLabelManager = FlowLayoutManager()
        binding.editLabel.addItemDecoration(SpaceItemDecoration(dp2px(10F)))
        binding.editLabel.layoutManager = editViewLabelManager
        binding.editLabel.adapter = editScheduleLabelAdapter
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
                R.id.insert_label_text -> addLabel()
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

    //日历点击时事件
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
        vm.priorityId.postValue(0)
        vm.label.postValue(getString(R.string.title_not_classified))
        addScheduleBinding.scheduleLabelId.text = "~0~"
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
        window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val d = requireContext().resources?.displayMetrics
            val p = it.attributes
            if (d != null) {
                p.width = d.widthPixels
            }
            it.attributes = p
            it.setBackgroundDrawableResource(R.drawable.add_schedule)

        }
        val imm: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        addSchedule.setOnDismissListener {
            binding.fabBtn.visibility = View.VISIBLE
            addScheduleBinding.editText.post {
                imm.hideSoftInputFromWindow(
                    addScheduleBinding.editText.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
            }
        }
        addScheduleBinding.editText.requestFocus()
        addScheduleBinding.editText.post {
            imm.showSoftInput(addScheduleBinding.editText, InputMethodManager.SHOW_IMPLICIT)
        }
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
            vm.priorityId.value = position
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
            .setPositiveButton(R.string.dialog_button_ok) { dialog: DialogInterface, _: Int ->
                addScheduleBinding.scheduleLabelId.text = labelAdapter.labelIds
                val labelText = labelAdapter.labelTitles.replace("无标签,", "")
                addScheduleBinding.scheduleLabel.text = labelText
                dialog.dismiss()
            }
            .setNeutralButton(R.string.dialog_button_cancel) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
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
        remindListHeadBinding.remindHeadBox.isChecked = true
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
        val d = requireContext().resources.displayMetrics
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
        schedule.remind = vm.remindChangeTime(
            remindAdapter.addRemind.toString(),
            selectYear,
            selectMonth,
            selectDay,
            addScheduleBinding.textTime.text.toString()
        )
        schedule.title = addScheduleBinding.editText.text.toString()
        schedule.detailed = null
        schedule.state = "0"
        schedule.priority = addScheduleBinding.priorityId.text.toString().toInt()
        if (addScheduleBinding.scheduleLabelId.text.toString().trim().isEmpty()) {
            schedule.labelId = "0"
        } else {
            schedule.labelId = addScheduleBinding.scheduleLabelId.text.toString().trim()
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


    //检测提醒是否过期
    private fun checkRemindTime(reminds: String): Int {
        var remindCheck = 0
        val now = Date()
        var date = Date()
        val str = reminds.split(",").toTypedArray()
        @SuppressLint("SimpleDateFormat") val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        for (s in str) {
            try {
                std.parse(s)?.let {
                    date = it
                }
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
                        cancelNotification(mSchedules[i])
                    }
                }
                for (i in mFinishSchedules.indices) {
                    if (mFinishSchedules[i].isEditorChecked) {
                        vm.deleteSchedule(mFinishSchedules[i])
                        cancelNotification(mFinishSchedules[i])
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


    //删除时取消提醒的方法
    @SuppressLint("UnspecifiedImmutableFlag", "SimpleDateFormat")
    private fun cancelNotification(schedule: Schedule) {
        val remind = schedule.remind?.split(",")?.dropLastWhile { it.isEmpty() }?.toTypedArray()
        val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date = Date()
        val now = Date()
        for (i in remind?.indices!!) {
            try {
                std.parse(remind[i])?.let {
                    date = it
                }
            } catch (ignored: Exception) {
            }
            if (date.time > now.time) {
                val gson = Gson()
                val intent = Intent(context, NotificationReceiver::class.java)
                intent.action = "Notification_Receiver"
                intent.putExtra("remindSchedule", gson.toJson(schedule))
                intent.putExtra("PendingIntentCode", schedule.id?.plus(i * 1000))
                val sender = schedule.id?.plus(i * 1000)?.let {
                    PendingIntent.getBroadcast(
                        requireContext(), it, intent,
                        FLAG_UPDATE_CURRENT
                    )
                }
                val am = context?.getSystemService(ALARM_SERVICE) as AlarmManager
                am.cancel(sender)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun editTimeText(date: String?): Pair<Int, String> {
        val weekDays = arrayOf("日", "一", "二", "三", "四", "五", "六")
        val now = java.util.Calendar.getInstance()
        val startDate = java.util.Calendar.getInstance()
        val timeText = StringBuffer("")
        val std = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var textColor = Color.BLACK
        date?.let { d ->
            std.parse(d)?.let {
                startDate.time = it
            }
        }
        if (startDate[java.util.Calendar.YEAR] != now[java.util.Calendar.YEAR]) {
            timeText.append(startDate[java.util.Calendar.YEAR]).append("年")
        }
        if (startDate[java.util.Calendar.MONTH] == now[java.util.Calendar.MONTH]) {
            when (startDate[java.util.Calendar.WEEK_OF_MONTH]) {
                now[java.util.Calendar.WEEK_OF_MONTH] -> timeText.append("周${weekDays[startDate[java.util.Calendar.DAY_OF_WEEK] - 1]}")
                    .append(",")
                now[java.util.Calendar.WEEK_OF_MONTH] - 1 -> timeText.append("上周${weekDays[startDate[java.util.Calendar.DAY_OF_WEEK] - 1]}")
                    .append(",")
            }
            when (startDate[java.util.Calendar.DAY_OF_MONTH]) {
                now[java.util.Calendar.DAY_OF_MONTH] -> {
                    timeText.delete(0, timeText.length)
                    timeText.append("今天").append(",")
                }
                now[java.util.Calendar.DAY_OF_MONTH] - 1 -> {
                    timeText.delete(0, timeText.length)
                    timeText.append("昨天").append(",")
                }
                now[java.util.Calendar.DAY_OF_MONTH] + 1 -> {
                    timeText.delete(0, timeText.length)
                    timeText.append("明天").append(",")
                }
            }
        }
        timeText.append(startDate[java.util.Calendar.MONTH] + 1).append("月")
            .append(startDate[java.util.Calendar.DAY_OF_MONTH]).append("日")
        if (startDate[java.util.Calendar.HOUR] < 10) {
            timeText.append(",").append("0${startDate[java.util.Calendar.HOUR]}")
        } else {
            timeText.append(",").append("${startDate[java.util.Calendar.HOUR]}")
        }
        if (startDate[java.util.Calendar.MINUTE] < 10) {
            timeText.append(":").append("0${startDate[java.util.Calendar.MINUTE]}")
        } else {
            timeText.append(":").append("${startDate[java.util.Calendar.HOUR]}")
        }
        if (now.time > startDate.time) {
            textColor = Color.RED
        }
        return Pair(textColor, timeText.toString())
    }

    private fun addLabel() {
        val label = Label()
        label.title = labelBinding.labelAddEdit.text.toString()
        label.color = -0x98641c
        vm.insertLabel(label)
    }

}

