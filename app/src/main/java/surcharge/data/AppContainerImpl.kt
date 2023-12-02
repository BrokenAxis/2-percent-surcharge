package surcharge.data

import android.content.Context
import surcharge.data.prints.Data
import surcharge.data.prints.DataImpl

interface AppContainer {
    val data: Data
}

class AppContainerImpl(private val applicationContext: Context): AppContainer {
    override val data: Data by lazy {
        DataImpl()
    }
}