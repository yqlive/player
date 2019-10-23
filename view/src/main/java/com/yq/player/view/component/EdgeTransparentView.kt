package com.yq.player.view.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import org.jetbrains.anko.dip


class EdgeTransparentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var position: Int = 0
    var drawSize: Float = dip(20).toFloat()
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val topMask = 0x01
    private val bottomMask = topMask shl 1
    private val leftMask = topMask shl 2
    private val rightMask = topMask shl 3

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    //渐变颜色
    private val mGradientColors = intArrayOf(0xFFFFFFFF.toInt(), 0x00000000)
    //渐变位置
    private val mGradientPosition = floatArrayOf(0f, 1f)

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initShader()
        mWidth = width
        mHeight = height
    }

    private fun initShader() {
        mPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            drawSize,
            mGradientColors,
            mGradientPosition,
            Shader.TileMode.CLAMP
        )
    }


    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val layerSave = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        val drawChild = super.drawChild(canvas, child, drawingTime)
        if (position == 0 || position and topMask != 0) {
            canvas.drawRect(0f, 0f, mWidth.toFloat(), drawSize, mPaint)
        }

        if (position == 0 || position and bottomMask != 0) {
            val save = canvas.save()
            canvas.rotate(180f, mWidth.toFloat() / 2f, mHeight.toFloat() / 2f)
            canvas.drawRect(0f, 0f, mWidth.toFloat(), drawSize, mPaint)
            canvas.restoreToCount(save)
        }

        val offset = (mHeight - mWidth) / 2f
        if (position == 0 || position and leftMask != 0) {
            val saveCount = canvas.save()
            canvas.rotate(90f, mWidth.toFloat() / 2f, mHeight.toFloat() / 2f)
            canvas.translate(0f, offset)
            canvas.drawRect(0 - offset, 0f, mWidth + offset, drawSize, mPaint)
            canvas.restoreToCount(saveCount)
        }

        if (position == 0 || position and rightMask != 0) {
            val saveCount = canvas.save()
            canvas.rotate(270f, mWidth.toFloat() / 2f, mHeight.toFloat() / 2f)
            canvas.translate(0f, offset)
            canvas.drawRect(0 - offset, 0f, mWidth + offset, drawSize, mPaint)
            canvas.restoreToCount(saveCount)
        }

        canvas.restoreToCount(layerSave)
        return drawChild
    }

    companion object {
        const val TOP = 0x01
        const val BOTTOM = 0x02
        const val LEFT = 0x04
        const val RIGHT = 0x08
    }
}
