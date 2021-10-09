package com.example.zyyschedule.adapter

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.zyyschedule.database.Schedule
import com.example.zyyschedule.viewmodel.CalendarViewModel
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class ItemTouchCallback(adapter: ScheduleAdapter, data: List<Schedule>,owner: ViewModelStoreOwner) :
    ItemTouchHelper.Callback() {
    private var mAdapter: ScheduleAdapter = adapter
    private var mData: List<Schedule> = data
    private var vm : CalendarViewModel = ViewModelProvider(owner).get(CalendarViewModel::class.java)

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags: Int = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition: Int = viewHolder.adapterPosition - 1
        val toPosition: Int = if (target.adapterPosition > 0 && target.adapterPosition<mData.size) {
            target.adapterPosition - 1
        }else if(target.adapterPosition<=0){
            target.adapterPosition
        }else{
            mData.size-1
        }
        return if(fromPosition != -1 && fromPosition != mAdapter.data.size) {
            Collections.swap(mData, fromPosition, toPosition)
            mAdapter.notifyItemMoved(fromPosition, toPosition)
            mAdapter.notifyDataSetChanged()//没有动画效果，但适用于item有点击事件的话
            true //true:可以滑动
        }else{
            false
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position: Int = viewHolder.adapterPosition-1
        if(position != -1 && position != mAdapter.data.size ) {
            if (mData[position].state.equals("0")) {
                mData[position].state = "1"
            } else {
                mData[position].state = "0"
            }
            vm.changeStateSchedule(mData[position])
            mAdapter.notifyDataSetChanged()//没有动画效果，但适用于item有点击事件的话
        }else{
            mAdapter.notifyDataSetChanged()
        }
    }




}