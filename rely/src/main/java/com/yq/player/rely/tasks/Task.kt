package com.yq.player.rely.tasks


import com.yq.player.rely.keeps.tasks.TaskState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface Task<R> {
    /**
     * @param block 任务完成后的执行块
     */
    fun done(block: suspend Task<R>.(R) -> Unit): Task<R>

    /**
     * @param block 任务出现异常后的执行块
     */
    fun failed(block: Task<R>.(err: Throwable) -> Unit): Task<R>

    /**
     * @param timeMillis 超时时限(毫秒)
     * @param block 任务超时后的执行块
     */
    fun timeout(timeMillis: Long = 30000, block: (Task<R>.() -> Unit)? = null): Task<R>

    /**
     * @param block 任务取消后的执行块
     */
    fun cancelled(block: Task<R>.() -> Unit): Task<R>

    fun finally(block: Task<R>.() -> Unit): Task<R>

    /**
     *@param dispatcher 任务整体所使用的协程作用域
     */
    fun work(dispatcher: CoroutineDispatcher = Dispatchers.Default): Task<R>

    /**
     * @param force 是否强制取消，true:无论任务是否正在执行，都强行取消。fasle:只有当任务正在执行时才会取消
     */
    fun cancel(force: Boolean = false)

    fun destory()

    operator fun <V> set(key: String, value: V)

    operator fun <V> get(key: String): V?

    val taskId: String

    val isWaiting: Boolean

    val isExecuting: Boolean

    val isFinished: Boolean

    val state: TaskState

}

