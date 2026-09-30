package com.example.myapplication.di

import com.example.myapplication.data.database.DatabaseBuilderFactory
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        modules(
            module { single { DatabaseBuilderFactory() } },
            appModule
        )
    }
}