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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.view.View
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import pro.devapp.clock.di.TimerModule
import pro.devapp.clock.utils.ClockSounds
import javax.inject.Inject
import kotlin.math.abs


class TimerService: Service() {
    private var lastUpdatedTime: Long = 0
    private val timerFormatter = SimpleDateFormat("mm:ss")
    private val handler = Handler()
    private val updateTimeRunnable: Runnable = Runnable { run { updateCurrentTime() } }

    private lateinit var stopReceiver: BroadcastReceiver
    private lateinit var pauseReceiver: BroadcastReceiver
    private lateinit var startReceiver: BroadcastReceiver

    private lateinit var pendingIntentPause: PendingIntent
    private lateinit var pendingIntentStop: PendingIntent
    private lateinit var pendingIntentStart: PendingIntent

    @Inject
    lateinit var clockSounds: ClockSounds

    @Inject
    lateinit var timerModule: TimerModule

    override fun onCreate() {
        super.onCreate()
        (application as ClockApp).appComponent.inject(this)
        pauseReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                timerModule.isRunning.value = false
                updateNotification()
            }
        }
        startReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                timerModule.isRunning.value = true
                updateNotification()
            }
        }
        stopReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                timerModule.isRunning.value = false
                timerModule.currentTimerValue.value = timerModule.timerValue.value
                this@TimerService.stopForeground(true)
            }
        }

        val pauseIntent = Intent()
        pauseIntent.action = TimerService::class.java.name + "pause"
        pendingIntentPause = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val stopIntent = Intent()
        stopIntent.action = TimerService::class.java.name + "stop"
        pendingIntentStop = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val startIntent = Intent()
        startIntent.action = TimerService::class.java.name + "start"
        pendingIntentStart = PendingIntent.getBroadcast(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        registerReceiver(pauseReceiver, IntentFilter(TimerService::class.java.name + "pause"))
        registerReceiver(stopReceiver, IntentFilter(TimerService::class.java.name + "stop"))
        registerReceiver(startReceiver, IntentFilter(TimerService::class.java.name + "start"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        handler.postDelayed(updateTimeRunnable, 500)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable)
        clockSounds.stopAll()
        unregisterReceiver(pauseReceiver)
        unregisterReceiver(stopReceiver)
        unregisterReceiver(startReceiver)
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

        val customRemoteViews = RemoteViews(getPackageName(), R.layout.notification_timer)
        customRemoteViews.setTextViewText(R.id.time, timerFormatter.format(Date(abs(currentTimerValue))))

        customRemoteViews.setOnClickPendingIntent(R.id.stop, pendingIntentStop)
        if(timerModule.isRunning.value!!){
            customRemoteViews.setOnClickPendingIntent(R.id.pause, pendingIntentPause)
            customRemoteViews.setImageViewResource(R.id.pause, R.drawable.ic_pause)

            customRemoteViews.setViewVisibility(R.id.stop, View.VISIBLE)
        } else{
            customRemoteViews.setOnClickPendingIntent(R.id.pause, pendingIntentStart)
            customRemoteViews.setImageViewResource(R.id.pause, R.drawable.ic_play)

            customRemoteViews.setViewVisibility(R.id.stop, View.INVISIBLE)
        }


        return NotificationCompat.Builder(this, ClockApp.CHANNEL_ID)
            .setContentTitle(if (currentTimerValue > 0) applicationContext.resources.getString(R.string.notification_timer_running) else applicationContext.resources.getString(R.string.notification_timer_end))
            .setContentText(timerFormatter.format(Date(abs(currentTimerValue))))
            .setSmallIcon(R.drawable.ic_timer_notification)
            .setCustomContentView(customRemoteViews)
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
        val cal = Calendar.getInstance()
        if (timerModule.isRunning.value!!){
            var currentTimerValue = timerModule.currentTimerValue.value
            if(currentTimerValue == null){
                currentTimerValue = 0
            }
            val diffTime = if (lastUpdatedTime > 0) cal.getTime().time - lastUpdatedTime else 0
            timerModule.currentTimerValue.value = currentTimerValue - diffTime
            if (currentTimerValue <= 0){
                clockSounds.playSound(this, R.raw.timer_end)
            }
            updateNotification()
        }
        lastUpdatedTime = cal.getTime().time
        handler.postDelayed(updateTimeRunnable, 500)
    }

    inner class LocalBinder : Binder() {
        val serviceInstance: TimerService
            get() = this@TimerService
    }
}