package surcharge.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import surcharge.SurchargeApplication
import surcharge.ui.home.HomeScreen
import surcharge.ui.theme.SurchargeTheme


class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as SurchargeApplication).container

        setContent {

            LaunchedEffect(appContainer.theme) {
                withContext(Dispatchers.IO) {
                    appContainer.theme.value = appContainer.settings.readTheme()
                }
            }

            SurchargeTheme(appContainer.theme.value) {
                HomeScreen()
                SurchargeApp(appContainer)
            }
        }
    }


}