package pro.devapp.clock.utils

import android.app.ActivityManager
import android.content.Context

class ServiceUtils {
    companion object{
        fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.className)) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
    }
}