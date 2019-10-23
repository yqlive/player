package com.yq.player.view.anko

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.SimpleDraweeView
import com.yq.player.view.component.recycler.adapter.RecyclerViewAdapter
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.internals.AnkoInternals

inline fun ViewManager.frescoImage(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    builder: GenericDraweeHierarchyBuilder.() -> Unit = {},
    init: SimpleDraweeView.() -> Unit
): SimpleDraweeView {
    return ankoView({
        SimpleDraweeView(ctx).apply {
            hierarchy = GenericDraweeHierarchyBuilder(ctx.resources).apply {
                fadeDuration = 300
                placeholderImageScaleType = ScalingUtils.ScaleType.CENTER_CROP
                this.builder()
            }.build()
        }
    }, theme, init)
}


fun <T> createAdapter(createBlock: AnkoContext<ViewGroup>.(Int) -> View): RecyclerViewAdapter<T> {
    return RecyclerViewAdapter(object : AnkoTypeAdapter {
        override fun createView(ui: AnkoContext<ViewGroup>, type: Int): View = with(ui) {
            this.createBlock(type)
        }
    })
}

fun <T> createAdapter(ankoUi: AnkoComponent<ViewGroup>): RecyclerViewAdapter<T> {
    return RecyclerViewAdapter(object : AnkoTypeAdapter {
        override fun createView(ui: AnkoContext<ViewGroup>, type: Int): View = ankoUi.createView(ui)
    })
}

fun <T> RecyclerView.adapter(createBlock: AnkoContext<ViewGroup>.(Int) -> View): RecyclerViewAdapter<T> =
    createAdapter<T>(createBlock).apply { adapter = this }

fun <T> RecyclerView.adapter(ankoUi: AnkoComponent<ViewGroup>): RecyclerViewAdapter<T> =
    createAdapter<T>(ankoUi).apply { adapter = this }


inline fun ViewManager.flexboxLayout(
    ctx: Context = AnkoInternals.getContext(this),
    theme: Int = 0,
    init: _FlexboxLayout.() -> Unit
): _FlexboxLayout {
    return ankoView({ _FlexboxLayout(ctx) }, theme, init)
}