package com.savana

import android.app.Application
import com.savana.di.appMainModule
import com.savana.di.authenticationModule
import com.savana.di.localStorageModule
import com.savana.di.registrationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class Application: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@Application)
            modules(
                localStorageModule,
                authenticationModule,
                registrationModule,
                appMainModule
            )
        }
    }

}