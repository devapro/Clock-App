package pro.devapp.clock.di

import androidx.annotation.NonNull
import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

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

    init {
        timerValue.value = 10 * 60 * 1000
        currentTimerValue.value = 10 * 60 * 1000
    }

    @Singleton
    @Provides
    fun provideTimeModule(): TimerModule{
        return this
    }
}