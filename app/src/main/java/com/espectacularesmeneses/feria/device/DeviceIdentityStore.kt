package com.espectacularesmeneses.feria.device

import android.content.Context

data class DeviceIdentity(
    val deviceId: String,
    val deviceCode: String,
    val deviceName: String,
    val deviceToken: String
)

object DeviceIdentityStore {

    private const val PREFERENCES_NAME =
        "meneses_device_identity"

    private const val KEY_DEVICE_ID =
        "device_id"

    private const val KEY_DEVICE_CODE =
        "device_code"

    private const val KEY_DEVICE_NAME =
        "device_name"

    private const val KEY_DEVICE_TOKEN =
        "device_token"

    private fun preferences(
        context: Context
    ) =
        context.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

    fun getIdentity(
        context: Context
    ): DeviceIdentity? {

        val preferences =
            preferences(context)

        val deviceId =
            preferences.getString(
                KEY_DEVICE_ID,
                null
            )

        val deviceCode =
            preferences.getString(
                KEY_DEVICE_CODE,
                null
            )

        val deviceName =
            preferences.getString(
                KEY_DEVICE_NAME,
                null
            )

        val deviceToken =
            preferences.getString(
                KEY_DEVICE_TOKEN,
                null
            )

        if (
            deviceId.isNullOrBlank() ||
            deviceCode.isNullOrBlank() ||
            deviceName.isNullOrBlank() ||
            deviceToken.isNullOrBlank()
        ) {
            return null
        }

        return DeviceIdentity(
            deviceId = deviceId,
            deviceCode = deviceCode,
            deviceName = deviceName,
            deviceToken = deviceToken
        )
    }

    fun saveIdentity(
        context: Context,
        identity: DeviceIdentity
    ) {

        preferences(context)
            .edit()
            .putString(
                KEY_DEVICE_ID,
                identity.deviceId
            )
            .putString(
                KEY_DEVICE_CODE,
                identity.deviceCode
            )
            .putString(
                KEY_DEVICE_NAME,
                identity.deviceName
            )
            .putString(
                KEY_DEVICE_TOKEN,
                identity.deviceToken
            )
            .apply()
    }

    fun clearIdentity(
        context: Context
    ) {

        preferences(context)
            .edit()
            .clear()
            .apply()
    }

    fun isProvisioned(
        context: Context
    ): Boolean {

        return getIdentity(context) != null
    }

    fun getDeviceCode(
        context: Context
    ): String? {

        return getIdentity(context)
            ?.deviceCode
    }

    fun getDeviceToken(
        context: Context
    ): String? {

        return getIdentity(context)
            ?.deviceToken
    }
}