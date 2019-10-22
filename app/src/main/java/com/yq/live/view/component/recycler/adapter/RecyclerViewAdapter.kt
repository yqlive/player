package com.yq.live.view.component.recycler.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yq.live.rely.PhrRes
import com.yq.live.rely.Pre
import com.yq.live.rely.WeakRef
import com.yq.live.rely.weakRefrence
import com.yq.live.view.anko.AnkoTypeAdapter
import com.yq.live.view.component.recycler.holder.RecyclerViewHolder
import com.yq.live.view.onClick
import org.jetbrains.anko.AnkoContext


class RecyclerViewAdapter<E>(
    private val layoutResId: Int,
    private var creator: AnkoTypeAdapter?
) : RecyclerView.Adapter<RecyclerViewHolder<E>>(), DataAdapter<E> {

    constructor(creator: AnkoTypeAdapter) : this(0, creator)
    constructor(layoutResId: Int) : this(layoutResId, null)

    override val datas by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { arrayListOf<E>() }
    private var _renderItemBlock: (View.(item: E, i: Int, type: Int) -> Unit)? = null

    private var _recyclerView: WeakRef<RecyclerView>? = null

    private var _dataChanged: Pre<Collection<E>>? = null

    val recyclerView: RecyclerView?
        get() = _recyclerView?.invoke()

    val itemViews: List<View>
        get() {
            val views = arrayListOf<View>()
            datas.forEachIndexed { index, any ->
                recyclerView?.layoutManager?.findViewByPosition(index)?.let {
                    views.add(it)
                }
            }
            return views
        }

    fun itemOf(e: E) = datas.indexOf(e)

    fun itemViewOf(e: E) = itemViewAt(itemOf(e))

    fun itemViewAt(i: Int) = recyclerView?.layoutManager?.findViewByPosition(i)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        _recyclerView = recyclerView.weakRefrence()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this._recyclerView = null
    }

    fun render(renderItemBlock: View.(item: E, i: Int, type: Int) -> Unit): RecyclerViewAdapter<E> {
        this._renderItemBlock = renderItemBlock
        return this
    }

    private var _itemClick: ((View, Int, E) -> Unit)? = null
    fun itemClick(blo: (View, Int, E) -> Unit): RecyclerViewAdapter<E> {
        _itemClick = blo
        return this
    }

    fun itemClick(blo: (E) -> Unit): RecyclerViewAdapter<E> = itemClick { _, _, data -> blo(data) }

    private val _clickIds by lazy { hashSetOf<Int>() }
    private var _itemViewClick: ((View, Int, E) -> Unit)? = null
    fun itemViewClick(vararg ids: Int, blo: (View, Int, E) -> Unit): RecyclerViewAdapter<E> {
        if (ids.isEmpty())
            return this
        _itemViewClick = blo
        _clickIds.clear()
        _clickIds.addAll(ids.toTypedArray())
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder<E> {
        val view = creator?.createView(AnkoContext.create(parent.context, parent), viewType)
            ?: LayoutInflater.from(parent.context).inflate(this.layoutResId, parent, false)
        return RecyclerViewHolder(view, viewType) { item, i, type ->
            _renderItemBlock?.let { it(item, i, type) }
            setOnClickListener { itemView ->
                val position = recyclerView?.getChildAdapterPosition(itemView) ?: i
                _itemClick?.invoke(itemView, position, item)
            }
            _clickIds.forEach {
                findViewById<View>(it)?.onClick { childView ->
                    val position = recyclerView?.run {
                        getChildAdapterPosition(layoutManager.findContainingItemView(childView))
                    } ?: i
                    _itemViewClick?.invoke(childView, position, item)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder<E>, position: Int) {
        val itemData = this.datas[position]
        holder.renderView(itemData, position)
    }

    private var _typeFinder: ((Int, E) -> Int)? = null

    fun typeFinder(blo: (Int, E) -> Int): RecyclerViewAdapter<E> {
        _typeFinder = blo
        return this
    }

    override fun getItemViewType(position: Int): Int {
        return _typeFinder?.invoke(position, datas[position]) ?: super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return this.datas.size
    }

    override fun items(list: Collection<E>): RecyclerViewAdapter<E> {
        this.datas.clear()
        if (list.isNotEmpty()) {
            this.datas.addAll(list)
        }
        _dataChanged?.let { it(datas) }
        this.notifyDataSetChanged()
        return this
    }

    override fun addItems(list: Collection<E>) {
        val len = this.datas.size
        if (list.isNotEmpty()) {
            this.datas.addAll(list)
            _dataChanged?.let { it(datas) }
            this.notifyItemRangeInserted(len, list.size)
        }

    }

    override fun removeItems(list: Collection<E>) {
        val len = this.datas.size
        if (list.isNotEmpty()) {
            this.datas.removeAll(list)
            _dataChanged?.let { it(datas) }
            this.notifyItemRangeRemoved(len, list.size)
        }

    }

    override fun addItem(item: E) {
        this.datas.add(item)
        _dataChanged?.let { it(datas) }
        this.notifyItemInserted(this.datas.size - 1)
    }

    override fun addItem(position: Int, item: E) {
        this.datas.add(position, item)
        _dataChanged?.let { it(datas) }
        this.notifyItemInserted(position)
    }

    override fun removeItem(item: E) {
        val i = datas.indexOf(item)
        if (i >= 0) {
            if (this.datas.remove(item)) {
                _dataChanged?.let { it(datas) }
                this.notifyItemRemoved(i)
            }
        }
    }

    override fun removeItem(position: Int) {
        this.datas.removeAt(position)
        _dataChanged?.let { it(datas) }
        this.notifyItemRemoved(position)
    }

    override fun removeAll(notify: Boolean, filter: ((E) -> Boolean)?) {
        var i = 0
        var size = datas.size
        if (filter == null)
            this.datas.clear()
        else
            while (i < datas.size) {
                if (filter(datas[i])) {
                    datas.removeAt(i)
                } else i++
            }
        if (datas.size != size) {
            _dataChanged?.let { it(datas) }
            if (notify)
                this.notifyDataSetChanged()
        }
    }


    fun removeIf(filter: ((E, Int, View?) -> Boolean)?) {
        var position = 0
        var size = datas.size
        val removeIf = hashSetOf<E>()
        if (filter == null)
            this.datas.clear()
        else
            while (position < datas.size) {
                if (filter(datas[position], position, recyclerView?.layoutManager?.findViewByPosition(position))) {
                    removeIf.add(datas[position])
                }
                position++
            }
        if (removeIf.isNotEmpty()) {
            datas.removeAll(removeIf)
        }
        if (datas.size != size) {
            _dataChanged?.let { it(datas) }
            this.notifyDataSetChanged()
        }
    }

    override fun replaceItem(position: Int, item: E, block: PhrRes<E, E, Boolean>?) {
        if (block == null || datas[position].block(item)) {
            this.datas[position] = item
            _dataChanged?.let { it(datas) }
            notifyItemChanged(position)
        }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        runCatching { super.setHasStableIds(hasStableIds) }
    }

    override fun onDataChanged(block: Pre<Collection<E>>) {
        _dataChanged = block
    }

    override fun recycle() {
        this.datas.clear()
        this._renderItemBlock = null
        this.creator = null
        this._typeFinder = null
    }
}
