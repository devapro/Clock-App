package pro.devapp.clock.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import pro.devapp.clock.databinding.FragmentTimerBinding
import pro.devapp.clock.services.TimerService
import pro.devapp.clock.services.TimerService.LocalBinder
import pro.devapp.clock.viewModels.TimerViewModel
import android.app.ActivityManager


class TimerFragment: Fragment(), TimerViewModel.TimerListener {
    private var mBinding: FragmentTimerBinding? = null
    private var serviceIntent: Intent? = null
    private var timerService: TimerService? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, pro.devapp.clock.R.layout.fragment_timer, container,false)
        mBinding?.lifecycleOwner = this
        mBinding?.model = ViewModelProviders.of(activity!!).get(TimerViewModel::class.java)
        mBinding?.model?.setTimerListener(this)
        return  mBinding?.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        serviceIntent = Intent(context, TimerService::class.java)
        if (isServiceRunning(TimerService::class.java)){
            activity?.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)
        }
        mBinding?.model?.setTimerListener(this)
    }

    override fun onDetach() {
        super.onDetach()
        mBinding?.model?.setTimerListener(null)
        try {
            activity?.unbindService(mConnection)
        } catch (e: Exception) {

        }
    }

    override fun onStartTimer() {
        if (serviceIntent != null && context != null) {
            ContextCompat.startForegroundService(context!!, serviceIntent!!)
            activity?.bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStopTimer() {
        activity?.unbindService(mConnection)
        activity?.stopService(serviceIntent)
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
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

    private val mConnection: ServiceConnection = object: ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocalBinder
            timerService = binder.serviceInstance
        }

    }
}