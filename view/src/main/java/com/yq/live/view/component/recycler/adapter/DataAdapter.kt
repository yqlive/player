package com.yq.live.view.component.recycler.adapter

import com.yq.live.rely.PhrRes
import com.yq.live.rely.Pre

interface DataAdapter<E> {

    val datas: Collection<E>

    fun items(list: Collection<E>): DataAdapter<E>

    fun addItems(list: Collection<E>)

    fun removeItems(list: Collection<E>)

    fun addItem(item: E)

    fun addItem(position: Int, item: E)

    fun removeItem(item: E)

    fun removeItem(position: Int)

    fun removeAll(notify: Boolean = true, filter: ((E) -> Boolean)? = null)

    fun recycle()

    fun replaceItem(position: Int, item: E, block: PhrRes<E, E, Boolean>? = null)

    fun onDataChanged(block: Pre<Collection<E>>)
}