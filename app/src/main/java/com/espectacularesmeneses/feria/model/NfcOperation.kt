package com.espectacularesmeneses.feria.model

sealed class NfcOperation {

    data object Read :
        NfcOperation()

    data object InitializeDevCustomerCard :
        NfcOperation()

    data object InitializeDevGameCard :
        NfcOperation()

    data object InitializeDevRechargeCard :
        NfcOperation()

    data object InitializeDevAdminCard :
        NfcOperation()

    data object CreateCustomerCard :
        NfcOperation()

    data class CreateGameCard(
        val gameId: String,
        val gameName: String,
        val gamePrice: Long
    ) :
        NfcOperation()

    data class CreateRechargeCard(
        val rechargePointId: String,
        val rechargePointName: String
    ) :
        NfcOperation()

    data object ConsultBalance :
        NfcOperation()

    data object ConsultHistory :
        NfcOperation()

    /*
     * Devolución / reset de una CUSTOMER desde TAQUILLA.
     *
     * El backend autoriza primero la devolución.
     * Después Android escribe:
     *
     * - status = INACTIVE
     * - balance = 0
     * - transactionCounter = 0
     *
     * Finalmente Android relee la NFC y confirma
     * la operación en el servidor.
     */
    data object ReturnCustomerCard :
        NfcOperation()

    /*
     * Recarga normal legacy realizada por una TAQUILLA.
     * Se conserva temporalmente mientras terminamos de retirar
     * rutas legacy de recuperación anteriores.
     */
    data class Recharge(
        val amount: Long
    ) :
        NfcOperation()

    /*
     * Recarga TAQUILLA mediante recharge_checkout.
     *
     * paymentMethod:
     * - CASH = efectivo
     * - CARD = tarjeta bancaria
     *
     * Cuando promotionId != null, la misma operación representa
     * una recarga promocional. El backend sigue siendo autoridad
     * de paid_recharge_amount, promotional_credit_amount y credited.
     *
     * promotionName se conserva únicamente para presentación en UI.
     */
    data class CheckoutRecharge(
        val amount: Long,
        val paymentMethod: String,
        val promotionId: String? = null,
        val promotionName: String? = null
    ) :
        NfcOperation()

    /*
     * Flujo promocional legacy.
     *
     * Se conserva temporalmente por compatibilidad mientras se
     * eliminan referencias históricas. Las nuevas promociones de
     * TAQUILLA se procesan mediante CheckoutRecharge.
     */
    data class PromotionalRecharge(
        val promotionId: String,
        val promotionName: String,
        val cashAmount: Long,
        val promotionalAmount: Long,
        val totalCreditAmount: Long
    ) :
        NfcOperation()

    /*
     * Cobro realizado por un JUEGO.
     */
    data class Charge(
        val peopleCount: Int
    ) :
        NfcOperation()

    /*
     * Recarga realizada desde ADMIN.
     */
    data class AdminRecharge(
        val amount: Long
    ) :
        NfcOperation()

    /*
     * Retiro de saldo realizado desde ADMIN.
     *
     * Backend:
     * transaction_type = ADJUSTMENT
     */
    data class AdminAdjustment(
        val amount: Long
    ) :
        NfcOperation()
}
