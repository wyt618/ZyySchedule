package com.example.zyyschedule.bean

data class RemindBean(
        var remindTitle: String = "",
        var remindType: Int = 0,
        var remindIsChecked: Boolean = false
)