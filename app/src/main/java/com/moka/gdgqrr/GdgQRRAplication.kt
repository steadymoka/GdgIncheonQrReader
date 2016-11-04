package com.moka.gdgqrr

import android.app.Application
import android.content.Context

class GdgQRRAplication : Application() {

    companion object {
        lateinit var context: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

}