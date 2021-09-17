package com.example.zyyschedule.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.view.Gravity





class CustomDrawerLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : DrawerLayout(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let{
            when(it.action){
                MotionEvent.ACTION_DOWN->{
                    val x = ev.x
                    val y = ev.y
                    val touchedView = findTopChildUnder(x.toInt(), y.toInt())
                    if (touchedView != null && isContentView(touchedView)
                        && this.isDrawerOpen(GravityCompat.END)) {
                        return false
                    }
                }
                else->{}
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun  findTopChildUnder(x:Int, y:Int):View?  {
        val childCount = childCount
        for (i in childCount-1 downTo  0) {
            val child:View  = getChildAt(i)
            if (x >= child.left && x < child.right &&
                y >= child.top && y < child.bottom
            ) {
                return child
            }
        }
        return null
    }

    private fun isContentView(child: View): Boolean {
        return (child.layoutParams as LayoutParams).gravity == Gravity.NO_GRAVITY
    }


}