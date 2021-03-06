package com.example.zyyschedule.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.zyyschedule.R
import com.example.zyyschedule.adapter.ScheduleAdapter
import com.example.zyyschedule.broadcastreceiver.NotificationReceiver
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.*
import com.example.zyyschedule.viewmodel.ScheduleViewModel
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class TodayScheduleFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentTodayScheduleBinding
    private val vm: ScheduleViewModel by viewModels()
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var finishScheduleAdapter: ScheduleAdapter
    private  val mSchedules: List<Schedule> by lazy {
        scheduleAdapter.data
    }
    private val mFinishSchedules: List<Schedule> by lazy{
        finishScheduleAdapter.data
    }
    private lateinit var scheduleHeadBinding: ScheduleListHeadBinding
    private lateinit var scheduleFootBinding: ScheduleFootBinding
    private lateinit var scheduleListFinishHeadBinding: ScheduleListFinishHeadBinding
    private lateinit var finishScheduleFootBinding: FinishScheduleFootBinding
    private lateinit var builder: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_today_schedule, container, false)
        scheduleHeadBinding =
            DataBindingUtil.inflate(inflater, R.layout.schedule_list_head, container, false)
        scheduleFootBinding =
            DataBindingUtil.inflate(inflater, R.layout.schedule_foot, container, false)
        scheduleListFinishHeadBinding =
            DataBindingUtil.inflate(inflater, R.layout.schedule_list_finish_head, container, false)
        finishScheduleFootBinding =
            DataBindingUtil.inflate(inflater, R.layout.finish_schedule_foot, container, false)
        builder = AlertDialog.Builder(context)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.deleteButton.setOnClickListener(this)
        binding.checkAll.setOnClickListener(this)
        binding.toCancelAll.setOnClickListener(this)
        val toDayLayoutManager = LinearLayoutManager(context)
        toDayLayoutManager.orientation = LinearLayoutManager.VERTICAL
        val finishLayoutManager = LinearLayoutManager(context)
        finishLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.todayScheduleList.layoutManager = toDayLayoutManager
        binding.finishScheduleList.layoutManager = finishLayoutManager
        scheduleAdapter = ScheduleAdapter(R.layout.schedule_item)
        scheduleAdapter.setOwner(this)
        scheduleAdapter.setHeaderView(scheduleHeadBinding.root)
        scheduleAdapter.addFooterView(scheduleFootBinding.root)
        finishScheduleAdapter = ScheduleAdapter(R.layout.schedule_item)
        finishScheduleAdapter.setOwner(this)
        finishScheduleAdapter.setHeaderView(scheduleListFinishHeadBinding.root)
        finishScheduleAdapter.setFooterView(finishScheduleFootBinding.root)
        binding.todayScheduleList.adapter = scheduleAdapter
        binding.finishScheduleList.adapter = finishScheduleAdapter
        scheduleHeadBinding.scheduleListHead.setText(R.string.title_today)
        LiveEventBus.get("ScheduleF_SomeF", String::class.java)
            .observe(viewLifecycleOwner, {
                when (it) {
                    "adapterComeBack" -> {
                        for (i in mSchedules.indices) {
                            mSchedules[i].isEditor = false
                        }
                        for (i in mFinishSchedules.indices) {
                            mFinishSchedules[i].isEditor = false
                        }
                        scheduleAdapter.notifyDataSetChanged()
                        finishScheduleAdapter.notifyDataSetChanged()
                        binding.editorLayout.visibility = View.GONE
                    }
                }
            })
        //??????????????????????????????
        scheduleAdapter.pitchOnNumber.observe(viewLifecycleOwner, {
            finishScheduleAdapter.pitchOnNumber.value?.let { fNumber ->
                val number = it + fNumber
                LiveEventBus
                    .get("pitchOnNumber", Int::class.java)
                    .post(number)
                if (number > 0) {
                    enabledTrue()
                } else {
                    enabledFalse()
                }
                vm.checkAllTag.value = number
            }
        })
        finishScheduleAdapter.pitchOnNumber.observe(viewLifecycleOwner, {
            scheduleAdapter.pitchOnNumber.value?.let { ufNumber ->
                val number = it + ufNumber
                LiveEventBus
                    .get("pitchOnNumber", Int::class.java)
                    .post(number)
                if (number > 0) {
                    enabledTrue()
                } else {
                    enabledFalse()
                }
                vm.checkAllTag.value = number
            }
        })
        //?????????????????????
        vm.checkAllTag.observe(viewLifecycleOwner,{
            if(it != -1){
                if(it == mSchedules.size+mFinishSchedules.size &&  it != 0){
                    binding.checkAll.visibility = View.GONE
                    binding.toCancelAll.visibility = View.VISIBLE
                }else{
                    binding.checkAll.visibility = View.VISIBLE
                    binding.toCancelAll.visibility = View.GONE
                }
            }
        })
        //????????????????????????item????????????
        scheduleAdapter.setOnItemLongClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, _: Int ->
            binding.editorLayout.visibility = View.VISIBLE
            LiveEventBus
                .get("SomeF_ScheduleF", String::class.java)
                .post("gone_titleBar")
            LiveEventBus
                .get("SomeF_MainA", String::class.java)
                .post("gone_navigation")
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
            binding.editorLayout.visibility = View.VISIBLE
            LiveEventBus
                .get("SomeF_ScheduleF", String::class.java)
                .post("gone_titleBar")
            LiveEventBus
                .get("SomeF_MainA", String::class.java)
                .post("gone_navigation")
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
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.delete_button -> gotoDeleteDialog()
                R.id.check_all -> checkAll()
                R.id.to_cancel_all -> toCancelAll()
            }
        }
    }

    private fun updateScheduleList() {
        val calendar = Calendar.getInstance()
        val day =
            "%" + calendar[Calendar.YEAR] + "-" + processingTime(calendar[Calendar.MONTH] + 1) + "-" + processingTime(
                calendar[Calendar.DAY_OF_MONTH]
            ) + "%"
        vm.getUnfinishedScheduleOfDay(day)
            .observe(viewLifecycleOwner, { schedules: List<Schedule> ->
                for (i in schedules.indices) {
                    schedules[i].isChecked = false
                }
                if (schedules.isEmpty()) {
                    scheduleHeadBinding.root.visibility = View.GONE
                    scheduleFootBinding.root.visibility = View.GONE
                } else {
                    scheduleHeadBinding.root.visibility = View.VISIBLE
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

    private fun processingTime(time: Int): String {
        return if (time < 10) {
            "0$time"
        } else {
            time.toString()
        }
    }

    private fun gotoDeleteDialog() {
        builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.delete_schedule_message)
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
                binding.editorLayout.visibility = View.GONE
                LiveEventBus.get("SomeF_ScheduleF", String::class.java)
                    .post("visibility_titleBar")
            }
            .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
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
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                val am = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                am.cancel(sender)
            }
        }
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