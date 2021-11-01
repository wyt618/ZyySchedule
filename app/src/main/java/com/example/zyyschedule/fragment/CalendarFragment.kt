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
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ConvertUtils.dp2px
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.adapter.EditScheduleLabelAdapter
import com.example.zyyschedule.adapter.LabelAdapter
import com.example.zyyschedule.adapter.ScheduleAdapter
import com.example.zyyschedule.bean.PriorityBean
import com.example.zyyschedule.bean.ScheduleTimeBean
import com.example.zyyschedule.broadcastreceiver.NotificationReceiver
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.*
import com.example.zyyschedule.dialog.PriorityDialog
import com.example.zyyschedule.dialog.RemindDialog
import com.example.zyyschedule.dialog.TimePickerDialog
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
    private val vm: CalendarViewModel by activityViewModels()
    private lateinit var binding: CalendarFragmentBinding
    private lateinit var dateJumpDialog: DialogDateBinding
    private lateinit var addScheduleBinding: AddScheduleBinding
    private lateinit var scheduleListHeadBinding: ScheduleListHeadBinding
    private lateinit var scheduleFootBinding: ScheduleFootBinding
    private lateinit var scheduleListFinishHeadBinding: ScheduleListFinishHeadBinding
    private lateinit var finishScheduleFootBinding: FinishScheduleFootBinding
    private lateinit var labelBinding: AllLabelDialogBinding
    private lateinit var labelLongClickBinding: LabelLongClickPopupWindowBinding
    private lateinit var labelItemFootBinding: LabbelItemFootBinding
    private lateinit var finishScheduleAdapter: ScheduleAdapter
    private lateinit var scheduleAdapter: ScheduleAdapter
    private var labelAdapter = LabelAdapter(R.layout.label_item)
    private var editScheduleLabelAdapter = EditScheduleLabelAdapter()

    private lateinit var labelChoose: AlertDialog
    private lateinit var addSchedule: AlertDialog
    private lateinit var builder: AlertDialog.Builder

    private val mSchedules: List<Schedule> by lazy {
        scheduleAdapter.data
    }
    private val mFinishSchedules: List<Schedule> by lazy {
        finishScheduleAdapter.data
    }
    private var selectYear: Int = 0
    private var selectMonth: Int = 0
    private var selectDay: Int = 0
    private lateinit var labelWindow: PopupWindow
    val day: MutableLiveData<String> = MutableLiveData(
        "%" + selectYear + "-" + processingTime(selectMonth) + "-" + processingTime(selectDay) + "%"
    )
    private lateinit var time: java.util.Calendar
    private var editSchedule: MutableLiveData<Schedule> = MutableLiveData()
    private var editLabel: Label = Label() //编辑界面长按后选中的label

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
        labelBinding =
            DataBindingUtil.inflate(inflater, R.layout.all_label_dialog, container, false)
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


    @SuppressLint("WrongConstant")
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
        binding.checkAll.setOnClickListener(this)
        binding.toCancelAll.setOnClickListener(this)
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
        labelBinding.labelList.layoutManager = layoutManager
        labelAdapter.setLoadFragment("CalendarFragment")
        labelAdapter.addFooterView(labelItemFootBinding.root)
        labelBinding.labelList.adapter = labelAdapter
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
        //更新优先级
        vm.priorityStyle.observe(viewLifecycleOwner) {
            addScheduleBinding.textPriority.setTextColor(it.priorityColor)
            addScheduleBinding.textPriority.text = it.priorityTitle
            addScheduleBinding.priorityId.text = it.priorityType.toString()
            addScheduleBinding.priorityButton.imageTintList = ColorStateList.valueOf(it.priorityColor)
        }

        vm.scheduleDate.observe(viewLifecycleOwner){date ->
            addScheduleBinding.textTime.text = "${processingTime(date.hour)}:${processingTime(date.minute)}"
        }

        vm.remindText.observe(viewLifecycleOwner){ remind ->
            if(remind.equals("无提醒")){
                addScheduleBinding.remindText.text = remind
            }else{
                addScheduleBinding.remindText.text = remind.substring(4)
            }
        }

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
                vm.checkAllTag.value = number
            }
        })
        finishScheduleAdapter.pitchOnNumber.observe(viewLifecycleOwner, {
            scheduleAdapter.pitchOnNumber.value?.let { ufNumber ->
                val number = it + ufNumber
                if (number > 0) {
                    enabledTrue()
                } else {
                    enabledFalse()
                }
                binding.goBackText.text = "选中${number}项"
                vm.checkAllTag.value = number
            }
        })
        //对标签数据进行监听，刷新标签选择对话框，对话框点击事件
        vm.getAllLabel().observe(viewLifecycleOwner, { labels: List<Label>? ->
            labelAdapter.setList(labels)
            if (labels != null) {
                labelAdapter.notifyItemChanged(labels.size)
            }
        })
        //全选按钮的显示
        vm.checkAllTag.observe(viewLifecycleOwner, {
            if (it != -1) {
                if (it == mSchedules.size + mFinishSchedules.size && it != 0) {
                    binding.checkAll.visibility = View.GONE
                    binding.toCancelAll.visibility = View.VISIBLE
                } else {
                    binding.checkAll.visibility = View.VISIBLE
                    binding.toCancelAll.visibility = View.GONE
                }
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

        //完成和未完成日程item长按事件
        scheduleAdapter.setOnItemLongClickListener { adapter, _, _ ->
            if (binding.drawerLayout.isDrawerOpen(Gravity.END)) {
                binding.drawerLayout.closeDrawer(Gravity.END)
            }
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
            if (binding.drawerLayout.isDrawerOpen(Gravity.END)) {
                binding.drawerLayout.closeDrawer(Gravity.END)
            }
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
                val (color, text) = vm.editTimeText(it.startTime)
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
                val (color, text) = vm.editTimeText(it.startTime)
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

        editScheduleLabelAdapter.setOnItemLongClickListener { adapter: BaseQuickAdapter<Any?, BaseViewHolder>, item: View, position: Int ->
            editLabel = adapter.data[position] as Label
            labelWindow =
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
                location[0], location[1] - item.height - 34
            )
            true
        }
        labelLongClickBinding.jumpImage.setOnClickListener(this)
        labelLongClickBinding.deleteImage.setOnClickListener(this)
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
                R.id.jump_image -> jumpLabelFragment()
                R.id.delete_image -> deleteLabelFromSchedule()
                R.id.check_all -> checkAll()
                R.id.to_cancel_all -> toCancelAll()
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
        vm.updatePriority(
            PriorityBean(
            requireContext().getString(R.string.priority_null_text),
            0,
            ContextCompat.getColor(requireContext(), R.color.priority_null)
        )
        )
        val date:java.util.Calendar = java.util.Calendar.getInstance()
        vm.label.postValue(getString(R.string.title_not_classified))
        addScheduleBinding.scheduleLabelId.text = "~0~"
        vm.remindText.postValue("无提醒")
        vm.updateScheduleDate(ScheduleTimeBean(date[java.util.Calendar.HOUR_OF_DAY],date[java.util.Calendar.MINUTE]))
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
        val timePickerDialog = TimePickerDialog()
        timePickerDialog.showNow(childFragmentManager,"timePickerDialog")
    }


    //选择优先级
    private fun gotoPriority() {
        val priorityDialog = PriorityDialog()
        priorityDialog.showNow(childFragmentManager, "priorityDialog")
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
        val remindDialog = RemindDialog()
        remindDialog.showNow(childFragmentManager,"remindDialog")
    }

    //新增日程到数据库
    private fun addSchedule() {
        val schedule = Schedule()
        val startTime =
            selectYear.toString() + "-" + processingTime(selectMonth) + "-" + processingTime(
                selectDay
            ) + " " + addScheduleBinding.textTime.text + ":00"
        schedule.startTime = startTime
        schedule.endTime = null
        schedule.remind = vm.remindText.value?.let {
            vm.remindChangeTime(
                it,
                selectYear,
                selectMonth,
                selectDay,
                addScheduleBinding.textTime.text.toString()
            )
        }
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

    private fun addLabel() {
        val label = Label()
        label.title = labelBinding.labelAddEdit.text.toString()
        label.color = -0x98641c
        vm.insertLabel(label)
    }

    private fun jumpLabelFragment() {
        LiveEventBus
            .get("SomeF_MainA", String::class.java)
            .post("visible_navigation")
        val gson = Gson()
        val json = gson.toJson(editLabel)
        val bundle = Bundle()
        bundle.putString("label", json.toString())
        val sf = ScheduleFragment()
        sf.arguments = bundle
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment, sf, null)
            .addToBackStack(null)
            .commit()
        labelWindow.dismiss()
    }

    private fun deleteLabelFromSchedule() {
        editSchedule.value?.labelId = editSchedule.value?.labelId?.replace("~${editLabel.id}~", "~")
        editSchedule.value = editSchedule.value
        //更新显示
        if (!editSchedule.value?.labelId.equals("~0~")) {
            val labelIds = editSchedule.value?.labelId?.split("~")?.dropWhile { labelId ->
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
        editScheduleLabelAdapter.notifyDataSetChanged()
        labelWindow.dismiss()
    }

    private fun checkAll() {
        binding.checkAll.visibility = View.GONE
        binding.toCancelAll.visibility = View.VISIBLE
        for (i in mSchedules.indices) {
            mSchedules[i].isEditorChecked = true
        }
        for (i in mFinishSchedules.indices) {
            mFinishSchedules[i].isEditorChecked = true
        }
        scheduleAdapter.pitchOnNumber.value = mSchedules.size
        finishScheduleAdapter.pitchOnNumber.value = mFinishSchedules.size
        scheduleAdapter.notifyDataSetChanged()
        finishScheduleAdapter.notifyDataSetChanged()
    }

    private fun toCancelAll() {
        binding.checkAll.visibility = View.VISIBLE
        binding.toCancelAll.visibility = View.GONE
        for (i in mSchedules.indices) {
            mSchedules[i].isEditorChecked = false
        }
        for (i in mFinishSchedules.indices) {
            mFinishSchedules[i].isEditorChecked = false
        }
        scheduleAdapter.pitchOnNumber.value = 0
        finishScheduleAdapter.pitchOnNumber.value = 0
        scheduleAdapter.notifyDataSetChanged()
        finishScheduleAdapter.notifyDataSetChanged()
    }
}

