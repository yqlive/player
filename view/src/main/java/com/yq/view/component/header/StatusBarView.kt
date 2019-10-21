package com.yq.view.component.header

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

class StatusBarView : View {
    @RequiresApi(api = 21)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun init(activity: Activity) {
        this.init(getStatusBarHeight(activity))
    }

    fun init(height: Int) {
        val layoutParams = this.layoutParams
        layoutParams.height = height
        this.layoutParams = layoutParams
        this.requestLayout()
    }

    companion object {

        fun getStatusBarHeight(context: Context): Int {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            return context.resources.getDimensionPixelSize(resourceId)
        }

        @SuppressLint("ResourceType")
        fun setPermeateStyle(activity: Activity) {
            val mContentView = activity.findViewById<View>(16908290) as ViewGroup
            setPermeateStyle(activity, mContentView.getChildAt(0))
        }

        fun setPermeateStyle(activity: Activity, view: View?) {
            val window = activity.window
            window.addFlags(67108864)
            if (view != null) {
                ViewCompat.setFitsSystemWindows(view, false)
            }

        }
    }

}