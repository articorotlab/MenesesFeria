package com.espectacularesmeneses.feria


import android.app.Application

import com.espectacularesmeneses.feria.device.DeviceIdentityStore
import com.espectacularesmeneses.feria.device.DeviceRuntimeIdentity


class MenesesFeriaApplication :
    Application() {


    override fun onCreate() {

        super.onCreate()


        val identity =
            DeviceIdentityStore
                .getIdentity(
                    this
                )


        if (
            identity != null
        ) {

            DeviceRuntimeIdentity
                .setIdentity(
                    identity
                )

        } else {

            DeviceRuntimeIdentity
                .clear()
        }
    }
}