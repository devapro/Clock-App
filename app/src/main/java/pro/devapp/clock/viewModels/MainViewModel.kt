package pro.devapp.clock.viewModels

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import pro.devapp.clock.fragments.ClockFragment
import pro.devapp.clock.fragments.MirrorFragment
import pro.devapp.clock.fragments.TimerFragment
import java.util.*

class MainViewModel : ViewModel() {
    companion object{
        const val REQUEST_MIC_PERMISSION = 0
    }

    private var fragmentChangeListener: OnFragmentChangeListener? = null
    private var currentTab : MutableLiveData<Int> = MutableLiveData()

    init {
        currentTab.value = 1
    }

    fun setListener(listener: OnFragmentChangeListener?) {
        fragmentChangeListener = listener
        setActiveTab(currentTab.value!!)
    }

    fun setActiveTab(index: Int) {
        currentTab.value = index
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

    fun isActiveTab(index: Int): LiveData<Boolean> {
        return Transformations.map(currentTab, fun(input: Int): Boolean? {
            return currentTab.value == index
        })
    }

    interface OnFragmentChangeListener {
        fun replaceFragment(fragmentClass: Class<*>)
    }
}