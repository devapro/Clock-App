package pro.devapp.clock

import android.app.Application
import pro.devapp.clock.di.ContextModule
import pro.devapp.clock.di.DaggerAppComponent

class ClockApp : Application()
{
    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .contextModule(ContextModule(applicationContext))
            .build()
    }
}