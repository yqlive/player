package com.yq.player.view.component.viewpager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import com.yq.player.view.component.viewpager.adapter.DataFragmentPagerAdapter
import com.yq.player.view.component.viewpager.adapter.DataFragmentStatePagerAdapter

fun <P> ViewPager.fragmentPagerAdapter(
    datas: MutableList<P>,
    render: (P.(Int) -> Fragment)
): DataFragmentPagerAdapter<P> {
    val sm = when (val ctx = context) {
        is FragmentActivity -> ctx.supportFragmentManager
        is Fragment -> ctx.requireFragmentManager()
        else -> throw Throwable("Unkown Context Type")
    }
    return DataFragmentPagerAdapter(sm, datas, render)
}

fun ViewPager.fragmentPagerAdapter(
    size: Int,
    render: (Int.(Int) -> Fragment)
): DataFragmentPagerAdapter<Int> {
    val sm = when (val ctx = context) {
        is FragmentActivity -> ctx.supportFragmentManager
        is Fragment -> ctx.requireFragmentManager()
        else -> throw Throwable("Unkown Context Type")
    }
    val data = arrayListOf<Int>()
    for (i in 0 until size) {
        data.add(i)
    }
    return DataFragmentPagerAdapter(sm, data, render)
}

fun <P> ViewPager.fragmentStatePagerAdapter(
    datas: MutableList<P>,
    render: (P.(Int) -> Fragment)
): DataFragmentStatePagerAdapter<P> {
    val sm = when (val ctx = context) {
        is FragmentActivity -> ctx.supportFragmentManager
        is Fragment -> ctx.requireFragmentManager()
        else -> throw Throwable("Unkown Context Type")
    }
    return DataFragmentStatePagerAdapter(sm, datas, render)
}

fun ViewPager.fragmentStatePagerAdapter(
    size: Int,
    render: (Int.(Int) -> Fragment)
): DataFragmentStatePagerAdapter<Int> {
    val sm = when (val ctx = context) {
        is FragmentActivity -> ctx.supportFragmentManager
        is Fragment -> ctx.requireFragmentManager()
        else -> throw Throwable("Unkown Context Type")
    }
    val data = arrayListOf<Int>()
    for (i in 0 until size) {
        data.add(i)
    }
    return DataFragmentStatePagerAdapter(sm, data, render)
}