package surcharge.data

import android.content.Context
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
import surcharge.data.prints.Data
import surcharge.data.prints.Firestore
import surcharge.data.settings.SettingsDataStore

interface AppContainer {
    val data: Data
    val settings: SettingsDataStore
    var theme: MutableIntState
    val squareId: String
    val squareLocationId: String
    var squareAuthState: Boolean
}

class AppContainerImpl(private val applicationContext: Context): AppContainer {
    override val data: Data by lazy {
        // TempData()
        // LocalData(applicationContext)
        Firestore(settings)
    }

    override val settings: SettingsDataStore by lazy {
        SettingsDataStore(applicationContext)
    }

    override var theme = mutableIntStateOf(0)

    override val squareId: String = SQUARE_ID
    override val squareLocationId: String = SQUARE_LOCATION
    override var squareAuthState: Boolean =
        MobilePaymentsSdk.authorizationManager().authorizationState.isAuthorized
}