package com.yq.live.view.enko

import android.content.res.ColorStateList

class ColorSelector {
    private val colorMap by lazy { hashMapOf<Int, HashSet<Int>>() }
    fun addState(stateSet: IntArray, color: Int) {
        var colors = colorMap[color]
        if (colors == null)
            colors = hashSetOf()
        colors.addAll(stateSet.asList())
        colorMap[color] = colors
    }

    fun create(): ColorStateList {
        val colors = colorMap.keys.toIntArray()
        val states = arrayOfNulls<IntArray>(colors.size)
        colors.forEachIndexed { index, it ->
            states[index] = colorMap[it]?.toIntArray()
        }
        colorMap.clear()
        return ColorStateList(states, colors)
    }

}