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

    private val handler = Handler()

    private val showTickness: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Update timer numbers and blink seconds points
     */
    private val updateUiRunnable: Runnable = Runnable { run { updateUi() } }

    /**
     * Stop blink seconds points if user start setting value by user
     */
    private val enableBlinking: Runnable = Runnable { run { stopBlink = false } }

    private var timerListener: TimerListener? = null

    private var stopBlink: Boolean = false

    @Inject
    lateinit var timerModule: TimerModule

    init {
        handler.postDelayed(updateUiRunnable, 500)
        showTickness.value = true
    }

    fun setTimerListener(listener: TimerListener?) {
        timerListener = listener
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
            return input || !timerModule.isRunning.value!!
        })
    }

    /**
     * For blink timer
     */
    fun getShowTime(): LiveData<Boolean> {
        return Transformations.map(showTickness, fun(input: Boolean): Boolean? {
            return input || timerModule.isRunning.value!! || stopBlink
        })
    }

    /**
     * Is running timer flag for ui
     */
    fun getIsRunning(): LiveData<Boolean> {
        return timerModule.isRunning
    }

    /**
     * Is timer reached time for show warning indicator
     */
    fun getIsEnd(): LiveData<Boolean> {
        return Transformations.map(showTickness, fun(input: Boolean): Boolean? {
            return (timerModule.currentTimerValue.value ?: 0) <= 0
        })
    }

    /**
     * Change timer value (seconds)
     */
    fun changeSeconds(seconds: Int){
        timerModule.timerValue.value = (timerModule.timerValue.value ?: 0) + seconds * 1000
        timerModule.currentTimerValue.value = timerModule.timerValue.value
        stopBinking()
    }

    /**
     * Change timer value (minutes)
     */
    fun changeMinutes(minutes: Int) {
        timerModule.timerValue.value = (timerModule.timerValue.value?: 0) + minutes * 60 * 1000
        timerModule.currentTimerValue.value = timerModule.timerValue.value
        stopBinking()
    }

    /**
     * Pause timer
     */
    fun pauseTimer(){
        timerModule.isRunning.value = false
    }

    /**
     * Start timer
     */
    fun startTimer(){
        timerModule.isRunning.value = true
        timerListener?.onStartTimer()
    }

    /**
     * Stop timer (reset current value)
     */
    fun stopTimer(){
        timerModule.isRunning.value = false
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

    private fun stopBinking(){
        stopBlink = true
        handler.removeCallbacks(enableBlinking)
        handler.postDelayed(enableBlinking, 1000)
    }

    interface TimerListener{
        fun onStartTimer()
        fun onStopTimer()
    }
}