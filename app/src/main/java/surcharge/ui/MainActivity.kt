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

    //    private var callbackReference: CallbackReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as SurchargeApplication).container

        setContent {
            LaunchedEffect(appContainer.theme) {
                withContext(Dispatchers.IO) {
                    appContainer.theme.intValue = appContainer.settings.readTheme()
                }
            }

            SurchargeTheme(appContainer.theme.intValue) {
                HomeScreen(appContainer)
                SurchargeApp(appContainer)
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        val authManager = MobilePaymentsSdk.authorizationManager()
////      Authorize and handle authorization successes or failures
//        callbackReference = authManager.authorize(accessToken, null) { result ->
//            when (result) {
//                is Result.Success -> {
//                    finishWithAuthorizedSuccess(result.value)
//                }
//                is Result.Failure -> {
//                    when (result.errorCode) {
//                        AuthorizeErrorCode.NO_NETWORK -> showRetryDialog(result)
//                        AuthorizeErrorCode.USAGE_ERROR -> showUsageErrorDialog(result)
//                    }
//                }
//            }
//        }
//    }
}