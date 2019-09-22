package pro.devapp.clock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import pro.devapp.clock.di.AppComponent
import pro.devapp.clock.di.ClockSoundModule
import pro.devapp.clock.di.ContextModule
import pro.devapp.clock.di.DaggerAppComponent

class ClockApp : Application()
{
    companion object{
        const val CHANNEL_ID = "timerNotificationChanel"
    }

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .clockSoundModule(ClockSoundModule(applicationContext))
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
            notificationChannel.setSound(null, null)
            notificationChannel.setShowBadge(false)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(notificationChannel)
        }
    }
}