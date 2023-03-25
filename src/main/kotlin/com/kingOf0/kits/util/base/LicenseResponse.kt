package com.kingOf0.kits.util.base

import kotlinx.serialization.Serializable

@Serializable
internal data class LicenseResponse(
    val secret: String,
    val result: String,
    val message: String = ""
)

internal enum class State {
    SUCCESS,
    FAILURE
}