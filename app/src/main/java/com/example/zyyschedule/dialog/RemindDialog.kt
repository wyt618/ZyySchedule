package com.example.zyyschedule.dialog

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.NotificationUtils
import com.example.zyyschedule.R
import com.example.zyyschedule.adapter.RemindAdapter
import com.example.zyyschedule.databinding.RemindDialogBinding
import com.example.zyyschedule.databinding.RemindListHeadBinding
import com.example.zyyschedule.viewmodel.CalendarViewModel

class RemindDialog : AppCompatDialogFragment() {
    private lateinit var binding: RemindDialogBinding
    private lateinit var headBinding: RemindListHeadBinding
    private val remindAdapter = RemindAdapter()
    private val vm: CalendarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setTitle(R.string.remind_dialog_title)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        binding = DataBindingUtil.inflate(inflater, R.layout.remind_dialog, container, false)
        binding.lifecycleOwner = this
        headBinding = DataBindingUtil.inflate(inflater, R.layout.remind_list_head, container, false)
        headBinding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val remindLayoutManager = LinearLayoutManager(context)
        remindLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.remindChooseList.layoutManager = remindLayoutManager
        binding.remindChooseList.adapter = remindAdapter
        remindAdapter.setList(vm.remindListData())
        remindAdapter.setHeaderView(headBinding.root)
        remindAdapter.setCheckAll(headBinding)
        headBinding.remindHeadBox.isClickable = false
        headBinding.remindHeadBox.isChecked = true
        headBinding.remindHeadBox.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                headBinding.remindHeadBox.isClickable = false
                remindAdapter.addRemind = StringBuffer("?????????")
                for (i in remindAdapter.data.indices) {
                    remindAdapter.data[i].remindIsChecked = false
                    remindAdapter.notifyItemChanged(i + 1)
                }
            } else {
                headBinding.remindHeadBox.isClickable = true
            }
        }

        binding.cancelButton.setOnClickListener {
            dialog?.dismiss()
        }

        binding.okButton.setOnClickListener {
            vm.remindText.postValue(remindAdapter.addRemind.toString())
            dialog?.dismiss()
        }

    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(500, 700)
    }

}
