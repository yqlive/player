package com.yq.view.component.header.simple

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.yq.view.component.header.HeaderBlock
import com.yq.view.drawableLeft
import com.yq.view.drawableRight

class TextHeaderBlock @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr), HeaderBlock {

    override var type = HeaderBlock.CENTER

    override var icon: Drawable?
        get() = if (type == HeaderBlock.RIGHT) drawableRight else drawableLeft
        set(value) {
            if (type == HeaderBlock.RIGHT) drawableRight = value else drawableLeft = value
        }

}
