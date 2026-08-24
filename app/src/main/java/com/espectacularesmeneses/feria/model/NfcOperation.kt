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
     * Recarga normal realizada por una TAQUILLA.
     */
    data class Recharge(
        val amount: Long
    ) :
        NfcOperation()

    /*
     * Recarga promocional realizada por una TAQUILLA.
     *
     * El backend sigue siendo la autoridad del monto acreditado.
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
