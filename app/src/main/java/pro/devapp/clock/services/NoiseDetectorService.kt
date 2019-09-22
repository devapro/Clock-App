package pro.devapp.clock.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.media.MediaRecorder
import android.os.Handler
import android.os.PowerManager
import android.util.Log
import java.io.IOException
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import pro.devapp.clock.ClockApp
import pro.devapp.clock.MainActivity
import pro.devapp.clock.R
import android.app.Activity
import android.app.KeyguardManager
import android.view.Display
import android.hardware.display.DisplayManager
import android.os.Build


class NoiseDetectorService : Service() {
    private val EMA_FILTER = 0.6

    private var mRecorder: MediaRecorder? = null
    private var mEMA = 0.0

    private var wakeLock: PowerManager.WakeLock? = null
    private val handler: Handler = Handler()
    private val noiseDetectorRunnuble = Runnable { run { detectNoise() } }
    private var lock: KeyguardManager.KeyguardLock? = null
    private var keyguardManager: KeyguardManager? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder?.setOutputFile("/dev/null")

        try {
            mRecorder?.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mEMA = 0.0

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "clockApp:WakeLock")
        keyguardManager = (getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager)
        lock =
            (getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager).newKeyguardLock(Context.KEYGUARD_SERVICE)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        mRecorder?.start()
        handler.postDelayed(noiseDetectorRunnuble, 500)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mRecorder?.stop()
        mRecorder?.release()
        mRecorder = null
        handler.removeCallbacks(noiseDetectorRunnuble)
        wakeLock!!.release()
    }

    private fun getAmplitude(): Double {
        return if (mRecorder != null)
            mRecorder!!.getMaxAmplitude() / 2700.0
        else
            0.0

    }

    private fun getAmplitudeEMA(): Double {
        val amp = getAmplitude()
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA
        return mEMA
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val customRemoteViews = RemoteViews(getPackageName(), R.layout.notification_noise_detector)

        return NotificationCompat.Builder(this, ClockApp.CHANNEL_ID)
            .setContentTitle(applicationContext.resources.getString(R.string.notification_noise_detector))
            .setSmallIcon(R.drawable.ic_mic_notification)
            .setCustomContentView(customRemoteViews)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(Notification.DEFAULT_ALL)
            .setSound(null)
            .build()
    }

    private fun detectNoise(){
        if (getAmplitudeEMA() > 3.0){
            Toast.makeText(applicationContext, "Detect noise!", Toast.LENGTH_SHORT).show()
            if (!isScreenOn(applicationContext)){
                if (!wakeLock!!.isHeld()) {
                    wakeLock!!.acquire(5 * 60 * 1000)
                }
                lock!!.disableKeyguard()
                handler.postDelayed(noiseDetectorRunnuble, 3000)
            } else {
                handler.postDelayed(noiseDetectorRunnuble, 1000)
            }
        } else {
            handler.postDelayed(noiseDetectorRunnuble, 500)
        }
    }

    private fun isScreenOn(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            val dm = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            var screenOn = false
            for (display in dm.displays) {
                if (display.state != Display.STATE_OFF) {
                    screenOn = true
                }
            }
            return screenOn
        } else {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager

            return pm.isScreenOn
        }
    }
}