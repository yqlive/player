package com.yq.player.view.enko

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable

class CircleAngle {
    val circleAngleArr by lazy { floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f) }
    var topLeft
        get() = circleAngleArr[0]
        set(value) {
            circleAngleArr[0] = value
            circleAngleArr[1] = value
        }
    var topRigth
        get() = circleAngleArr[2]
        set(value) {
            circleAngleArr[2] = value
            circleAngleArr[3] = value
        }
    var buttomLeft
        get() = circleAngleArr[4]
        set(value) {
            circleAngleArr[4] = value
            circleAngleArr[5] = value
        }
    var buttomRight
        get() = circleAngleArr[6]
        set(value) {
            circleAngleArr[6] = value
            circleAngleArr[7] = value
        }
}

data class Storke(var width: Int = 0, var color: Int = 0)

var GradientDrawable.backgoundColor: Int
    get() = 0
    set(value) = setColor(value)

fun GradientDrawable.circleAngle(block: CircleAngle.() -> Unit) {
    cornerRadii = CircleAngle().apply { block() }.circleAngleArr
}

fun GradientDrawable.storke(block: Storke.() -> Unit) {
    val storke = Storke().apply { block() }
    setStroke(storke.width, storke.color) //边框宽度，边框颜色
}

fun StateListDrawable.aboveAnchor(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_above_anchor * if (at) 1 else -1), drawable)
}

fun StateListDrawable.accelerated(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_accelerated * if (at) 1 else -1), drawable)
}

fun StateListDrawable.activated(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_activated * if (at) 1 else -1), drawable)
}

fun StateListDrawable.active(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_active * if (at) 1 else -1), drawable)
}

fun StateListDrawable.checkable(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_checkable * if (at) 1 else -1), drawable)
}

fun StateListDrawable.checked(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_checked * if (at) 1 else -1), drawable)
}

fun StateListDrawable.dragCanAccept(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_drag_can_accept * if (at) 1 else -1), drawable)
}

fun StateListDrawable.dragHovered(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_drag_hovered * if (at) 1 else -1), drawable)
}

fun StateListDrawable.empty(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_empty * if (at) 1 else -1), drawable)
}

fun StateListDrawable.enabled(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_enabled * if (at) 1 else -1), drawable)
}

fun StateListDrawable.expanded(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_expanded * if (at) 1 else -1), drawable)
}

fun StateListDrawable.first(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_first * if (at) 1 else -1), drawable)
}

fun StateListDrawable.focused(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_focused * if (at) 1 else -1), drawable)
}

fun StateListDrawable.hovered(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_hovered * if (at) 1 else -1), drawable)
}

fun StateListDrawable.last(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_last * if (at) 1 else -1), drawable)
}

fun StateListDrawable.longPressable(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_long_pressable * if (at) 1 else -1), drawable)
}

fun StateListDrawable.middle(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_middle * if (at) 1 else -1), drawable)
}

fun StateListDrawable.multiline(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_multiline * if (at) 1 else -1), drawable)
}

fun StateListDrawable.pressed(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_pressed * if (at) 1 else -1), drawable)
}

fun StateListDrawable.selected(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_selected * if (at) 1 else -1), drawable)
}

fun StateListDrawable.single(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_single * if (at) 1 else -1), drawable)
}

fun StateListDrawable.windowFocused(drawable: Drawable? = null, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_window_focused * if (at) 1 else -1), drawable)
}

fun ColorSelector.aboveAnchor(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_above_anchor * if (at) 1 else -1), color)
}

fun ColorSelector.accelerated(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_accelerated * if (at) 1 else -1), color)
}

fun ColorSelector.activated(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_activated * if (at) 1 else -1), color)
}

fun ColorSelector.active(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_active * if (at) 1 else -1), color)
}

fun ColorSelector.checkable(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_checkable * if (at) 1 else -1), color)
}

fun ColorSelector.checked(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_checked * if (at) 1 else -1), color)
}

fun ColorSelector.dragCanAccept(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_drag_can_accept * if (at) 1 else -1), color)
}

fun ColorSelector.dragHovered(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_drag_hovered * if (at) 1 else -1), color)
}

fun ColorSelector.empty(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_empty * if (at) 1 else -1), color)
}

fun ColorSelector.enabled(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_enabled * if (at) 1 else -1), color)
}

fun ColorSelector.expanded(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_expanded * if (at) 1 else -1), color)
}

fun ColorSelector.first(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_first * if (at) 1 else -1), color)
}

fun ColorSelector.focused(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_focused * if (at) 1 else -1), color)
}

fun ColorSelector.hovered(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_hovered * if (at) 1 else -1), color)
}

fun ColorSelector.last(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_last * if (at) 1 else -1), color)
}

fun ColorSelector.longPressable(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_long_pressable * if (at) 1 else -1), color)
}

fun ColorSelector.middle(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_middle * if (at) 1 else -1), color)
}

fun ColorSelector.multiline(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_multiline * if (at) 1 else -1), color)
}

fun ColorSelector.pressed(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_pressed * if (at) 1 else -1), color)
}

fun ColorSelector.selected(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_selected * if (at) 1 else -1), color)
}

fun ColorSelector.single(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_single * if (at) 1 else -1), color)
}

fun ColorSelector.windowFocused(color: Int, at: Boolean = true) {
    addState(intArrayOf(android.R.attr.state_window_focused * if (at) 1 else -1), color)
}