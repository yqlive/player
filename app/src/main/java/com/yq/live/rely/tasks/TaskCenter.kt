package com.yq.live.rely.tasks

import com.yq.live.rely.childCoroutine
import com.yq.live.rely.weakRefrence
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.coroutines.Continuation

/**
 * 创建一个同步任务执行块
 * @param dispatcher 任务块所使用的协程作用域
 * @param block 任务块，执行后返回R
 */
fun <T, R> T.task(
    dispatcher: CoroutineDispatcher = childCoroutine,
    block: suspend T.() -> R
): Task<R> = SimpleTask(dispatcher, this.weakRefrence()(), block)

/**
 * 创建一个异步任务执行块
 * @param dispatcher 任务块所使用的协程作用域
 * @param block 任务块，执行前传入提供一个方法块参数，该参数用于手动完成任务。
 */
fun <T, R> T.call(
    dispatcher: CoroutineDispatcher = childCoroutine,
    block: T.(Continuation<R>) -> Unit
): Task<R> = AsyncTask(dispatcher, this.weakRefrence()(), block)

fun <R> call(
    dispatcher: CoroutineDispatcher = childCoroutine,
    block: (Continuation<R>) -> Unit
): Task<R> = CallTask(dispatcher, block)


class TimeOutException : RuntimeException("time out")


