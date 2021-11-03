package com.example.zyyschedule.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zyyschedule.R
import com.example.zyyschedule.adapter.LabelAdapter
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.databinding.AllLabelDialogBinding
import com.example.zyyschedule.databinding.LabbelItemFootBinding
import com.example.zyyschedule.viewmodel.CalendarViewModel


class LabelDialog : AppCompatDialogFragment(), View.OnClickListener {
    private lateinit var binding: AllLabelDialogBinding
    private val vm: CalendarViewModel by activityViewModels()
    private var labelAdapter = LabelAdapter()
    private lateinit var footBinding: LabbelItemFootBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setTitle(R.string.label_dialog_title)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        binding = DataBindingUtil.inflate(inflater, R.layout.all_label_dialog, container, false)
        binding.lifecycleOwner = this
        footBinding = DataBindingUtil.inflate(inflater, R.layout.labbel_item_foot, container, false)
        footBinding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //对标签数据进行监听，刷新标签选择对话框，对话框点击事件
        vm.getAllLabel().observe(viewLifecycleOwner, { labels: List<Label>? ->
            labelAdapter.setList(labels)
            if (labels != null) {
                labelAdapter.notifyItemChanged(labels.size)
            }
        })


        initView()
    }

    private fun initView() {
        binding.okButton.setOnClickListener(this)
        binding.cancelButton.setOnClickListener(this)
        footBinding.insertLabelText.setOnClickListener(this)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.labelList.layoutManager = layoutManager
        labelAdapter.setLoadFragment("CalendarFragment")
        labelAdapter.addFooterView(footBinding.root)
        binding.labelList.adapter = labelAdapter
        //对弹出选择标签的对话框editText进行监听
        binding.labelAddEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
            override fun afterTextChanged(text: Editable?) {
                val labelText: MutableLiveData<String> = MutableLiveData(text.toString())
                labelText.observe(viewLifecycleOwner) {
                    if (it.trim().isEmpty()) {
                        vm.getAllLabel().observe(viewLifecycleOwner) { labelList ->
                            labelAdapter.setList(labelList)
                            labelAdapter.notifyDataSetChanged()
                        }
                        footBinding.root.visibility = View.GONE
                    } else {
                        vm.checkLabelTFI(it).observe(viewLifecycleOwner) { count ->
                            if (count > 0) {
                                footBinding.root.visibility = View.GONE
                            } else {
                                footBinding.root.visibility = View.VISIBLE
                                footBinding.insertLabelText.text = "创建\"${it}\""
                            }
                        }
                        vm.fuzzyLabelTitle("%$it%").observe(viewLifecycleOwner) { labels ->
                            labelAdapter.setList(labels)
                            labelAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(600, 600)
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.cancel_button -> dialog?.dismiss()
                R.id.ok_button -> {
                    vm.updateLabelText(listOf(labelAdapter.labelTitles, labelAdapter.labelIds))
                    dialog?.dismiss()
                }
                R.id.insert_label_text -> addLabel()
                else -> {
                }
            }
        }
    }

    private fun addLabel() {
        val label = Label()
        label.title = binding.labelAddEdit.text.toString()
        label.color = -0x98641c
        vm.insertLabel(label)
    }

}