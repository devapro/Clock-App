package pro.devapp.clock.viewModels

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class TimerViewModel: ViewModel() {
    //TODO get current timer time from service, remove duplicate code for timer
    private val timeFormatter = SimpleDateFormat("HH:mm:ss")
    private val secondsFormatter = SimpleDateFormat("ss")
    private val minutesFormatter = SimpleDateFormat("mm")

    private var timerValue: Long = 10 * 60 * 1000
    private var currentTimerValue: Long = timerValue
    private var isRunning: MutableLiveData<Boolean> = MutableLiveData()

    private val handler = Handler()

    private val currentTime: MutableLiveData<Date> = MutableLiveData()

    private val updateTimeRunnable: Runnable = Runnable { run { updateCurrentTime() } }

    private var timerListener: TimerListener? = null

    init {
        handler.postDelayed(updateTimeRunnable, 1000)
        isRunning.value = false
        val cal = Calendar.getInstance()
        currentTime.value = cal.getTime()
    }

    fun setTimerListener(listener: TimerListener?) {
        timerListener = listener
    }

    private fun updateCurrentTime(){
        val cal = Calendar.getInstance()
        val diffTime = if (currentTime.value!!.time > 0) cal.getTime().time - currentTime.value!!.time else 0
        currentTime.value = cal.getTime()
        if (isRunning.value!!) {
            currentTimerValue -= diffTime
            if (currentTimerValue <= 0){
                ///
            }
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
            return secondsFormatter.format(Date(abs(currentTimerValue)))
        })
    }

    fun getTimerMinutes(): LiveData<String> {
        return Transformations.map(currentTime, fun(input: Date): String? {
            return minutesFormatter.format(Date(abs(currentTimerValue)))
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

    fun getIsEnd(): LiveData<Boolean> {
        return Transformations.map(currentTime, fun(input: Date): Boolean? {
            return currentTimerValue <= 0
        })
    }

    fun changeSeconds(seconds: Int){
        timerValue += seconds * 1000
        currentTimerValue = timerValue
        val cal = Calendar.getInstance()
        currentTime.value = cal.getTime()
        if (isRunning.value!!) {
            timerListener?.onStartTimer(currentTimerValue)
        }
    }

    fun changeMinutes(minutes: Int) {
        timerValue += minutes * 60 * 1000
        currentTimerValue = timerValue
        val cal = Calendar.getInstance()
        currentTime.value = cal.getTime()
        if (isRunning.value!!) {
            timerListener?.onStartTimer(currentTimerValue)
        }
    }

    fun pauseTimer(){
        isRunning.value = false
        timerListener?.onStopTimer()
    }

    fun startTimer(){
        isRunning.value = true
        timerListener?.onStartTimer(currentTimerValue)
    }

    fun stopTimer(){
        isRunning.value = false
        currentTimerValue = timerValue
        timerListener?.onStopTimer()
    }

    fun updateCurrentTimerValueAndStart(interval: Long?) {
        if (interval != null && interval > 0) {
            currentTimerValue = interval
            isRunning.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateTimeRunnable)
    }

    interface TimerListener{
        fun onStartTimer(interval: Long)
        fun onStopTimer()
    }
}