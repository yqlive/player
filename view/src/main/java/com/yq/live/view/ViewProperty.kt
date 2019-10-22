package com.yq.live.view

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import org.jetbrains.anko.internals.AnkoInternals.noGetter

/**-----------------------------------Property---------------------------------------*/
const val parentId = ConstraintSet.PARENT_ID
const val horizontal = LinearLayout.HORIZONTAL
const val vertical = LinearLayout.VERTICAL

val matrix = ImageView.ScaleType.MATRIX
val fitXy = ImageView.ScaleType.FIT_XY
val fitStart = ImageView.ScaleType.FIT_START
val fitCenter = ImageView.ScaleType.FIT_CENTER
val fitEnd = ImageView.ScaleType.FIT_END
val center = ImageView.ScaleType.CENTER
val centerCrop = ImageView.ScaleType.CENTER_CROP
val centerInside = ImageView.ScaleType.CENTER_INSIDE

var ConstraintLayout.LayoutParams.centerOf: Int
    get() = startToStart
    set(value) {
        verticalCenterOf = value
        horizontalCenterOf = value
    }

var ConstraintLayout.LayoutParams.verticalCenterOf: Int
    get() = topToTop
    set(value) {
        topToTop = value
        bottomToBottom = value
    }
var ConstraintLayout.LayoutParams.horizontalCenterOf: Int
    get() = startToStart
    set(value) {
        startToStart = value
        endToEnd = value
    }


var ConstraintLayout.LayoutParams.topOf: Int
    get() = startToStart
    set(value) {
        startToStart = value
        endToEnd = value
        topToTop = value
    }


var ConstraintLayout.LayoutParams.bottomOf: Int
    get() = startToStart
    set(value) {
        startToStart = value
        endToEnd = value
        bottomToBottom = value
    }

var ConstraintLayout.LayoutParams.startOf: Int
    get() = startToStart
    set(value) {
        startToStart = value
        topToTop = value
        bottomToBottom = value
    }

var ConstraintLayout.LayoutParams.endOf: Int
    get() = topToTop
    set(value) {
        endToEnd = value
        topToTop = value
        bottomToBottom = value
    }

var ConstraintLayout.LayoutParams.topStartOf: Int
    get() = startToStart
    set(value) {
        startToStart = value
        topToTop = value
    }


var ConstraintLayout.LayoutParams.bottomStartOf: Int
    get() = startToStart
    set(value) {
        startToStart = value
        bottomToBottom = value
    }

var ConstraintLayout.LayoutParams.topEndOf: Int
    get() = startToStart
    set(value) {
        endToEnd = value
        topToTop = value
    }

var ConstraintLayout.LayoutParams.bottomEndOf: Int
    get() = startToStart
    set(value) {
        endToEnd = value
        bottomToBottom = value
    }

var View.allowClick: Boolean
    get() = isFocusable && isClickable && isEnabled
    set(value) {
        isFocusable = value
        isClickable = value
        isEnabled = value
    }

var View.show: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

var Collection<View>.show: Boolean
    get() = find { !it.show }?.let { false } ?: true
    set(value) {
        forEach {
            it.show = value
        }
    }

var Collection<View>.visibility: Int
    get() = if (isEmpty()) View.GONE else first()?.visibility
    set(value) {
        forEach {
            it.visibility = value
        }
    }

/**
 * 增加竖直方向的分割间距
 * 第一个item的顶部也会相应的距离
 * value >=0 则为水平分割线
 * value <0 则为垂直分割线
 */
var RecyclerView.decoration: Int
    get() = 0
    set(value) {
        this.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildLayoutPosition(view)
                if (value > 0) {
                    if (position == 0) outRect.top = value
                    outRect.bottom = value
                } else {
                    if (position == 0) outRect.left = Math.abs(value)
                    outRect.right = Math.abs(value)
                }
            }
        })
    }


/**-----------------------------------Method---------------------------------------*/

const val DEFAULT_VIEW_MAX_SIZE = 350f

//hierarchy.roundingParams = RoundingParams().setRoundAsCircle(true)

var SimpleDraweeView.isRound
    get() = hierarchy.roundingParams?.roundAsCircle ?: false
    set(value) {
        hierarchy.roundingParams = RoundingParams().setRoundAsCircle(value)
    }

var SimpleDraweeView.cornerRadius
    get() = hierarchy.roundingParams?.cornersRadii?.average()?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams = RoundingParams.fromCornersRadius(value)
    }

