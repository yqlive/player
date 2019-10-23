package com.yq.player.view

import android.app.Activity
import android.app.Dialog
import android.support.v4.app.Fragment
import android.view.View
import com.yq.player.rely.WeakRef
import com.yq.player.rely.weakRefrence
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <V : View> view(listener: View.OnClickListener? = null) = ViewBinder<V>(listener)

fun <V : View> view(block: () -> V) = ViewSetter(block())

fun <V : View> View.view(id: Int) = ViewFinder<V>(id, this)
fun <V : View> Activity.view(id: Int) = ViewFinder<V>(id, this)
fun <V : View> Dialog.view(id: Int) = ViewFinder<V>(id, this)
fun <V : View> Fragment.view(id: Int) = ViewFinder<V>(id, this)

fun <V : View> Any.view(id: Int) = view<V>(id, this)

fun <V : View> view(id: Int, context: Any): ViewFinder<V> {
    return when (context) {
        is View -> context.view(id)
        is Activity -> context.view(id)
        is Dialog -> context.view(id)
        is Fragment -> context.view(id)
        else -> throw Throwable("context must be View、Activity、Dialog、Fragment")
    }
}

/**
 * View绑定动态代理类
 * 被写入的View会绑定一个固定的Id，该Id不会被重新写入后而随之改变
 * 如果该动态代理没有赋值或者声明为了静态代理，在取值时可能会因为没有赋值抛空指针异常
 */
class ViewBinder<V : View>(onClickListener: View.OnClickListener? = null) : ReadWriteProperty<Any?, V> {
    private lateinit var view: WeakRef<V>
    private val vid by lazyId
    private val onClick = onClickListener?.weakRefrence()
    override fun getValue(thisRef: Any?, property: KProperty<*>): V = view()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        if (value.id == View.NO_ID) {
            value.id = vid
        }
        onClick?.invoke()?.let {
            value.setOnClickListener(it)
        }
        view = value.weakRefrence()
    }
}

/**
 * View赋值静态代理类
 * 初始化传入一个View，如果该View未曾设置过id，则会自动为其生成Id。该View被代理后的Id会随着自身Id的改变而改变
 */
class ViewSetter<V : View>(view: V) : ReadOnlyProperty<Any?, V> {
    private val _view: WeakRef<V> = view.weakRefrence()

    init {
        val value = _view()
        if (value.id == View.NO_ID) {
            value.id = gid
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V = _view()

}

/**
 * View查找动态代理类
 * 初始化传入View的指定id，以及View所在的上下文context
 * 该代理类被赋值时会为被代理的View的id设置为代理类初始化赋值的id
 * 从代理类取值时会从上下文中查找对应id的view
 * 如果该动态代理没有赋值或者声明为了静态代理，在取值时可能会因为找不到对应id的View而抛空指针异常
 */
class ViewFinder<V : View> : ReadWriteProperty<Any?, V> {
    private val _id: Int
    private val _context: WeakRef<Any>

    constructor(id: Int, context: View) {
        _id = id
        _context = context.weakRefrence()
    }

    constructor(id: Int, context: Dialog) {
        _id = id
        _context = context.weakRefrence()
    }

    constructor(id: Int, context: Fragment) {
        _id = id
        _context = context.weakRefrence()
    }

    constructor(id: Int, context: Activity) {
        _id = id
        _context = context.weakRefrence()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: V) {
        value.id = _id
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        return when (val con = _context()) {
            is Activity -> con.findViewById(_id) as V
            is Fragment -> con.view?.findViewById(_id) as V
            is View -> con.findViewById(_id) as V
            is Dialog -> con.findViewById(_id) as V
            else -> throw Throwable("context@$con must be View、Activity、Dialog、Fragment!")
        }
    }

}
