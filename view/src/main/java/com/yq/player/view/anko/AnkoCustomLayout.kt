package com.yq.player.view.anko

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayout

@Suppress("ClassName")
open class _FlexboxLayout(ctx: Context) : FlexboxLayout(ctx) {

    inline fun <T : View> T.lparams(
        source: FlexboxLayout.LayoutParams?,
        init: FlexboxLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = FlexboxLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
        source: FlexboxLayout.LayoutParams?
    ): T {
        val layoutParams = FlexboxLayout.LayoutParams(source!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        c: Context?,
        attrs: AttributeSet?,
        init: FlexboxLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = FlexboxLayout.LayoutParams(c!!, attrs!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
        c: Context?,
        attrs: AttributeSet?
    ): T {
        val layoutParams = FlexboxLayout.LayoutParams(c!!, attrs!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        init: FlexboxLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = FlexboxLayout.LayoutParams(width, height)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
        width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
    ): T {
        val layoutParams = FlexboxLayout.LayoutParams(width, height)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        source: ViewGroup.LayoutParams?,
        init: FlexboxLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = FlexboxLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    fun <T : View> T.lparams(
        source: ViewGroup.LayoutParams?
    ): T {
        val layoutParams = FlexboxLayout.LayoutParams(source!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

}
