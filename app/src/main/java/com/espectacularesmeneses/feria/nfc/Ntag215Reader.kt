package com.espectacularesmeneses.feria.nfc

import android.nfc.Tag
import android.nfc.tech.NfcA

object Ntag215Reader {

    /*
     * Meneses Card v1 ocupa:
     *
     * Página 04
     * hasta
     * Página 11
     *
     * 8 páginas × 4 bytes
     * =
     * 32 bytes
     */

    const val START_PAGE = 4
    const val END_PAGE = 11

    const val TOTAL_BYTES = 32

    /**
     * Lee las páginas 4-11 de una tarjeta NFC Type 2.
     *
     * Esta función es SOLO LECTURA.
     */
    fun readMenesesData(tag: Tag): ByteArray {

        val nfcA = NfcA.get(tag)
            ?: throw IllegalArgumentException(
                "La tarjeta no soporta NFC-A."
            )

        val result = ByteArray(TOTAL_BYTES)

        try {

            nfcA.connect()

            /*
             * READ 0x30 devuelve cuatro páginas.
             *
             * Empezamos en página 4:
             *
             * READ 04
             * -> páginas 4,5,6,7
             *
             * Después:
             *
             * READ 08
             * -> páginas 8,9,10,11
             */

            val firstBlock = readFourPages(
                nfcA = nfcA,
                startPage = 4
            )

            val secondBlock = readFourPages(
                nfcA = nfcA,
                startPage = 8
            )

            firstBlock.copyInto(
                destination = result,
                destinationOffset = 0
            )

            secondBlock.copyInto(
                destination = result,
                destinationOffset = 16
            )

            return result

        } finally {

            try {
                nfcA.close()
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Ejecuta:
     *
     * 30 XX
     *
     * donde 30 = READ
     * y XX = página inicial.
     *
     * Devuelve 16 bytes:
     * cuatro páginas consecutivas.
     */
    private fun readFourPages(
        nfcA: NfcA,
        startPage: Int
    ): ByteArray {

        val command = byteArrayOf(
            0x30.toByte(),
            startPage.toByte()
        )

        val response =
            nfcA.transceive(command)

        require(response.size == 16) {
            "Respuesta NFC inesperada. Se esperaban 16 bytes y llegaron ${response.size}."
        }

        return response
    }
}