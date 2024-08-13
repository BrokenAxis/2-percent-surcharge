package surcharge.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import com.google.firebase.auth.FirebaseAuth
import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
import com.squareup.sdk.mobilepayments.authorization.AuthorizeErrorCode
import com.squareup.sdk.mobilepayments.core.CallbackReference
import com.squareup.sdk.mobilepayments.core.Result
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import surcharge.SurchargeApplication
import surcharge.data.AppContainer
import surcharge.ui.theme.SurchargeTheme
import surcharge.utils.retrofit.ApiClient
import surcharge.utils.retrofit.Token
import java.time.Instant
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {

    private var authResultReference: CallbackReference? = null
    private var authStateReference: CallbackReference? = null
    private lateinit var appContainer: AppContainer
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        appContainer = (application as SurchargeApplication).container

        val auth = FirebaseAuth.getInstance()

        setAuthStateCallback()
        runBlocking(IO) {
            appContainer.settings.updateIntent((intent.data ?: "").toString())

            if (Instant.now().minus(7, ChronoUnit.DAYS)
                    .isAfter(appContainer.settings.readLastRefresh())
            ) {
                refreshToken(appContainer.settings.readSquareAccessToken())
                appContainer.settings.resetLastRefresh()
            }
        }

        setContent {
            LaunchedEffect(appContainer.theme) {
                withContext(IO) {
                    appContainer.theme.intValue = appContainer.settings.readTheme()
                }
            }
            val start = when (auth.currentUser != null) {
                true -> SurchargeDestinations.HOME_ROUTE
                false -> SurchargeDestinations.LOGIN_ROUTE
            }

            SurchargeTheme(appContainer.theme.intValue) {
                SurchargeApp(appContainer, start)
            }
        }
    }

    private fun setAuthStateCallback() {
        appContainer.squareAuthState =
            MobilePaymentsSdk.authorizationManager().authorizationState.isAuthorized
        authStateReference = MobilePaymentsSdk.authorizationManager()
            .setAuthorizationStateChangedCallback { authState ->
                appContainer.squareAuthState = authState.isAuthorized
//            // unsure when this would ever be true given callback is only called when log in or out occurs
//            when (authState.isAuthorizationInProgress) {
//                true -> {}
//                false -> {}
            }
    }

    private fun authorize(accessToken: String) {
        authResultReference = MobilePaymentsSdk.authorizationManager().authorize(
            token = accessToken,
            locationId = appContainer.squareLocationId
        ) { result ->
            when (result) {
                is Result.Success -> {

                }

                is Result.Failure -> {
                    when (result.errorCode) {
                        AuthorizeErrorCode.NO_NETWORK -> {}
                        AuthorizeErrorCode.USAGE_ERROR -> {}
                    }
                }
            }
        }
    }

    private fun refreshToken(accessToken: Token) {
        if (Instant.now().isBefore(Instant.parse(accessToken.refreshTokenExpiresAt))) {
            runBlocking(IO) {
                ApiClient.squareApi.refreshToken(
                    clientID = appContainer.squareId,
                    refreshToken = accessToken.refreshToken
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        setAuthStateCallback()

        if (!appContainer.squareAuthState) {
            var accessToken: Token
            runBlocking(IO) {
                accessToken = appContainer.settings.readSquareAccessToken()
            }
            authorize(accessToken.accessToken)
        }
    }

    override fun onPause() {
        super.onPause()
        authResultReference?.clear()
        authStateReference?.clear()
    }
}