package com.espectacularesmeneses.feria.nfc

import android.nfc.Tag
import android.nfc.tech.NfcA

object Ntag215Writer {

    const val START_PAGE = 4
    const val END_PAGE = 11

    const val PAGE_SIZE = 4
    const val TOTAL_BYTES = 32

    fun writeMenesesData(
        tag: Tag,
        data: ByteArray
    ) {

        require(data.size == TOTAL_BYTES) {
            "Meneses Card v1 requiere exactamente $TOTAL_BYTES bytes."
        }

        val nfcA =
            NfcA.get(tag)
                ?: throw IllegalArgumentException(
                    "La tarjeta no soporta NFC-A."
                )

        try {

            nfcA.connect()

            for (pageIndex in 0 until 8) {

                val pageNumber =
                    START_PAGE + pageIndex

                val start =
                    pageIndex * PAGE_SIZE

                val pageData =
                    data.copyOfRange(
                        start,
                        start + PAGE_SIZE
                    )

                writePage(
                    nfcA = nfcA,
                    page = pageNumber,
                    data = pageData
                )
            }

        } finally {

            try {
                nfcA.close()
            } catch (_: Exception) {
            }
        }
    }

    private fun writePage(
        nfcA: NfcA,
        page: Int,
        data: ByteArray
    ) {

        require(data.size == PAGE_SIZE) {
            "Una página NFC debe contener exactamente 4 bytes."
        }

        val command =
            byteArrayOf(
                0xA2.toByte(),
                page.toByte(),
                data[0],
                data[1],
                data[2],
                data[3]
            )

        nfcA.transceive(command)
    }
}