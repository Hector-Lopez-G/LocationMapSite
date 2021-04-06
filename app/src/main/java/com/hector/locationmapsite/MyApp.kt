package com.hector.locationmapsite

import android.app.Application
import com.huawei.agconnect.config.AGConnectServicesConfig
import com.huawei.hms.maps.MapsInitializer

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        // Please replace Your API key with the API key in
        // agconnect-services.json.
        val apiKey = AGConnectServicesConfig.fromContext(this).getString("client/api_key")
        MapsInitializer.setApiKey(apiKey)
    }
}