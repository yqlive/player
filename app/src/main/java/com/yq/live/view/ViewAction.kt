package com.yq.live.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.postprocessors.IterativeBoxBlurPostProcessor
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.facebook.imagepipeline.request.Postprocessor
import com.yq.live.rely.id2Uri
import com.yq.live.view.component.recycler.adapter.DataAdapter
import org.jetbrains.anko.AnkoContext

/**-----------------------------------Animate---------------------------------------*/
fun View.slideTop(startDelay: Int = 0, duration: Int = 200, translationY: Float = 100f) {
    this.translationY = translationY
    alpha = 0f
    animate()
        .alpha(1f)
        .translationY(0f)
        .scaleY(1f)
        .setStartDelay(startDelay.toLong()).duration = duration.toLong()
}

fun View.slideOut(startDelay: Int = 0, duration: Int = 200, translationY: Float = 100f) {
    this.translationY = translationY
    alpha = 0f
    animate()
        .alpha(1f)
        .translationY(0f)
        .scaleY(1f)
        .setStartDelay(startDelay.toLong()).duration = duration.toLong()
}

fun View.alphaVary(startDelay: Int = 0, duration: Int = 200) {
    alpha = 0f
    animate()
        .alpha(1f)
        .setStartDelay(startDelay.toLong()).duration = duration.toLong()
}

/**-----------------------------------Find---------------------------------------*/

inline fun <reified T : View> View.find(@IdRes id: Int, block: T.() -> Unit): T? =
    findViewById<T>(id)?.apply { block() }

inline fun <reified T : View> Activity.find(@IdRes id: Int, block: T.() -> Unit): T? =
    findViewById<T>(id)?.apply { block() }

inline fun <reified T : View> View.finds(@IdRes vararg id: Int): List<T> {
    val views = arrayListOf<T>()
    id.forEach {
        findViewById<T>(it)?.apply { views.add(this) }
    }
    return views
}

inline fun <reified T : View> Activity.finds(@IdRes vararg id: Int): List<T> {
    val views = arrayListOf<T>()
    id.forEach {
        findViewById<T>(it)?.apply { views.add(this) }
    }
    return views
}

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(message = "Use support library fragments instead. Framework fragments were deprecated in API 28.")
inline fun <reified T : View> Fragment.find(@IdRes id: Int, block: T.() -> Unit): T? =
    (view?.findViewById(id) as? T)?.apply { block() }

inline fun <reified T : View> Dialog.find(@IdRes id: Int, block: T.() -> Unit): T =
    findViewById<T>(id).apply { block() }

/**-----------------------------------Listener---------------------------------------*/
inline fun View.onClick(crossinline listener: (View) -> Unit) {
    setOnClickListener {
        listener(it)
    }
}

