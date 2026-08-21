package com.espectacularesmeneses.feria.device


object DeviceRuntimeIdentity {

    @Volatile
    private var currentIdentity:
            DeviceIdentity? =
        null


    fun setIdentity(
        identity: DeviceIdentity
    ) {

        currentIdentity =
            identity
    }


    fun clear() {

        currentIdentity =
            null
    }


    fun getIdentity():
            DeviceIdentity? {

        return currentIdentity
    }


    fun requireIdentity():
            DeviceIdentity {

        return currentIdentity
            ?: throw IllegalStateException(
                "DEVICE_IDENTITY_NOT_INITIALIZED"
            )
    }


    fun deviceId():
            String {

        return requireIdentity()
            .deviceId
    }


    fun deviceCode():
            String {

        return requireIdentity()
            .deviceCode
    }


    fun deviceName():
            String {

        return requireIdentity()
            .deviceName
    }


    fun deviceToken():
            String {

        return requireIdentity()
            .deviceToken
    }


    fun isInitialized():
            Boolean {

        return currentIdentity != null
    }
}