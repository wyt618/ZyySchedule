package com.example.zyyschedule.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.zyyschedule.R
import com.example.zyyschedule.bean.ScheduleTimeBean
import com.example.zyyschedule.databinding.TimepickerDialogBinding
import com.example.zyyschedule.viewmodel.CalendarViewModel

class TimePickerDialog : AppCompatDialogFragment() {
   private lateinit var  binding:TimepickerDialogBinding
   private val vm:CalendarViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setTitle(R.string.add_schedule_timePicker)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        binding = DataBindingUtil.inflate(inflater, R.layout.timepicker_dialog,container,false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onResume() {
        dialog?.window?.setLayout(600,600)
        super.onResume()
    }

    fun initView(){
        binding.hourPicker.maxValue = 23
        binding.minePicker.maxValue = 59
        binding.hourPicker.minValue = 0
        binding.minePicker.minValue = 0
        vm.scheduleDate.value?.let { date ->
            binding.hourPicker.value = date.hour
            binding.minePicker.value = date.minute
        }

        binding.cancelButton.setOnClickListener{
            dialog?.dismiss()
        }

        binding.okButton.setOnClickListener {
            vm.updateScheduleDate(ScheduleTimeBean(binding.hourPicker.value,binding.minePicker.value))
            dialog?.dismiss()
        }
    }


}