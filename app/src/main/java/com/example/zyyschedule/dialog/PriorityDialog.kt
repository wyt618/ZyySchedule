package com.example.zyyschedule.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.zyyschedule.bean.PriorityBean
import com.example.zyyschedule.R
import com.example.zyyschedule.adapter.PriorityListAdapter
import com.example.zyyschedule.databinding.PriorityDialogBinding
import com.example.zyyschedule.viewmodel.CalendarViewModel


class PriorityDialog : AppCompatDialogFragment() {
    private lateinit var binding: PriorityDialogBinding
    private val priorityListAdapter = PriorityListAdapter()
    private val vm: CalendarViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setTitle(R.string.priority_dialog_title)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        binding = DataBindingUtil.inflate(inflater, R.layout.priority_dialog, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.priorityList.layoutManager = layoutManager
        binding.priorityList.adapter = priorityListAdapter
        priorityListAdapter.setList(vm.priorityListData(requireContext()))
        priorityListAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>, item: View, position: Int ->
            val text = item.findViewById<TextView>(R.id.priority_title)
            //          lis?.invoke(text)
            vm.updatePriority(
                PriorityBean(
                    text.text.toString(),
                    position,
                    text.textColors.defaultColor
                )
            )
            dismiss()
        }
    }

    override fun onResume() {
        dialog?.window?.setLayout(600, 350)
        super.onResume()
    }


//    private var lis:((String,)->Unit)? = null
//
//    fun setClickListener(lis:((String,)->Unit)?){
//        this.lis = lis
//    }


}