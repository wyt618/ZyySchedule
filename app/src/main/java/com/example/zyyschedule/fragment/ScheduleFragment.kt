package com.example.zyyschedule.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.example.zyyschedule.R
import com.example.zyyschedule.activity.AddLabelActivity
import com.example.zyyschedule.adapter.LabelAdapter
import com.example.zyyschedule.database.Label
import com.example.zyyschedule.databinding.ScheduleFragmentBinding
import com.example.zyyschedule.viewmodel.ScheduleViewModel
import com.jeremyliao.liveeventbus.LiveEventBus

@SuppressLint("NotifyDataSetChanged")
class ScheduleFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: ScheduleFragmentBinding
    private val vm: ScheduleViewModel by viewModels()
    private val labelAdapter: LabelAdapter = LabelAdapter(R.layout.label_item)
    private lateinit var labelItemEditorButton: View

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.schedule_fragment, container, false)
        labelItemEditorButton = inflater.inflate(R.layout.label_item_editor_button, null)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("WrongConstant", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gotoTodayScheduleFragment()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.labelRecyclerview.layoutManager = layoutManager
        labelAdapter.setLoadFragment("ScheduleFragment")
        binding.labelRecyclerview.adapter = labelAdapter
        binding.ivMainMenu.setOnClickListener(this)
        binding.drawerLayout.setOnClickListener(this)
        binding.goBack.setOnClickListener(this)
        LiveEventBus.get("SomeF_ScheduleF", String::class.java)
            .observe(this, { s: String ->
                when (s) {
                    "gone_titleBar" -> {
                        binding.scheduleTitleBar.visibility = View.GONE
                        binding.editTool.visibility = View.VISIBLE
                    }
                    "visibility_titleBar" -> {
                        exitEditor()
                    }

                }
            })
        LiveEventBus
            .get("pitchOnNumber", Int::class.java)
            .observe(viewLifecycleOwner, {
                binding.goBackText.text = "选中${it}项"
                if (it > 0) {
                    LiveEventBus
                        .get("SomeF_MainA", String::class.java)
                        .post("enabled_true")
                } else {
                    LiveEventBus
                        .get("SomeF_MainA", String::class.java)
                        .post("enabled_false")
                }
            })
        // 侧滑菜单点击事件
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.today -> gotoTodayScheduleFragment()
                R.id.inbox -> gotoLocalFragment()
                R.id.dates -> gotoLabelFragment()
                R.id.add_list -> gotoAddLabelActivity()
            }
            true
        }
        vm.getAllLabel().observe(viewLifecycleOwner, { labels: List<Label>? ->
            labelAdapter.setList(labels)
            labelAdapter.notifyDataSetChanged()
        })


        //item点击事件
        labelAdapter.setOnItemClickListener { _: BaseQuickAdapter<*, *>?, itemView: View, _: Int ->
            val labelName = itemView.findViewById<TextView>(R.id.label_name)
            val labelId = itemView.findViewById<TextView>(R.id.label_id)
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.setTransition(FragmentTransaction.TRANSIT_NONE)
            ft.replace(R.id.scheduleFragment, LabelFragment(labelId.text.toString()), null)
                .commit()
            binding.scheduleTitleBarTitle.text = labelName.text
            binding.drawerLayout.closeDrawer(Gravity.START)
        }

        //item长按事件
        labelAdapter.setOnItemLongClickListener { adapter: BaseQuickAdapter<*, *>, itemView: View, pos: Int ->
            if (labelItemEditorButton.parent != null) {
                val vg = labelItemEditorButton.parent as ViewGroup
                vg.removeView(labelItemEditorButton)
            }
            val builder = AlertDialog.Builder(requireActivity())
            builder.setView(labelItemEditorButton)
            val deleteDialog = builder.create()
            deleteDialog.window!!.setBackgroundDrawableResource(R.color.transparent)
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
            deleteDialog.show()
            deleteDialog.window!!.setLayout(66, 66)
            val params = deleteDialog.window!!.attributes
            val d = requireContext().resources!!.displayMetrics
            params.x = itemView.width - 50
            params.y =
                -d.heightPixels / 2 + binding.labelRecyclerview.top + itemView.height + itemView.top - 10
            deleteDialog.window!!.attributes = params
            deleteDialog.window!!.setGravity(Gravity.START)
            labelItemEditorButton.findViewById<View>(R.id.delete_button).setOnClickListener {
                deleteDialog.dismiss()
                val builder1 = AlertDialog.Builder(requireActivity())
                builder1.setTitle("删除标签")
                builder1.setMessage("您标签内的所有日程将被删除。")
                builder1.setPositiveButton(
                    "删除"
                ) { _: DialogInterface?, _: Int ->
                    val labels = adapter.data as List<*>
                    vm.deleteLabel(labels[pos] as Label)
                    adapter.notifyDataSetChanged()
                }
                    .setNegativeButton("取消") { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                val dialog = builder1.create()
                dialog.window!!.setBackgroundDrawableResource(R.drawable.delete_dialog)
                dialog.show()
                val d1 = requireContext().resources!!.displayMetrics
                val p = dialog.window!!.attributes
                p.width = d1.widthPixels / 3
                p.height = d1.heightPixels / 5
                dialog.window!!.attributes = p
            }
            deleteDialog.setOnDismissListener { binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED) }
            false
        }


    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.ivMainMenu -> binding.drawerLayout.openDrawer(Gravity.START)
                R.id.go_back -> exitEditor()
            }
        }
    }

    private fun exitEditor() {
        binding.editTool.visibility = View.GONE
        binding.scheduleTitleBar.visibility = View.VISIBLE
        LiveEventBus.get("ScheduleF_SomeF", String::class.java)
            .post("adapterComeBack")
        LiveEventBus.get("SomeF_MainA", String::class.java)
            .post("visible_navigation")

    }


    @SuppressLint("WrongConstant")
    private fun gotoTodayScheduleFragment() {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_NONE)
        ft.replace(R.id.scheduleFragment, TodayScheduleFragment(), null)
            .commit()
        binding.scheduleTitleBarTitle.setText(R.string.title_today)
        binding.drawerLayout.closeDrawer(Gravity.START)
    }

    @SuppressLint("WrongConstant")
    private fun gotoLocalFragment() {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_NONE)
        ft.replace(R.id.scheduleFragment, LocalFragment(), null)
            .commit()
        binding.scheduleTitleBarTitle.setText(R.string.title_local_schedule)
        binding.drawerLayout.closeDrawer(Gravity.START)
    }

    @SuppressLint("WrongConstant")
    private fun gotoLabelFragment() {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_NONE)
        ft.replace(R.id.scheduleFragment, LabelFragment("0"), null)
            .commit()
        binding.scheduleTitleBarTitle.setText(R.string.title_not_classified)
        binding.drawerLayout.closeDrawer(Gravity.START)
    }

    private fun gotoAddLabelActivity() {
        val intent = Intent(activity, AddLabelActivity::class.java)
        startActivity(intent)
    }
}