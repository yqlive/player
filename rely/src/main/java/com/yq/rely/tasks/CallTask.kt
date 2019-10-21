package com.yq.rely.tasks

import com.yq.rely.keeps.tasks.TaskState
import com.yq.rely.w
import kotlinx.coroutines.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CallTask<R>(private val taskDispatcher: CoroutineDispatcher = Dispatchers.Default, private val miTask: (Continuation<R>) -> Unit) : BaseTask<R>() {

    @Suppress("UNCHECKED_CAST")
    override fun work(dispatcher: CoroutineDispatcher): CallTask<R> {
        GlobalScope.async(dispatcher) {
            try {
                state = TaskState.EXECUTING
                job = async(taskDispatcher) {
                    runCatching {
                        suspendCoroutine<R> {
                            try {
                                miTask(it)
                            } catch (e: Throwable) {
                                it.resumeWithException(e)
                            }
                        }
                    }
                }
                val deferred = job as Deferred<Result<R>>
                val result = if (timeMillis > 0) withTimeout(timeMillis) { deferred.await() } else deferred.await()
                onDone?.let { it(result.getOrThrow()) }
            } catch (e: Throwable) {
                when {
                    isTimeout(e) -> onTimeout?.let { it() }
                    isCancel(e) -> {}
                    else -> {
                        w(e, "CallTask Exception")
                        onFailed?.let { it(e) }
                    }
                }
            }
        }
        return this
    }

}