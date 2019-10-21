package com.yq.view.enko

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.support.graphics.drawable.VectorDrawableCompat

fun drawableSelector(block: StateListDrawable.() -> Unit): StateListDrawable {
    return StateListDrawable().apply { block() }
}

fun colorSelector(block: ColorSelector.() -> Unit): ColorStateList {
    return ColorSelector().apply { block() }.create()
}


fun drawable(block: GradientDrawable.() -> Unit): GradientDrawable {
    return GradientDrawable().apply { block() }
}



fun Context.drawable(res: Int, block: VectorDrawableCompat.() -> Unit): VectorDrawableCompat? {
    return VectorDrawableCompat.create(resources, res, null)?.apply { block() }
}

