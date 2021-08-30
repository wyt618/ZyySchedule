package com.example.zyyschedule.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.zyyschedule.R
import com.example.zyyschedule.adapter.ScheduleAdapter
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.databinding.*
import com.example.zyyschedule.viewmodel.ScheduleViewModel

class LabelFragment(labelId: Int):Fragment() {
    private val mLabelId = labelId
    private lateinit var binding: FragmentLabelBinding
    private val vm: ScheduleViewModel by viewModels()
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var finishScheduleAdapter: ScheduleAdapter
    private lateinit var mSchedules: List<Schedule>
    private lateinit var mFinishSchedules: List<Schedule>
    private lateinit var scheduleHeadBinding: ScheduleListHeadBinding
    private lateinit var scheduleFootBinding: ScheduleFootBinding
    private lateinit var scheduleListFinishHeadBinding: ScheduleListFinishHeadBinding
    private lateinit var finishScheduleFootBinding: FinishScheduleFootBinding
    private lateinit var builder: AlertDialog.Builder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_label, container, false)
        scheduleHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_head, container, false)
        scheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_foot, container, false)
        scheduleListFinishHeadBinding = DataBindingUtil.inflate(inflater, R.layout.schedule_list_finish_head, container, false)
        finishScheduleFootBinding = DataBindingUtil.inflate(inflater, R.layout.finish_schedule_foot, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        builder = AlertDialog.Builder(context)
        val labelSchedule = LinearLayoutManager(context)
        labelSchedule.orientation = LinearLayoutManager.VERTICAL
        val finishSchedule = LinearLayoutManager(context)
        finishSchedule.orientation = LinearLayoutManager.VERTICAL
        binding.labelScheduleList.layoutManager = labelSchedule
        binding.finishScheduleList.layoutManager = finishSchedule
        scheduleAdapter = ScheduleAdapter(R.layout.schedule_item_local)
        scheduleAdapter.setOwner(this)
        scheduleAdapter.setHeaderView(scheduleHeadBinding.root)
        scheduleAdapter.addFooterView(scheduleFootBinding.root)
        finishScheduleAdapter = ScheduleAdapter(R.layout.schedule_item_local)
        finishScheduleAdapter.setOwner(this)
        finishScheduleAdapter.setHeaderView(scheduleListFinishHeadBinding.root)
        finishScheduleAdapter.setFooterView(finishScheduleFootBinding.root)
        binding.labelScheduleList.adapter = scheduleAdapter
        binding.finishScheduleList.adapter = finishScheduleAdapter
        scheduleHeadBinding.scheduleListHead.setText(R.string.local_schedule_head)
        scheduleHeadBinding.scheduleDeleteBack.setOnClickListener {
            scheduleHeadBinding.deleteSchedule.visibility = View.GONE
            scheduleHeadBinding.scheduleListHead.visibility = View.VISIBLE
            scheduleHeadBinding.scheduleDeleteBack.visibility = View.GONE
            scheduleListFinishHeadBinding.deleteSchedule.visibility = View.GONE
            scheduleListFinishHeadBinding.scheduleListFinish.visibility = View.VISIBLE
            scheduleListFinishHeadBinding.scheduleDeleteBack.visibility = View.GONE
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
            scheduleHeadBinding.deleteSchedule.visibility = View.GONE
            scheduleHeadBinding.scheduleListHead.visibility = View.VISIBLE
            scheduleHeadBinding.scheduleDeleteBack.visibility = View.GONE
            for (i in mSchedules.indices) {
                mSchedules[i].isEditor = false
            }
            for (i in mFinishSchedules.indices) {
                mFinishSchedules[i].isEditor = false
            }
            scheduleAdapter.notifyDataSetChanged()
            finishScheduleAdapter.notifyDataSetChanged()
        }
        scheduleHeadBinding.deleteSchedule.setOnClickListener { gotoDeleteDialog() }
        scheduleListFinishHeadBinding.deleteSchedule.setOnClickListener { gotoDeleteDialog() }
        //完成和未完成日程item长按事件

        scheduleAdapter.setOnItemLongClickListener { adapter: BaseQuickAdapter<*, *>, _: View?, _: Int ->
            scheduleHeadBinding.deleteSchedule.visibility = View.VISIBLE
            scheduleHeadBinding.scheduleListHead.visibility = View.GONE
            scheduleHeadBinding.scheduleDeleteBack.visibility = View.VISIBLE
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
            scheduleHeadBinding.deleteSchedule.visibility = View.VISIBLE
            scheduleHeadBinding.scheduleListHead.visibility = View.GONE
            scheduleHeadBinding.scheduleDeleteBack.visibility = View.VISIBLE
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
    }

    private fun gotoDeleteDialog() {
        builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.delete_schedule_message)
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
                    scheduleListFinishHeadBinding.deleteSchedule.visibility = View.GONE
                    scheduleListFinishHeadBinding.scheduleListFinish.visibility = View.VISIBLE
                    scheduleListFinishHeadBinding.scheduleDeleteBack.visibility = View.GONE
                    scheduleHeadBinding.deleteSchedule.visibility = View.GONE
                    scheduleHeadBinding.scheduleListHead.visibility = View.VISIBLE
                    scheduleHeadBinding.scheduleDeleteBack.visibility = View.GONE
                }
                .setNeutralButton(R.string.dialog_button_cancel) { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun updateScheduleList() {
        vm.getUFScheduleOfLabel(mLabelId)!!.observe(viewLifecycleOwner, { schedules: List<Schedule> ->
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
            mSchedules = scheduleAdapter.data
        })
        vm.getFScheduleOfLabel(mLabelId)!!.observe(viewLifecycleOwner, { schedules: List<Schedule> ->
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
}