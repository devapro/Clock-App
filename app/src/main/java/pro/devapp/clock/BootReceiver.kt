package pro.devapp.clock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pro.devapp.clock.services.NoiseDetectorService
import pro.devapp.clock.utils.ServiceUtils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            val serviceIntent = Intent(context, NoiseDetectorService::class.java)
            if (context != null && !ServiceUtils.isServiceRunning(context, NoiseDetectorService::class.java)){
                context.startService(serviceIntent)
            }
        }
    }
}