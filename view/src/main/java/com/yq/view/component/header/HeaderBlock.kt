package com.yq.view.component.header

import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.view.View

interface HeaderBlock {

    fun setTextColor(@ColorInt color: Int)

    fun setText(text: CharSequence?)

    fun setText(@StringRes resid: Int)

    fun getText(): CharSequence?

    fun setVisibility(vib: Int)

    fun setOnClickListener(onClickListener: View.OnClickListener)

    var icon: Drawable?

    var type: Int

    fun setTextSize(size: Float)

    fun getTextSize(): Float

    companion object {
        const val LEFT = 0x00000000
        const val RIGHT = 0x00000001
        const val CENTER = -0x00000001
    }
}
