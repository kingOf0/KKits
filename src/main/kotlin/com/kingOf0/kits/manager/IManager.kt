package com.kingOf0.kits.manager

import com.kingOf0.kits.LOGGER
import com.kingOf0.kits.util.KUtils.disable

abstract class IManager(private val name: String) {

    protected abstract fun load() : Boolean
    protected var failMessage: String? = null

    fun initialize() {
        runCatching { load() }
            .onSuccess {
                if (it) success() else failure()
            }.onFailure {
                it.printStackTrace()
                failure()
            }
    }

    protected fun log(message: String) {
        LOGGER.info("[$name] * $message")
    }

    private fun success() {
        LOGGER.info("+ $name | Successfully loaded.")
    }

    fun failure() {
        LOGGER.warning("- $name | Couldn't loaded successfully! | $failMessage")
        disable(failMessage ?: "$name | Couldn't loaded successfully!") //todo: check appearance
    }


}