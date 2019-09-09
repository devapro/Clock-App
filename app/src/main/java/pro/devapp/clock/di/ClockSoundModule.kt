package pro.devapp.clock.di

import android.content.Context
import dagger.Module
import dagger.Provides
import pro.devapp.clock.utils.ClockSounds
import javax.inject.Singleton

@Module
class ClockSoundModule (context: Context){
    private var clockSounds: ClockSounds = ClockSounds(context)

    @Provides
    @Singleton
    fun provideClockSounds() : ClockSounds {
        return clockSounds
    }
}