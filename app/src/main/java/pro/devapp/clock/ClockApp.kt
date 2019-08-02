package pro.devapp.clock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import pro.devapp.clock.di.ContextModule
import pro.devapp.clock.di.DaggerAppComponent

class ClockApp : Application()
{
    companion object{
        const val CHANNEL_ID = "timerNotificationChanel"
    }
    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder()
            .contextModule(ContextModule(applicationContext))
            .build()

        createNotificationChanel()
    }

    private fun createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Timer",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}