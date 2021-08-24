package com.example.zyyschedule.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.zyyschedule.databinding.SettingFragmentBinding
import com.example.zyyschedule.viewmodel.SettingViewModel

open class SettingFragment : Fragment() {
    private val vm: SettingViewModel by viewModels()
    private lateinit var binding:SettingFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SettingFragmentBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}