var SimpleDraweeView.cornerRadiusTop
    get() = (cornerRadiusTopLeft + cornerRadiusTopRight) / 2.0f
    set(value) {
        cornerRadiusTopLeft = value
        cornerRadiusTopRight = value
    }

var SimpleDraweeView.cornerRadiusBottom
    get() = (cornerRadiusBottomLeft + cornerRadiusBottomRight) / 2.0f
    set(value) {
        cornerRadiusBottomLeft = value
        cornerRadiusBottomRight = value
    }

var SimpleDraweeView.cornerRadiusLeft
    get() = (cornerRadiusTopLeft + cornerRadiusBottomLeft) / 2.0f
    set(value) {
        cornerRadiusTopLeft = value
        cornerRadiusBottomLeft = value
    }

var SimpleDraweeView.cornerRadiusRight
    get() = (cornerRadiusTopRight + cornerRadiusBottomRight) / 2.0f
    set(value) {
        cornerRadiusTopRight = value
        cornerRadiusBottomRight = value
    }

var SimpleDraweeView.cornerRadiusTopLeft
    get() = hierarchy.roundingParams?.cornersRadii?.let { it[0] }?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams =
            RoundingParams.fromCornersRadii(
                value,
                cornerRadiusTopRight, cornerRadiusBottomRight, cornerRadiusBottomLeft
            )
    }

var SimpleDraweeView.cornerRadiusTopRight
    get() = hierarchy.roundingParams?.cornersRadii?.let { it[1] }?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams =
            RoundingParams.fromCornersRadii(
                cornerRadiusTopLeft, value,
                cornerRadiusBottomRight, cornerRadiusBottomLeft
            )
    }

var SimpleDraweeView.cornerRadiusBottomRight
    get() = hierarchy.roundingParams?.cornersRadii?.let { it[2] }?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams =
            RoundingParams.fromCornersRadii(
                cornerRadiusTopLeft,
                cornerRadiusTopRight, value, cornerRadiusBottomLeft
            )
    }

var SimpleDraweeView.cornerRadiusBottomLeft
    get() = hierarchy.roundingParams?.cornersRadii?.let { it[3] }?.toFloat() ?: 0f
    set(value) {
        hierarchy.roundingParams =
            RoundingParams.fromCornersRadii(
                cornerRadiusTopLeft,
                cornerRadiusTopRight, cornerRadiusBottomRight, value
            )
    }

var GradientDrawable.cornerRadiusTop
    get() = (cornerRadiusTopLeft + cornerRadiusTopRight) / 2.0f
    set(value) {
        cornerRadiusTopLeft = value
        cornerRadiusTopRight = value
    }

var GradientDrawable.cornerRadiusBottom
    get() = (cornerRadiusBottomLeft + cornerRadiusBottomRight) / 2.0f
    set(value) {
        cornerRadiusBottomLeft = value
        cornerRadiusBottomRight = value
    }

var GradientDrawable.cornerRadiusLeft
    get() = (cornerRadiusTopLeft + cornerRadiusBottomLeft) / 2.0f
    set(value) {
        cornerRadiusTopLeft = value
        cornerRadiusBottomLeft = value
    }

var GradientDrawable.cornerRadiusRight
    get() = (cornerRadiusTopRight + cornerRadiusBottomRight) / 2.0f
    set(value) {
        cornerRadiusTopRight = value
        cornerRadiusBottomRight = value
    }

var GradientDrawable.cornerRadiusTopLeft
    @SuppressLint("NewApi") @RequiresApi(Build.VERSION_CODES.N)
    get() = kotlin.runCatching { cornerRadii?.let { (it[0] + it[1]) / 2f }?.toFloat() }.getOrNull() ?: 0f
    set(value) {
        cornerRadii = floatArrayOf(
            value, value,
            cornerRadiusTopRight, cornerRadiusTopRight,
            cornerRadiusBottomLeft, cornerRadiusBottomLeft,
            cornerRadiusBottomRight, cornerRadiusBottomRight
        )
    }

var GradientDrawable.cornerRadiusTopRight
    @SuppressLint("NewApi") @RequiresApi(Build.VERSION_CODES.N)
    get() = kotlin.runCatching { cornerRadii?.let { (it[2] + it[3]) / 2f }?.toFloat() }.getOrNull() ?: 0f
    set(value) {
        cornerRadii = floatArrayOf(
            cornerRadiusTopLeft, cornerRadiusTopLeft,
            value, value,
            cornerRadiusBottomLeft, cornerRadiusBottomLeft,
            cornerRadiusBottomRight, cornerRadiusBottomRight
        )
    }

