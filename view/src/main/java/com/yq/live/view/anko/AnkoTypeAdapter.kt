package com.yq.live.view.anko

import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext

interface AnkoTypeAdapter {
    fun createView(ui: AnkoContext<ViewGroup>, type: Int): View
}