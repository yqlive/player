package com.yq.view.component.recycler.holder

import android.support.v7.widget.RecyclerView
import android.view.View

class RecyclerViewHolder<T>(
    rootView: View,
    private val type: Int,
    private val renderItem: View.(item: T, i: Int, type: Int) -> Unit
) : RecyclerView.ViewHolder(rootView) {

    fun renderView(item: T, i: Int) {
        itemView.renderItem(item, i, type)
    }
}
