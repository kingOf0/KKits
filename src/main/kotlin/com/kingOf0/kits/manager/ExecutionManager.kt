package com.kingOf0.kits.manager

import com.kingOf0.kits.PLUGIN_INSTANCE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.logging.Level

/**
 * Using plugin's logger for avoiding printing errors to standard output with no tag.
 */
private val LOGGER = PLUGIN_INSTANCE.logger

class QueueJob<T>(private val job: suspend () -> T) {

    private var onCompleted: ((T) -> Unit)? = null
    private var onError: ((Throwable) -> Unit) = { LOGGER.log(Level.SEVERE, "ExecutionManager occurred an error!", it) }

    suspend fun invoke() {
        runCatching { job.invoke() }
            .onSuccess { onCompleted?.invoke(it) }
            .onFailure { onError.invoke(it) }
    }

    /**
     * This is a blocking call for the Execution Manager's main executor coroutine!
     * If you are not going to add a new job to queue, start another coroutine here to avoid blocking executor queue!
     */
    fun invokeOnCompletion(block: (T) -> Unit): QueueJob<T> {
        onCompleted = block
        return this
    }

    /**
     * This is a blocking call for the Execution Manager's main executor coroutine!
     *
     * Default: it will just print the stack trace to the console.
     */
    fun invokeOnError(block: (Throwable) -> Unit): QueueJob<T> {
        onError = block
        return this
    }
}

object ExecutionManager {

    private val queue = Vector<QueueJob<*>>()
    private val prioritizedQueue = Vector<QueueJob<*>>()

    private val job = CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            delay(100)
            startAll(prioritizedQueue)
            startAll(queue)
        }
    }

    private suspend fun startAll(queue: Vector<QueueJob<*>>) {
        while (queue.isNotEmpty()) {
            queue.removeFirstOrNull()?.invoke()
        }
    }

    fun <T> addQueue(block: suspend () -> T): QueueJob<T> {
        val job = QueueJob(block)
        queue.add(job)
        return job
    }

    fun <T> addQueueImm(block: suspend () -> T): QueueJob<T> {
        val job = QueueJob(block)
        prioritizedQueue.add(job)
        return job
    }

    fun destroy() {
        job.cancel()
    }

}
