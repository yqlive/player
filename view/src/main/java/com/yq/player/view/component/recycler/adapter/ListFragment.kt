package com.yq.player.view.component.recycler.adapter

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yq.player.rely.d
import com.yq.player.view.anko.AnkoFragment
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView


open class ListFragment : AnkoFragment() {

    protected var _index: Int = 0

    protected var _recyclerView: RecyclerView? = null
    protected var _recyclerViewRender: (RecyclerView.() -> Unit)? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _index = savedInstanceState?.getInt("index") ?: arguments?.getInt("index") ?: 0
        return AnkoContext.create(context!!).ui()
    }

    override fun AnkoContext<Context>.ui(): View = this.recyclerView {
        _recyclerView = this
        layoutManager = LinearLayoutManager(context)
        _recyclerViewRender?.invoke(this)
        lparams(matchParent, matchParent)
    }


    fun recyclerView(blo: RecyclerView.() -> Unit) {
        _recyclerViewRender = blo
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("index", _index)
        d("call onSaveInstanceState:$_index", "ListFragment")
    }


}
