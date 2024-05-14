package surcharge.data

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import surcharge.data.prints.Data
import surcharge.data.prints.LocalData
import surcharge.data.settings.SettingsDataStore

interface AppContainer {
    val data: Data
    val settings: SettingsDataStore
    var theme: MutableIntState
}

class AppContainerImpl(private val applicationContext: Context): AppContainer {
    override val data: Data by lazy {
        // TempData()
        LocalData(applicationContext)
    }

    override val settings: SettingsDataStore by lazy {
        SettingsDataStore(applicationContext)
    }

    override var theme = mutableIntStateOf(0)
}