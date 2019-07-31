package pro.devapp.clock.viewModels

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class TimerViewModel: ViewModel() {
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")
    private val secondsFormatter = SimpleDateFormat("ss")
    private val minutesFormatter = SimpleDateFormat("mm")

    private var timerValue = 60

    private val handler = Handler()

    private val currentTime: MutableLiveData<Date> = MutableLiveData()

    private val updateTimeRunnable: Runnable = Runnable { run { updateCurrentTime() } }

    init {
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    private fun updateCurrentTime(){
        val cal = Calendar.getInstance()
        currentTime.value = cal.getTime()
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    fun getCurrentTime(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return timeFormatter.format(input)
        })
    }

    fun getTimerSeconds(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return secondsFormatter.format(input)
        })
    }

    fun getTimerMinutes(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return minutesFormatter.format(input)
        })
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateTimeRunnable)
    }
}