package com.kingOf0.kits.manager

import com.kingOf0.kits.LOGGER
import com.kingOf0.kits.util.KUtils.disable

abstract class IManager(private val name: String) {

    protected abstract fun load() : Boolean

    fun initialize() {
        runCatching { load() }
            .onSuccess {
                if (it) success() else failure()
            }.onFailure {
                it.printStackTrace()
                failure()
            }
    }

    private fun success() {
        LOGGER.info("+ $name | Successfully loaded.")
    }

    private fun failure() {
        LOGGER.warning("- $name | Couldn't loaded successfully!")
        disable()
    }
}