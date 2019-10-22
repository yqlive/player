package com.yq.live.view.anko

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yq.live.rely.d
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoContextImpl
import org.jetbrains.anko.internals.AnkoInternals

abstract class AnkoActivity : AppCompatActivity() {
    protected lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiBefore(savedInstanceState)
        rootView = AnkoContextImpl(this, this, true).ui()
        uiCreated()
    }

    abstract fun AnkoContext<Activity>.ui(): View

    open fun uiBefore(savedInstanceState: Bundle?) {}

    open fun uiCreated() {}

    private val _clickListener by lazy { View.OnClickListener { v -> onClickView(v) } }

    open fun onClickView(v: View) {}

    fun <T : View> T.toClick(): T {
        this.setOnClickListener(_clickListener)
        return this
    }

    val resultMap by lazy { hashMapOf<Int, (Int, Intent?) -> Unit>() }

    private var permissionResult: ((Int, Array<String>, IntArray) -> Unit)? = null

    fun permissionResult(block: (Int, Array<String>, IntArray) -> Unit) {
        permissionResult = block
    }

    inline fun <reified T : Activity> startActivityForResult(
        vararg params: Pair<String, Any?>,
        noinline blo: (Int, Intent?) -> Unit
    ) {
        val requestCode: Int = android.support.v4.view.ViewCompat.generateViewId()
        this.resultMap[requestCode] = blo
        AnkoInternals.internalStartActivityForResult(this, T::class.java, requestCode, params)
    }

    fun startActivityForResult(
        intent: Intent,
        requestCode: Int = android.support.v4.view.ViewCompat.generateViewId(),
        blo: (Int, Intent?) -> Unit
    ) {
        this.resultMap[requestCode] = blo
        startActivityForResult(intent, requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionResult?.invoke(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resultMap[requestCode]?.invoke(resultCode, data)
    }

    override fun onDestroy() {
        resultMap.clear()
        d("${this::class.java.name}", "onDestory")
        super.onDestroy()
    }
}

abstract class AnkoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        AnkoContext.create(context!!).ui()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        uiCreated()
    }

    abstract fun AnkoContext<Context>.ui(): View

    open fun uiCreated() {}

    private val _clickListener by lazy { View.OnClickListener { v -> onClickView(v) } }

    open fun onClickView(v: View) {}

    fun <T : View> T.toClick(): T {
        this.setOnClickListener(_clickListener)
        return this
    }
}