package com.psssum.share

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex


class UILApplication : Application() {
    override fun onCreate() {
        super.onCreate()

    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}