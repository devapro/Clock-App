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

    private var timerValue: Long = 10*60*1000
    private var currentTimerValue: Long = 10*60*1000
    private var isRunning: MutableLiveData<Boolean> = MutableLiveData()

    private val handler = Handler()

    private val currentTime: MutableLiveData<Date> = MutableLiveData()

    private val updateTimeRunnable: Runnable = Runnable { run { updateCurrentTime() } }

    init {
        handler.postDelayed(updateTimeRunnable, 1000)
        isRunning.value = false
        val cal = Calendar.getInstance()
        currentTime.value = cal.getTime()
    }

    private fun updateCurrentTime(){
        val cal = Calendar.getInstance()
        val diffTime = cal.getTime().time - currentTime.value!!.time
        currentTime.value = cal.getTime()
        if (isRunning.value!!) {
            currentTimerValue -= diffTime
        }
        handler.postDelayed(updateTimeRunnable, 1000)
    }

    fun getCurrentTime(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return timeFormatter.format(input)
        })
    }

    fun getTimerSeconds(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return secondsFormatter.format(Date(currentTimerValue))
        })
    }

    fun getTimerMinutes(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return minutesFormatter.format(Date(currentTimerValue))
        })
    }

    fun getShowTick(): LiveData<Boolean> {
        return Transformations.map(currentTime, fun(input: Date): Boolean? {
            return input.seconds % 2 == 0 || !isRunning.value!!
        })
    }

    fun getShowTime(): LiveData<Boolean> {
        return Transformations.map(currentTime, fun(input: Date): Boolean? {
            return input.seconds % 2 == 0 || isRunning.value!!
        })
    }

    fun getIsRunning(): LiveData<Boolean> {
        return isRunning
    }

    fun changeSeconds(seconds: Int){
        timerValue += seconds * 1000
        currentTimerValue = timerValue
        val cal = Calendar.getInstance()
        currentTime.value = cal.getTime()
    }

    fun changeMinutes(minutes: Int) {
        timerValue += minutes * 60 * 1000
        currentTimerValue = timerValue
        val cal = Calendar.getInstance()
        currentTime.value = cal.getTime()
    }

    fun pauseTimer(){
        isRunning.value = false
    }

    fun startTimer(){
        isRunning.value = true
    }

    fun stopTimer(){
        isRunning.value = false
        currentTimerValue = timerValue
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateTimeRunnable)
    }
}