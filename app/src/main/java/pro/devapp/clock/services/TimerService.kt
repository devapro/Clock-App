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
import kotlin.math.abs


class TimerService: Service() {
    private var currentTimerValue: Long = -1
    private var currentTime: Long = 0
    private val timerFormatter = SimpleDateFormat("mm:ss")
    private val handler = Handler()
    private val updateTimeRunnable: Runnable = Runnable { run { updateCurrentTime() } }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentTimerValue = intent!!.getLongExtra("interval", 0)
        if (currentTimerValue < 1) {
            stopSelf()
        }
        startForeground(1, createNotification())
        handler.postDelayed(updateTimeRunnable, 1000)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return LocalBinder()
    }

    fun getCurrentTimerValue(): Long {
        return currentTimerValue
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("openTab", 2)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        return NotificationCompat.Builder(this, ClockApp.CHANNEL_ID)
            .setContentTitle(if (currentTimerValue > 0) "Timer running" else "Timer end!")
            .setContentText(timerFormatter.format(Date(abs(currentTimerValue))))
            .setSmallIcon(R.drawable.ic_timer_notification)
            .setContentIntent(pendingIntent)
            // TODO set sound
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(1, createNotification())
    }

    private fun updateCurrentTime(){
        val cal = Calendar.getInstance()
        val diffTime = if (currentTime > 0) cal.getTime().time - currentTime else 0
        currentTime = cal.getTime().time
        currentTimerValue -= diffTime
        if (currentTimerValue <= 0){
            //TODO
        }
        updateNotification()
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    inner class LocalBinder : Binder() {
        val serviceInstance: TimerService
            get() = this@TimerService
    }
}