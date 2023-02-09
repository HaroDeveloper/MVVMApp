package povio.flowrspot

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import povio.flowrspot.di.retrofitModule
import povio.flowrspot.di.viewModelModule

class FlowrSpot : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@FlowrSpot)
            modules(
                listOf(
                    retrofitModule,
                    viewModelModule
                )
            )
        }
    }
}