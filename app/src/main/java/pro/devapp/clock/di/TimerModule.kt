package pro.devapp.clock.di

import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Values for timer
 */
@Module
class TimerModule {

    /**
     * Timer value in seconds (set by user)
     */
    @NonNull
    var timerValue: MutableLiveData<Long> = MutableLiveData()

    /**
     * Current value in seconds (set if timer run)
     */
    @NonNull
    var currentTimerValue: MutableLiveData<Long> = MutableLiveData()

    /**
     * Flag for timer state
     */
    @NonNull
    var isRunning: MutableLiveData<Boolean> = MutableLiveData()

    init {
        // default timer value is 10 min
        timerValue.value = 10 * 60 * 1000
        currentTimerValue.value = 10 * 60 * 1000
        isRunning.value = false
    }

    @Singleton
    @Provides
    fun provideTimeModule(): TimerModule{
        return this
    }
}