package com.schmitttech.ingresso

import android.app.Application
import com.schmitttech.ingresso.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Main Application class for the Ingresso app.
 * Initializes the Koin dependency injection framework.
 */
class IngressoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@IngressoApplication)
            modules(appModule)
        }
    }
}
