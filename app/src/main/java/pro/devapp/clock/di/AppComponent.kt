package pro.devapp.clock.di

import dagger.Component
import pro.devapp.clock.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class])
interface AppComponent {
    fun inject(item: MainActivity)
}