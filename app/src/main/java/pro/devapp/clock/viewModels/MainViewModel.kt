package pro.devapp.clock.viewModels

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import pro.devapp.clock.fragments.ClockFragment
import pro.devapp.clock.fragments.MirrorFragment
import pro.devapp.clock.fragments.TimerFragment

class MainViewModel : ViewModel() {
    companion object{
        const val REQUEST_MIC_PERMISSION = 0
    }

    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private var currentTab = 0

    fun setListener(listener: OnFragmentChangeListener?) {
        fragmentChangeListener = listener
        setActiveTab(currentTab)
    }

    fun setActiveTab(index: Int) {
        currentTab = index
        if (fragmentChangeListener != null) {
            val fragmentClass: Class<*>
            when (index) {
                0 -> {
                    fragmentClass = MirrorFragment::class.java
                }
                1 -> {
                    fragmentClass = ClockFragment::class.java
                }
                2 -> {
                    fragmentClass = TimerFragment::class.java
                }
                else -> fragmentClass = ClockFragment::class.java
            }
            fragmentChangeListener!!.replaceFragment(fragmentClass)
        }
    }

    fun checkPermissionMic(activity: Activity): Boolean{
        val permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_MIC_PERMISSION)
            return false
        }
        return true
    }

    interface OnFragmentChangeListener {
        fun replaceFragment(fragmentClass: Class<*>)
    }
}