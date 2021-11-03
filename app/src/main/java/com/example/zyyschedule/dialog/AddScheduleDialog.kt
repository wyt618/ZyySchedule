package com.example.zyyschedule.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.zyyschedule.R
import com.example.zyyschedule.bean.ScheduleTimeBean
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.AddScheduleBinding
import com.example.zyyschedule.viewmodel.CalendarViewModel
import com.jeremyliao.liveeventbus.LiveEventBus
import java.text.SimpleDateFormat
import java.util.*

class AddScheduleDialog(selectYear: Int, selectMonth: Int, selectDay: Int) : DialogFragment(),
    View.OnClickListener {
    private val curYear: Int = selectYear
    private val curMonth: Int = selectMonth
    private val curDay: Int = selectDay
    private lateinit var binding: AddScheduleBinding
    private val vm: CalendarViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_schedule, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        dialog?.window?.let {
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
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setOnClick()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {

        binding.editText.text = null
        binding.editText.requestFocus()
        val imm: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.editText.requestFocus()
        binding.editText.post {
            imm.showSoftInput(binding.editText, InputMethodManager.SHOW_IMPLICIT)
        }
        dialog?.setOnDismissListener {
            binding.editText.post {
                imm.hideSoftInputFromWindow(
                    binding.editText.windowToken,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
            }
        }
        val date = Calendar.getInstance()
        vm.remindText.postValue("无提醒")
        vm.updateLabelText(listOf("无标签", "~0~"))
        vm.updateScheduleDate(ScheduleTimeBean(date[Calendar.HOUR_OF_DAY], date[Calendar.MINUTE]))
        //更新优先级
        vm.priorityStyle.observe(viewLifecycleOwner) {
            binding.textPriority.setTextColor(it.priorityColor)
            binding.priorityId.text = it.priorityType.toString()
            binding.priorityButton.imageTintList = ColorStateList.valueOf(it.priorityColor)
        }
        //更新时间
        vm.scheduleDate.observe(viewLifecycleOwner) { time ->
            binding.textTime.text = "${processingTime(time.hour)}:${processingTime(time.minute)}"
        }
        //更新提醒
        vm.remindText.observe(viewLifecycleOwner) { remind ->
            if (remind.equals("无提醒")) {
                binding.remindText.text = remind
            } else {
                binding.remindText.text = remind.substring(4)
            }
        }
        //更新标签
        vm.labelText.observe(viewLifecycleOwner) {
            if (it[0] == "无标签") {
                binding.scheduleLabel.text = it[0]
                binding.scheduleLabelId.text = it[1]
            } else {
                binding.scheduleLabel.text = it[0].replace("无标签,", "")
                binding.scheduleLabelId.text = it[1]
            }
        }

        //设置新增日程对话框有内容时唤醒按钮
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it == ' ' }.isNotEmpty()) {
                    binding.sendSchedule.setImageResource(R.drawable.ic_baseline_send_click_24)
                    binding.sendSchedule.isClickable = true

                } else {
                    binding.sendSchedule.setImageResource(R.drawable.ic_baseline_send_24)
                    binding.sendSchedule.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setOnClick() {
        binding.addScheduleSelectTime.setOnClickListener(this)
        binding.textTime.setOnClickListener(this)
        binding.priorityButton.setOnClickListener(this)
        binding.textPriority.setOnClickListener(this)
        binding.labelButton.setOnClickListener(this)
        binding.scheduleLabel.setOnClickListener(this)
        binding.remindButton.setOnClickListener(this)
        binding.remindText.setOnClickListener(this)
        binding.sendSchedule.setOnClickListener(this)
    }

    //处理日期数字 例如6月转为06，7分钟转化为07
    private fun processingTime(time: Int): String {
        return if (time < 10) {
            "0$time"
        } else {
            time.toString()
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.add_schedule_selectTime, R.id.textTime -> gotoGetTime()
                R.id.priority_button, R.id.text_priority -> gotoPriority()
                R.id.label_button, R.id.schedule_label -> gotoChooseLabel()
                R.id.remind_button, R.id.remind_text -> gotoAddRemind()
                R.id.send_schedule -> addSchedule()
            }
        }
    }

    //选择时间对话框
    private fun gotoGetTime() {
        val timePickerDialog = TimePickerDialog()
        timePickerDialog.showNow(childFragmentManager, "timePickerDialog")
    }


    //选择优先级
    private fun gotoPriority() {
        val priorityDialog = PriorityDialog()
        priorityDialog.showNow(childFragmentManager, "priorityDialog")
    }

    //选择标签对话框
    private fun gotoChooseLabel() {
        val labelDialog = LabelDialog()
        labelDialog.showNow(childFragmentManager, "labelDialog")
    }

    //选择提醒
    private fun gotoAddRemind() {
        val remindDialog = RemindDialog()
        remindDialog.showNow(childFragmentManager, "remindDialog")
    }

    //新增日程到数据库
    private fun addSchedule() {
        val schedule = Schedule()
        val startTime =
            curYear.toString() + "-" + processingTime(curMonth) + "-" + processingTime(curDay) + " " + binding.textTime.text + ":00"
        schedule.startTime = startTime
        schedule.endTime = null
        schedule.remind = vm.remindText.value?.let {
            vm.remindChangeTime(
                it,
                curYear,
                curMonth,
                curDay,
                binding.textTime.text.toString()
            )
        }
        schedule.title = binding.editText.text.toString()
        schedule.detailed = null
        schedule.state = "0"
        schedule.priority = binding.priorityId.text.toString().toInt()
        if (binding.scheduleLabelId.text.toString().trim().isEmpty()) {
            schedule.labelId = "0"
        } else {
            schedule.labelId = binding.scheduleLabelId.text.toString().trim()
        }
        vm.insertSchedule(schedule)
        dialog?.dismiss()
        if (schedule.remind!!.isNotEmpty()) {
            val remindCheck: Int = checkRemindTime(schedule.remind!!)
            if (remindCheck > 0) {
                Toast.makeText(context, "抱歉，有" + remindCheck + "条提醒因为超出当前时间无效", Toast.LENGTH_LONG)
                    .show()
            }
        }
        if(binding.remindText.text != "无提醒") {
            LiveEventBus
                .get("AddScheduleF_CalendarF", String::class.java)
                .post("check_Notification")
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

}