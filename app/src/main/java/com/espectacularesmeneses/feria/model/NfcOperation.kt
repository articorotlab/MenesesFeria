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
     * Recarga realizada por una TAQUILLA.
     */
    data class Recharge(
        val amount: Long
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