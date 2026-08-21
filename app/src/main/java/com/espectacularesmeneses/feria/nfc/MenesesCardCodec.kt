package com.espectacularesmeneses.feria.nfc

import com.espectacularesmeneses.feria.model.CardStatus
import com.espectacularesmeneses.feria.model.CardType
import com.espectacularesmeneses.feria.model.MenesesCard
import com.espectacularesmeneses.feria.util.ByteUtils

object MenesesCardCodec {

    const val CARD_DATA_SIZE = 32

    private val MAGIC = byteArrayOf(
        0x4D, // M
        0x45, // E
        0x4E, // N
        0x31  // 1
    )

    fun encode(card: MenesesCard): ByteArray {

        val data = ByteArray(CARD_DATA_SIZE)

        /*
         * Bytes 0 - 3
         * MEN1
         */
        MAGIC.copyInto(
            destination = data,
            destinationOffset = 0
        )

        /*
         * Byte 4
         * Versión del protocolo
         */
        data[4] = card.version.toByte()

        /*
         * Byte 5
         * Tipo de tarjeta
         */
        data[5] = card.type.code.toByte()

        /*
         * Byte 6
         * Estado
         */
        data[6] = card.status.code.toByte()

        /*
         * Byte 7
         * Flags.
         *
         * Reservado para futuras versiones.
         */
        data[7] = 0x00

        /*
         * Bytes 8 - 15
         * Card ID
         */
        ByteUtils.longToBytes(
            card.cardId
        ).copyInto(
            destination = data,
            destinationOffset = 8
        )

        /*
         * Bytes 16 - 23
         * Saldo en pesos ENTEROS.
         */
        ByteUtils.longToBytes(
            card.balance
        ).copyInto(
            destination = data,
            destinationOffset = 16
        )

        /*
         * Bytes 24 - 31
         * Contador de transacciones.
         */
        ByteUtils.longToBytes(
            card.transactionCounter
        ).copyInto(
            destination = data,
            destinationOffset = 24
        )

        return data
    }

    fun decode(data: ByteArray): MenesesCard {

        require(data.size >= CARD_DATA_SIZE) {
            "Los datos de la tarjeta están incompletos."
        }

        /*
         * Validamos MEN1.
         */
        val receivedMagic =
            data.copyOfRange(0, 4)

        require(
            receivedMagic.contentEquals(MAGIC)
        ) {
            "La tarjeta no utiliza el formato Meneses Card."
        }

        /*
         * Versión
         */
        val version =
            data[4].toInt() and 0xFF

        require(version == 1) {
            "Versión de tarjeta no soportada: $version"
        }

        /*
         * Tipo
         */
        val typeCode =
            data[5].toInt() and 0xFF

        val type =
            CardType.fromCode(typeCode)
                ?: throw IllegalArgumentException(
                    "Tipo de tarjeta desconocido: $typeCode"
                )

        /*
         * Estado
         */
        val statusCode =
            data[6].toInt() and 0xFF

        val status =
            CardStatus.fromCode(statusCode)
                ?: throw IllegalArgumentException(
                    "Estado de tarjeta desconocido: $statusCode"
                )

        /*
         * Card ID
         */
        val cardId =
            ByteUtils.bytesToLong(
                data.copyOfRange(
                    8,
                    16
                )
            )

        /*
         * Saldo
         */
        val balance =
            ByteUtils.bytesToLong(
                data.copyOfRange(
                    16,
                    24
                )
            )

        /*
         * Transaction Counter
         */
        val transactionCounter =
            ByteUtils.bytesToLong(
                data.copyOfRange(
                    24,
                    32
                )
            )

        return MenesesCard(
            version = version,
            type = type,
            status = status,
            cardId = cardId,
            balance = balance,
            transactionCounter = transactionCounter
        )
    }

    fun isMenesesCard(data: ByteArray): Boolean {

        if (data.size < 4) {
            return false
        }

        return data
            .copyOfRange(0, 4)
            .contentEquals(MAGIC)
    }
}