package pro.devapp.clock.viewModels

import android.app.Application
import android.os.Handler
import androidx.annotation.NonNull
import androidx.lifecycle.*
import pro.devapp.clock.ClockApp
import pro.devapp.clock.di.TimerModule
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class TimerViewModel(@NonNull application: Application): AndroidViewModel(application) {
    init {
        (application as ClockApp).appComponent.inject(this)
    }

    private val timeFormatter = SimpleDateFormat("HH:mm:ss")
    private val secondsFormatter = SimpleDateFormat("ss")
    private val minutesFormatter = SimpleDateFormat("mm")

    private var isRunning: MutableLiveData<Boolean> = MutableLiveData()

    private val handler = Handler()

    private val showTickness: MutableLiveData<Boolean> = MutableLiveData()

    private val updateUiRunnable: Runnable = Runnable { run { updateUi() } }

    private var timerListener: TimerListener? = null

    @Inject
    lateinit var timerModule: TimerModule

    init {
        handler.postDelayed(updateUiRunnable, 500)
        isRunning.value = false
        showTickness.value = true
    }

    fun setTimerListener(listener: TimerListener?) {
        timerListener = listener
    }

    fun setTimerRunning(){
        isRunning.value = true
    }

    /**
     * For clock
     */
    fun getCurrentTime(): LiveData<String> {
        return Transformations.map(showTickness, fun(input: Boolean): String? {
            val date = Date()
            return timeFormatter.format(date)
        })
    }

    /**
     * For timer seconds
     */
    fun getTimerSeconds(): LiveData<String> {
        return Transformations.map(showTickness, fun(input: Boolean): String? {
            return secondsFormatter.format(Date(abs(timerModule.currentTimerValue.value ?: 0)))
        })
    }

    /**
     * For timer minutes
     */
    fun getTimerMinutes(): LiveData<String> {
        return Transformations.map(showTickness, fun(input: Boolean): String? {
            return minutesFormatter.format(Date(abs(timerModule.currentTimerValue.value ?: 0)))
        })
    }

    /**
     * For blink tickness
     */
    fun getShowTick(): LiveData<Boolean> {
        return Transformations.map(showTickness, fun(input: Boolean): Boolean? {
            return input || !isRunning.value!!
        })
    }

    /**
     * For blink timer
     */
    fun getShowTime(): LiveData<Boolean> {
        return Transformations.map(showTickness, fun(input: Boolean): Boolean? {
            return input || isRunning.value!!
        })
    }

    fun getIsRunning(): LiveData<Boolean> {
        return isRunning
    }

    fun getIsEnd(): LiveData<Boolean> {
        return Transformations.map(showTickness, fun(input: Boolean): Boolean? {
            return (timerModule.currentTimerValue.value ?: 0) <= 0
        })
    }

    fun changeSeconds(seconds: Int){
        timerModule.timerValue.value = (timerModule.timerValue.value ?: 0) + seconds * 1000
        timerModule.currentTimerValue.value = timerModule.timerValue.value
    }

    fun changeMinutes(minutes: Int) {
        timerModule.timerValue.value = (timerModule.timerValue.value?: 0) + minutes * 60 * 1000
        timerModule.currentTimerValue.value = timerModule.timerValue.value
    }

    fun pauseTimer(){
        isRunning.value = false
        timerListener?.onStopTimer()
    }

    fun startTimer(){
        isRunning.value = true
        timerListener?.onStartTimer(timerModule.currentTimerValue.value ?: 0)
    }

    fun stopTimer(){
        isRunning.value = false
        timerModule.currentTimerValue.value = timerModule.timerValue.value
        timerListener?.onStopTimer()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateUiRunnable)
    }

    private fun updateUi(){
        showTickness.value = !showTickness.value!!
        handler.postDelayed(updateUiRunnable, 500)
    }

    interface TimerListener{
        fun onStartTimer(interval: Long)
        fun onStopTimer()
    }
}