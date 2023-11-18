package surcharge.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import surcharge.SurchargeApplication
import surcharge.ui.home.HomeScreen


class MainActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as SurchargeApplication).container

        setContent {
            HomeScreen()
            SurchargeApp(appContainer)
        }
    }


}