package pro.devapp.clock.di

import dagger.Component
import pro.devapp.clock.MainActivity
import pro.devapp.clock.services.TimerService
import pro.devapp.clock.viewModels.TimerViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, ClockSoundModule::class, TimerModule::class])
interface AppComponent {
    fun inject(item: MainActivity)
    fun inject(item: TimerService)
    fun inject(item: TimerViewModel)
}