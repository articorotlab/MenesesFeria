package com.espectacularesmeneses.feria.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

object ByteUtils {

    fun longToBytes(value: Long): ByteArray {

        return ByteBuffer
            .allocate(Long.SIZE_BYTES)
            .order(ByteOrder.BIG_ENDIAN)
            .putLong(value)
            .array()
    }

    fun bytesToLong(bytes: ByteArray): Long {

        require(bytes.size == Long.SIZE_BYTES) {
            "Se requieren exactamente 8 bytes para convertir a Long."
        }

        return ByteBuffer
            .wrap(bytes)
            .order(ByteOrder.BIG_ENDIAN)
            .long
    }

    fun bytesToHex(bytes: ByteArray): String {

        return bytes.joinToString(" ") {
            "%02X".format(it.toInt() and 0xFF)
        }
    }
}