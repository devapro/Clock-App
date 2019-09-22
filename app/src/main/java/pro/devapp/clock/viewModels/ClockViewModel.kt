package pro.devapp.clock.viewModels

import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import pro.devapp.clock.fragments.SettingsFragment
import java.text.SimpleDateFormat
import java.util.*

class ClockViewModel : ViewModel() {
    private val timeFormatter = SimpleDateFormat("HH:mm")
    private val secondsFormatter = SimpleDateFormat("ss")
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd")

    private val handler = Handler()

    private val currentTime: MutableLiveData<Date> = MutableLiveData()

    private var changeFragmentListener: FragmentChangeListener? = null

    private val updateTimeRunnable: Runnable = Runnable { run { updateCurrentTime() } }

    init {
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    fun setChangeFragmentListener(listener: FragmentChangeListener?){
        changeFragmentListener = listener
    }

    fun getCurrentTime(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return timeFormatter.format(input)
        })
    }

    fun getCurrentSeconds(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return secondsFormatter.format(input)
        })
    }

    fun getCurrentDate(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return dateFormatter.format(input)
        })
    }

    fun openSettings() {
        changeFragmentListener?.replaceFragment(SettingsFragment())
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun updateCurrentTime(){
        val cal = Calendar.getInstance()
        currentTime.value = cal.getTime()
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    interface FragmentChangeListener{
        fun replaceFragment(fragment : Fragment)
    }
}