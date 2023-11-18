package surcharge.data

import android.content.Context
import surcharge.data.prints.Prints
import surcharge.data.prints.PrintsImpl

interface AppContainer {
    val prints: Prints
}

class AppContainerImpl(private val applicationContext: Context): AppContainer {
    override val prints: Prints by lazy {
        PrintsImpl()
    }
}