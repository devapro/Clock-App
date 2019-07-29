package pro.devapp.clock.di

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ContextModule(context: Context) {
    private var mContext: Context = context
    @Provides
    @Singleton
    fun provideContext(): Context {
        return mContext
    }
}