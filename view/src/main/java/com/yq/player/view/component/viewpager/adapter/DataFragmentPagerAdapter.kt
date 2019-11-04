package com.yq.player.view.component.viewpager.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class DataFragmentPagerAdapter<P>(
    sm: FragmentManager,
    private val datas: MutableList<P>,
    private val render: (P.(Int) -> Fragment)
) : FragmentPagerAdapter(sm) {

    val fragments by lazy { linkedMapOf<Int, Fragment>() }

    operator fun get(key: Int): Fragment? {
        return fragments[key]
    }

    fun itemFragment(position: Int) = fragments[position]

    override fun getItem(position: Int): Fragment {
        return fragments[position] ?: render.let { datas[position].it(position) }
    }

    override fun getCount(): Int = datas.size
}