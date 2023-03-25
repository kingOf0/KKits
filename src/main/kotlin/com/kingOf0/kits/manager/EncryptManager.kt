package com.kingOf0.kits.manager

import java.io.IOException
import java.util.*
import kotlin.experimental.xor

object EncryptManager {

    private val decoder: Base64.Decoder = Base64.getDecoder()
    private val encoder: Base64.Encoder = Base64.getEncoder()

    const val uuid = "b9307f84-ff2c-4634-a70f-040a5f0609e1"
    val binarySecret = decode("UgkCAQdWCQQdVlcDUx0EBwMEHFAHAFccAAUBUQVXAAcACFQBUgkCAAdXCQQdVlcCUx0FBwMEHFEHAFccAQQAUAVWAQYBCFUAUggCAAdXCQQdVlcDUx0FBgMEHFAHAVYdAAQBUAVXAQcACVQBUwgDAQdWCQUdV1YCUx0FBwMEHVEHAFccAQQAUQVWAQcACFUBUgkCAAZXCAUdV1cCUx0FBgMEHFAHAFcdAAUBUQVXAAYACFQBUggDAAdWCQQcV1YDUxwFBgMFHVEHAFccAAQBUAVXAQYACFQBUggCAAdWCAUdV1cCUxwEBwMFHFEHAVYdAAQBUAVWAQcACFQBUgkCAQdXCQQdV1YDUxwFBgMEHFAHAFccAAUAUQVXAQYACVUA", uuid)

    fun encode(s: String, key: String): String {
        return base64Encode(xor(s.toByteArray(), key.toByteArray()))
    }

    fun decode(s: String, key: String): String {
        return String(xor(base64Decode(s), key.toByteArray()))
    }

    private fun xor(a: ByteArray, key: ByteArray): ByteArray {
        val out = ByteArray(a.size)
        for (i in a.indices) {
            out[i] = (a[i] xor key[i % key.size])
        }
        return out
    }

    private fun base64Decode(s: String): ByteArray {
        return try {
            decoder.decode(s)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun base64Encode(bytes: ByteArray): String {
        return encoder.encodeToString(bytes).replace("\\s", "")
    }

    fun toBinary(s: String): String {
        val bytes = s.toByteArray()
        val binary = StringBuilder()
        for (b in bytes) {
            var j = b.toInt()
            for (i in 0..7) {
                binary.append(if (j and 128 == 0) 0 else 1)
                j = j shl 1
            }
        }
        return binary.toString()
    }

    fun fromBinary(binary: String): String {
        val bytes = ByteArray(binary.length / 8)
        var i = 0
        var j = 0
        while (i < binary.length) {
            var k = 0
            for (l in 0..7) {
                k = k shl 1
                k = k or (binary[i + l] - '0')
            }
            bytes[j] = k.toByte()
            i += 8
            j++
        }
        return String(bytes)
    }


}