package com.example.zyyschedule.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
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
import com.blankj.utilcode.util.NotificationUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemSwipeListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.zyyschedule.R
import com.example.zyyschedule.adapter.EditScheduleLabelAdapter
import com.example.zyyschedule.adapter.ScheduleAdapter
import com.example.zyyschedule.broadcastreceiver.NotificationReceiver
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.*
import com.example.zyyschedule.dialog.AddScheduleDialog
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
    private lateinit var scheduleListHeadBinding: ScheduleListHeadBinding
    private lateinit var scheduleFootBinding: ScheduleFootBinding
    private lateinit var scheduleListFinishHeadBinding: ScheduleListFinishHeadBinding
    private lateinit var finishScheduleFootBinding: FinishScheduleFootBinding
    private lateinit var labelLongClickBinding: LabelLongClickPopupWindowBinding
    private lateinit var finishScheduleAdapter: ScheduleAdapter
    private lateinit var scheduleAdapter: ScheduleAdapter
    private var editScheduleLabelAdapter = EditScheduleLabelAdapter()
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
    private var editLabel: Label = Label() //??????????????????????????????label

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        builder = AlertDialog.Builder(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.calendar_fragment, container, false)
        dateJumpDialog = DataBindingUtil.inflate(inflater, R.layout.dialog_date, container, false)
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
        return binding.root
    }


    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        time = java.util.Calendar.getInstance()
        selectYear = binding.calendarView.curYear
        selectMonth = binding.calendarView.curMonth
        selectDay = binding.calendarView.curDay
        binding.flCurrent.setOnClickListener(this)
        binding.tvYear.text = binding.calendarView.curYear.toString()
        binding.tvMonthDay.text =
            binding.calendarView.curMonth.toString() + "???" + binding.calendarView.curDay + "???"
        binding.tvLunar.text = "??????"
        binding.calendarView.setOnCalendarSelectListener(this)
        binding.fabBtn.setOnClickListener(this)
        binding.goBack.setOnClickListener(this)
        binding.deleteButton.setOnClickListener(this)
        binding.checkAll.setOnClickListener(this)
        binding.toCancelAll.setOnClickListener(this)
        binding.vm = vm
        binding.lifecycleOwner = this
        scheduleListHeadBinding.scheduleListHead.text =
            selectMonth.toString() + "???" + selectDay + "???"
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
        //?????????????????????????????????
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

        // ?????????????????????
        LiveEventBus.get("AddScheduleF_CalendarF", String::class.java)
            .observe(this,{
                if(it == "check_Notification"){
                    getNotification()
                }
            })

        //??????adapter
        binding.scheduleList.adapter = scheduleAdapter
        binding.finishScheduleList.adapter = finishScheduleAdapter
        scheduleListHeadBinding.scheduleListHead.text =
            selectMonth.toString() + "???" + selectDay + "???"
        enabledFalse()
        //??????????????????????????????
        scheduleAdapter.pitchOnNumber.observe(viewLifecycleOwner, {
            finishScheduleAdapter.pitchOnNumber.value?.let { fNumber ->
                val number = it + fNumber
                if (number > 0) {
                    enabledTrue()
                } else {
                    enabledFalse()
                }
                binding.goBackText.text = "??????${number}???"
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
                binding.goBackText.text = "??????${number}???"
                vm.checkAllTag.value = number
            }
        })

        //?????????????????????
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


        //????????????????????????
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
                        dateJumpDialog.datePicker.year.toString() + "???" + (dateJumpDialog.datePicker.month + 1) + "???" + dateJumpDialog.datePicker.dayOfMonth + "???",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
            builder.create().show()
            true
        }

        //????????????????????????item????????????
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


        //????????????????????????item????????????
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
                //?????????????????????
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
                    1 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_low)
                    2 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_middle)
                    3 -> imageColor =
                        ContextCompat.getColor(requireContext(), R.color.priority_high)
                }
                binding.editFlag.imageTintList = ColorStateList.valueOf(imageColor)
                //?????????????????????
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
        //?????????????????????????????????
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
                R.id.go_back -> exitEditor()
                R.id.delete_button -> gotoDeleteDialog()
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

    //?????????????????????
    @SuppressLint("SetTextI18n")
    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
        selectYear = calendar!!.year
        selectMonth = calendar.month
        selectDay = calendar.day
        binding.tvLunar.visibility = View.VISIBLE
        binding.tvYear.visibility = View.VISIBLE
        binding.tvMonthDay.text = calendar.month.toString() + "???" + calendar.day + "???"
        binding.tvYear.text = calendar.year.toString()
        binding.tvLunar.text = calendar.lunar
        scheduleListHeadBinding.scheduleListHead.text =
            selectMonth.toString() + "???" + selectDay + "???"
        scheduleListHeadBinding.scheduleListHead.visibility = View.VISIBLE
        updateScheduleList()
        setCalendarTag()
        exitEditor()
    }

    //???????????????????????????
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gotoAddSchedule() {
        binding.fabBtn.visibility = View.GONE
        val addScheduleDialog = AddScheduleDialog(selectYear, selectMonth, selectDay)
        addScheduleDialog.showNow(childFragmentManager, "addSchedule")
        addScheduleDialog.dialog?.setOnDismissListener {
            binding.fabBtn.visibility = View.VISIBLE
        }
    }


    //????????????????????????
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

    //??????????????????????????????
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

    //?????????????????????
    private fun getSchemeCalendar(year: Int, month: Int, day: Int): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = 0xFF0000//???????????????????????????????????????????????????
        calendar.addScheme(Scheme())
        calendar.addScheme(-0xff7800, "???")
        calendar.addScheme(-0xff7800, "???")
        return calendar
    }

    //??????????????????
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

    //?????????????????? ??????6?????????06???7???????????????07
    private fun processingTime(time: Int): String {
        return if (time < 10) {
            "0$time"
        } else {
            time.toString()
        }
    }


    //?????????????????????????????????
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

    //?????????????????????????????????
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


    //??????????????????????????????
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
        //????????????
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

    //??????????????????????????????
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
}

