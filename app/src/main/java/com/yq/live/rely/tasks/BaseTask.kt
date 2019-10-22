package com.yq.live.rely.tasks

import com.yq.live.rely.keeps.tasks.TaskState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

abstract class BaseTask<Result> : Task<Result> {
    override var state: TaskState = TaskState.WAITING
    override val taskId = "${System.currentTimeMillis()}${Math.random()}"

    private val bundle by lazy { hashMapOf<String, Any>() }

    override operator fun <V> get(key: String): V? = bundle[key] as? V

    override operator fun <V> set(key: String, value: V) {
        bundle[key] = value as Any
    }

    protected var onDone: (suspend (Result) -> Unit)? = {
        state = TaskState.DONE
        onFinally(this)
    }
    protected var onCancelled: (Task<Result>.() -> Unit)? = {
        this@BaseTask.state = TaskState.WAITING
        onFinally(this)
    }
    protected var onFailed: ((Throwable) -> Unit)? = {
        state = TaskState.FAILED
        onFinally(this)
    }
    protected var onTimeout: (() -> Unit)? = {
        state = TaskState.TIMEOUT
        onFinally(this)
    }
    protected var finallyBlock: (Task<Result>.() -> Unit)? = null
    protected val onFinally: Task<Result>.() -> Unit = { finallyBlock?.invoke(this) }
    protected var timeMillis = 0L
    protected var job: Job? = null

    override fun done(block: suspend Task<Result>.(Result) -> Unit): Task<Result> {
        onDone = {
            state = TaskState.DONE
            block(it)
            onFinally(this)
        }
        return this
    }

    override fun failed(block: Task<Result>.(err: Throwable) -> Unit): Task<Result> {
        onFailed = {
            state = TaskState.FAILED
            block(it)
            onFinally(this)
        }
        return this
    }

    override fun timeout(timeMillis: Long, block: (Task<Result>.() -> Unit)?): Task<Result> {
        if (timeMillis > 0)
            this.timeMillis = timeMillis
        onTimeout = {
            state = TaskState.TIMEOUT
            block?.invoke(this)
            onFinally(this)
        }
        return this
    }

    override fun cancelled(block: Task<Result>.() -> Unit): Task<Result> {
        onCancelled = {
            this@BaseTask.state = TaskState.WAITING
            block
            onFinally(this)
        }
        return this
    }

    override fun finally(block: Task<Result>.() -> Unit): Task<Result> {
        finallyBlock = block
        return this
    }

    override fun cancel(force: Boolean) {
        if (isExecuting || force) {
            try {
                state = TaskState.WAITING
                job?.cancel()
                onCancelled?.let { it() }
            } catch (e: Throwable) {
            }
        }
    }

    override fun destory() {
        try {
            job?.cancel()
        } catch (e: Throwable) {
        }
        state = TaskState.WAITING
        onDone = null
        onCancelled = null
        onFailed = null
        finallyBlock = null
        onTimeout = null
        timeMillis = 0L
        job = null
        bundle.clear()
    }


    override val isWaiting: Boolean
        get() = state == TaskState.WAITING
    override val isExecuting: Boolean
        get() = state == TaskState.EXECUTING
    override val isFinished: Boolean
        get() = !isWaiting && !isExecuting

    protected fun isTimeout(e: Throwable) =
        timeMillis > 0 && (e is TimeoutCancellationException || e is TimeOutException || e is TimeoutException || e is SocketTimeoutException || (e is InterruptedIOException && (e.message?.contains(
            "timeout"
        ) ?: false)))

    protected fun isCancel(e: Throwable) = e is CancellationException

}