var GradientDrawable.cornerRadiusBottomLeft
    @SuppressLint("NewApi") @RequiresApi(Build.VERSION_CODES.N)
    get() = kotlin.runCatching { cornerRadii?.let { (it[4] + it[5]) / 2f }?.toFloat() }.getOrNull() ?: 0f
    set(value) {
        cornerRadii = floatArrayOf(
            cornerRadiusTopLeft, cornerRadiusTopLeft,
            cornerRadiusTopRight, cornerRadiusTopRight,
            value, value,
            cornerRadiusBottomRight, cornerRadiusBottomRight
        )
    }


var GradientDrawable.cornerRadiusBottomRight
    @SuppressLint("NewApi") @RequiresApi(Build.VERSION_CODES.N)
    get() = kotlin.runCatching { cornerRadii?.let { (it[6] + it[7]) / 2f }?.toFloat() }.getOrNull() ?: 0f
    set(value) {
        cornerRadii = floatArrayOf(
            cornerRadiusTopLeft, cornerRadiusTopLeft,
            cornerRadiusTopRight, cornerRadiusTopRight,
            cornerRadiusBottomLeft, cornerRadiusBottomLeft,
            value, value
        )
    }

var TextView.drawableLeft
    get() = kotlin.runCatching { compoundDrawables?.let { it[0] } }.getOrNull()
    set(left) {
        drawableIntrinsicBounds(left)
    }

var TextView.drawableTop
    get() = kotlin.runCatching { compoundDrawables?.let { it[1] } }.getOrNull()
    set(top) {
        drawableIntrinsicBounds(top = top)
    }

var TextView.drawableRight
    get() = kotlin.runCatching { compoundDrawables?.let { it[2] } }.getOrNull()
    set(right) {
        drawableIntrinsicBounds(right = right)
    }

var TextView.drawableBottom
    get() = kotlin.runCatching { compoundDrawables?.let { it[3] } }.getOrNull()
    set(bottom) {
        drawableIntrinsicBounds(bottom = bottom)
    }

var TextView.drawableLeftResource: Int
    get() = noGetter()
    set(value) {
        drawableIntrinsicBounds(value)
    }

var TextView.drawableTopResource: Int
    get() = noGetter()
    set(value) {
        drawableIntrinsicBounds(top = value)
    }

var TextView.drawableRightResource: Int
    get() = noGetter()
    set(value) {
        drawableIntrinsicBounds(right = value)
    }

var TextView.drawableBottomResource: Int
    get() = noGetter()
    set(value) {
        drawableIntrinsicBounds(bottom = value)
    }

var TextView.drawablePadding
    get() = compoundDrawablePadding
    set(value) {
        compoundDrawablePadding = value
    }

var EditText.textCursorColorResource: Int
    get() = noGetter()
    set(value) {
        textCursorColor = color(value)
    }
var EditText.textCursorColor: Int
    get() = noGetter()
    set(color) {
        try {
            val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")//获取这个字段
            fCursorDrawableRes.isAccessible = true//代表这个字段、方法等等可以被访问
            val mCursorDrawableRes = fCursorDrawableRes.getInt(this)

            val fEditor = TextView::class.java.getDeclaredField("mEditor")
            fEditor.isAccessible = true
            val editor = fEditor.get(this)

            val clazz = editor.javaClass
            val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
            fCursorDrawable.isAccessible = true

            val drawables = arrayOf(
                this.context.drawable(mCursorDrawableRes),
                this.context.drawable(mCursorDrawableRes)
            )
            drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)//SRC_IN 上下层都显示。下层居上显示。
            drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            fCursorDrawable.set(editor, drawables)
        } catch (ignored: Throwable) {
        }
    }

val AppBarLayout.scroll
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
val AppBarLayout.enterAlways
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
val AppBarLayout.enterAlwaysCollapsed
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
val AppBarLayout.snap
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
val AppBarLayout.exitUntilCollapsed
    get() = AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED


val CollapsingToolbarLayout.pin
    get() = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PIN
val CollapsingToolbarLayout.off
    get() = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_OFF
val CollapsingToolbarLayout.parallax
    get() = CollapsingToolbarLayout.LayoutParams.COLLAPSE_MODE_PARALLAX