package com.example.myapplication.di

import android.app.Application
import com.example.myapplication.data.database.DatabaseBuilderFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class RechnungsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RechnungsApp)
            modules(
                module { single { DatabaseBuilderFactory(androidContext()) } },
                appModule
            )
        }
    }
}