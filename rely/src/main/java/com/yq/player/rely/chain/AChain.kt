package com.yq.player.rely.chain

import com.yq.player.rely.d
import com.yq.player.rely.mainCoroutine
import com.yq.player.rely.w

typealias  Block<S, E> = suspend Box.(S) -> E

/**
 * 创建任务链
 * @param block 任务块
 * @return [Knot] 链节点
 */
fun <E> chain(block: suspend Box.() -> E) = Knot({ Unit }, { block() })

/**+
 *  创建链节点
 * @param block 任务块
 * @return [Knot] 链节点
 */
fun <S, E> Knot<*, S>.then(course: Block<S, E>): Knot<S, E> =
    Knot(this, course).apply {
        box = this@then.box
    }

/**
 *  结束链节点构建，并生成任务链对象
 * @param block 任务块
 * @return [Chain] 任务链
 */
fun <S, E> Knot<*, S>.end(course: Block<S, E>) = this.then(course).let { Chain(it) }

private fun demo() {
    chain {
        "start" put "this is a string"//将字符串"this is a string"存入一个名为"start"的变量中
        1 + 1
    }.then {
        "${it + 2}"
    }.then {
        it.toInt()
    }.end {
        it.toFloat()
    }.onCancel {
        w("cancel of knot index :${it.currentStep}")
    }.onTimeout(1000) {
        w("time out at knot index  :${it.currentStep}")
    }.onFailure { _, throwable ->
        w(throwable, "Catched Throwable")
    }.onFinally {
        "key" put 15

        //取出"start"中的值，方法1取值可能为空，且需要指定返回类型
        val way1: String? = take("key")
        //方法2取值为空时，返回指定的默认值，返回类型为默认值类型
        val way2 = "key" take "default value"
        val way3 = "key" take String::class.java
        d("${way1 == way2}")
        it.destory()
    }.call(mainCoroutine)
}