package com.yq.rely

import kotlinx.coroutines.*

val mainCoroutine = Dispatchers.Main

val childCoroutine = Dispatchers.Default

fun launch(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit
): Job = GlobalScope.launch(dispatcher, block = block)

fun <T> async(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend CoroutineScope.() -> T
): Deferred<T> = GlobalScope.async(dispatcher, block = block)

suspend fun <T> await(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: suspend CoroutineScope.() -> T
): T = async(dispatcher, block = block).await()

fun <T> T.untill(block: () -> Boolean): T {
    while (!block()) {
    }
    return this
}