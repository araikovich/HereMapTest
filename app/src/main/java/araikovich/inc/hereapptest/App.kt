package araikovich.inc.hereapptest

import android.app.Application
import araikovich.inc.hereapptest.di.positionModule
import araikovich.inc.hereapptest.di.useCaseModule
import araikovich.inc.hereapptest.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    companion object {

        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@App)
            modules(
                listOf(viewModelsModule, positionModule, useCaseModule)
            )
        }
    }
}