package pro.devapp.clock.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import pro.devapp.clock.ClockApp
import pro.devapp.clock.MainActivity
import pro.devapp.clock.R
import android.os.Binder
import android.os.Handler
import java.text.SimpleDateFormat
import java.util.*
import android.app.NotificationManager
import android.content.Context
import pro.devapp.clock.di.TimerModule
import pro.devapp.clock.utils.ClockSounds
import javax.inject.Inject
import kotlin.math.abs


class TimerService: Service() {
    private var lastUpdatedTime: Long = 0
    private val timerFormatter = SimpleDateFormat("mm:ss")
    private val handler = Handler()
    private val updateTimeRunnable: Runnable = Runnable { run { updateCurrentTime() } }

    @Inject
    lateinit var clockSounds: ClockSounds

    @Inject
    lateinit var timerModule: TimerModule

    override fun onCreate() {
        super.onCreate()
        (application as ClockApp).appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        handler.postDelayed(updateTimeRunnable, 500)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        clockSounds.stopAll()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return LocalBinder()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("openTab", 2)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        var currentTimerValue = timerModule.currentTimerValue.value
        if(currentTimerValue == null){
            currentTimerValue = 0
        }

        return NotificationCompat.Builder(this, ClockApp.CHANNEL_ID)
            .setContentTitle(if (currentTimerValue > 0) applicationContext.resources.getString(R.string.notification_timer_running) else applicationContext.resources.getString(R.string.notification_timer_end))
            .setContentText(timerFormatter.format(Date(abs(currentTimerValue))))
            .setSmallIcon(R.drawable.ic_timer_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(null)
            .build()
    }

    private fun updateNotification() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, createNotification())
    }

    private fun updateCurrentTime(){
        var currentTimerValue = timerModule.currentTimerValue.value
        if(currentTimerValue == null){
            currentTimerValue = 0
        }
        val cal = Calendar.getInstance()
        val diffTime = if (lastUpdatedTime > 0) cal.getTime().time - lastUpdatedTime else 0
        lastUpdatedTime = cal.getTime().time
        timerModule.currentTimerValue.value = currentTimerValue - diffTime
        if (currentTimerValue <= 0){
            clockSounds.playSound(this, R.raw.timer_end)
        }
        updateNotification()
        handler.postDelayed(updateTimeRunnable, 500)
    }

    inner class LocalBinder : Binder() {
        val serviceInstance: TimerService
            get() = this@TimerService
    }
}