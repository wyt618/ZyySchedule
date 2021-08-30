package com.example.zyyschedule.fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.zyyschedule.databinding.CalendarFragmentBinding
import com.example.zyyschedule.viewmodel.CalendarViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView

class CalendarFragmentk:Fragment(), View.OnClickListener, CalendarView.OnCalendarSelectListener {
    private lateinit var  binding: CalendarFragmentBinding
    private val vm:CalendarViewModel by viewModels()

    override fun onClick(v: View?) {

    }

    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {

    }


    override fun onCalendarOutOfRange(calendar: Calendar?) {

    }

}