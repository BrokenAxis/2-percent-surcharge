package surcharge

import android.app.Application
import com.cloudinary.android.MediaManager
import surcharge.data.AppContainer
import surcharge.data.AppContainerImpl

class SurchargeApplication : Application() {
    companion object {
        const val SURCHARGE_APP_URI = "" // TODO
    }

    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        MediaManager.init(this)
        container = AppContainerImpl(this)
    }
}