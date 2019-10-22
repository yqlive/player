package com.yq.live.rely.chain

/**
 * 链节点
 * <S,E> [S] 来源数据泛型 [E] 返回数据泛型
 */
class Knot<S, E> {
    /**
     * 上级链节点
     */
    var originKnot: Knot<*, S>? = null
    /**
     * 节点顺序
     */
    val index: Int

    private val _course: Block<S, E>
    private val _origin: suspend Box.() -> S

    /**
     * 所属任务链
     */
    var chain: Chain<*>? = null


    /**
     * 链值
     */
    var box: Box = Box()

    /**
     * 链节点
     * @param cause 上级链节点
     * @param course 当前节点任务块
     */
    constructor(cause: Knot<*, S>, course: Block<S, E>) {
        _course = course
        originKnot = cause
        _origin = { cause.start() }
        index = cause.index + 1
    }

    /**
     * 链节点
     * @param cause 上级节点任务块
     * @param course 当前节点任务块
     */
    constructor(cause: suspend Box.() -> S, course: Block<S, E>) {
        _course = course
        _origin = cause
        index = 0
    }

    /**
     * 开始当前链节点
     * @return [E] 当前链节点的泛型返回值
     */
    suspend fun start(): E {
        chain?.let { it.currentStep = it.stepCount - index - 1 }
        return box._course(box._origin())
    }

    /**
     * 销毁当前链节点
     */
    fun destroy() {
        box.clear()
        originKnot?.destroy()
        originKnot = null
        chain = null
    }

}