/**-----------------------------------TextView---------------------------------------*/
fun TextView.drawableIntrinsicBounds(
    left: Drawable? = null, top: Drawable? = null, right: Drawable? = null, bottom: Drawable? = null
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

fun TextView.drawableIntrinsicBounds(
    left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0
) {
    setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
}

fun TextView.drawableBounds(
    drawable: Drawable? = null,
    mode: Int = DrawableMode.ALL,
    block: (Drawable.() -> Unit)? = null
) {
    if (block != null) {
        drawable?.block()
    }
    when (mode) {
        DrawableMode.LEFT ->
            setCompoundDrawables(drawable, null, null, null)
        DrawableMode.TOP ->
            setCompoundDrawables(null, drawable, null, null)
        DrawableMode.RIGHT ->
            setCompoundDrawables(null, null, drawable, null)
        DrawableMode.BOTTOM ->
            setCompoundDrawables(null, null, null, drawable)
        else ->
            setCompoundDrawables(drawable, drawable, drawable, drawable)
    }
}

fun <V : View> ViewGroup.forEach(block: (V) -> Unit) {
    for (i in 0 until childCount) block(getChildAt(i) as V)
}

fun TextView.drawableBounds(drawable: Int = 0, mode: Int = DrawableMode.ALL, block: (Drawable.() -> Unit)? = null) =
    drawableBounds(drawable(drawable), mode, block)

object DrawableMode {
    const val LEFT = 0
    const val RIGHT = 1
    const val TOP = 2
    const val BOTTOM = 3
    const val ALL = -1
}

/**-----------------------------------Resources---------------------------------------*/
fun Context.drawable(id: Int) = if (id != 0) ContextCompat.getDrawable(this, id) else null

fun View.drawable(id: Int) = context.drawable(id)
fun AnkoContext<*>.drawable(id: Int) = ctx.drawable(id)
inline fun Fragment.drawable(value: Int) = requireActivity().drawable(value)

fun Context.color(id: Int) = if (id != 0) ContextCompat.getColor(this, id) else 0
fun View.color(id: Int) = context.color(id)
fun AnkoContext<*>.color(id: Int) = ctx.color(id)
inline fun Fragment.color(value: Int) = requireActivity().color(value)

fun Context.color(color: String) = if (!color.isNullOrEmpty()) Color.parseColor(color) else 0
fun View.color(color: String) = context.color(color)
fun AnkoContext<*>.color(color: String) = ctx.color(color)
inline fun Fragment.color(color: String) = requireActivity().color(color)

fun color(color: Long) = color.toInt()

val gid get() = ViewCompat.generateViewId()

val lazyId get() = lazy { gid }

//fun <T : View> Activity.view(id: Int) = IdView<T>(id, this)
//fun <T : View> View.view(id: Int) = IdView<T>(id, this)
//fun <T : View> Fragment.view(id: Int) = IdView<T>(id, this)
//fun <T : View> Dialog.view(id: Int) = IdView<T>(id, this)
//
//fun <T : View> Activity.viewOptional(id: Int) = IdViewOptional<T>(id, this)
//fun <T : View> View.viewOptional(id: Int) = IdView<T>(id, this)
//fun <T : View> Fragment.viewOptional(id: Int) = IdView<T>(id, this)
//fun <T : View> Dialog.viewOptional(id: Int) = IdView<T>(id, this)

/**-----------------------------------Others---------------------------------------*/
@JvmOverloads
fun SimpleDraweeView.preview(
    uri: String,
    w: Int = this.width,
    h: Int = this.height,
    maxSiz: Float = DEFAULT_VIEW_MAX_SIZE,
    auto: Boolean = false
) {
    preview(Uri.parse(uri), w, h, maxSiz, auto)
}

@JvmOverloads
fun SimpleDraweeView.preview(
    uri: Uri,
    w: Int = this.width,
    h: Int = this.height,
    maxSiz: Float = DEFAULT_VIEW_MAX_SIZE,
    auto: Boolean = false
) {
    val builder = ImageRequestBuilder.newBuilderWithSource(uri)
    if (w > 0 && h > 0) {
        builder.resizeOptions = ResizeOptions(w, h, maxSiz)
    }
    val request = builder.build()
    val controller = Fresco.newDraweeControllerBuilder()
        .setImageRequest(request)
        .setTapToRetryEnabled(true)
        .setAutoPlayAnimations(auto)
        .setOldController(this.controller)
        .build() as PipelineDraweeController
    this.controller = controller
}

/**
 * 设置图片高斯模糊
 * @param url
 * @param iterations 迭代次数，越大越模化。
 * @param blurRadius 模糊图半径，必须大于0，越大越模糊。
 */
fun SimpleDraweeView.blurImage(uri: Uri, iterations: Int, blurRadius: Int) {
    val request = ImageRequestBuilder.newBuilderWithSource(uri)
        .setPostprocessor(IterativeBoxBlurPostProcessor(iterations, blurRadius) as Postprocessor)
        .build()
    val newController = Fresco.newDraweeControllerBuilder()
        .setOldController(controller)
        .setImageRequest(request)
        .build()
    controller = newController
}

fun SimpleDraweeView.blurImage(url: String, iterations: Int, blurRadius: Int) {
    blurImage(Uri.parse(url), iterations, blurRadius)
}

fun SimpleDraweeView.blurImage(resId: Int, iterations: Int, blurRadius: Int) {
    blurImage(context.id2Uri(resId), iterations, blurRadius)
}

fun SimpleDraweeView.setImageURI(resId: Int) {
    setImageURI(context.id2Uri(resId))
}

fun View.inLocation(cx: Float, cy: Float): Boolean {
    val location = IntArray(2)
// 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
    getLocationOnScreen(location)
    return RectF(
        location[0].toFloat(), location[1].toFloat(), location[0].toFloat() + width,
        location[1].toFloat() + height
    ).contains(cx, cy)
}

val RecyclerView.itemViews: Collection<View>
    get() {
        val dataAdapter = adapter as? DataAdapter<*>
        val views = arrayListOf<View>()
        dataAdapter?.datas?.forEachIndexed { index, any ->
            layoutManager.findViewByPosition(index)?.let {
                views.add(it)
            }
        }
        return views
    }