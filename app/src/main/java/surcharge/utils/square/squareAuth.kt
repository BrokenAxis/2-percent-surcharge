package surcharge.utils.square

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material3.SnackbarHostState
import androidx.core.net.toUri
import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
import com.squareup.sdk.mobilepayments.authorization.AuthorizeErrorCode
import com.squareup.sdk.mobilepayments.core.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import surcharge.data.SQUARE_ID
import surcharge.utils.retrofit.ApiClient
import surcharge.utils.retrofit.Location
import surcharge.utils.retrofit.RetrofitClient
import surcharge.utils.retrofit.Token
import surcharge.utils.retrofit.generateCodeChallenge

fun requestSquareAuth(
    clientId: String,
    state: String,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    ApiClient.resetCodeVerifier()
    val url = Uri.Builder()
        .scheme("https")
        .authority("connect.squareup.com")
        .appendPath("oauth2")
        .appendPath("authorize")
        .appendQueryParameter("client_id", clientId)
        .appendQueryParameter("scope", RetrofitClient.PERMS)
        .appendQueryParameter("session", "false")
        .appendQueryParameter("state", state)
        .appendQueryParameter("code_challenge", generateCodeChallenge(ApiClient.codeVerifier))
        .build()

    val intent = Intent(Intent.ACTION_VIEW, url)
    launcher.launch(intent)
}

suspend fun handleOAuth(
    intent: String,
    csrf: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onSuccess: (token: Token, location: Location) -> Unit
) {
    val uri = intent.toUri()
    val authorizationCode = uri.getQueryParameter("code")
    val state = uri.getQueryParameter("state")

    if (state != csrf) {
        snackbarHostState.showSnackbar("Error: authentication state mismatch. Please try again")
    } else if (authorizationCode != null) {
        val response = ApiClient.squareApi.getToken(
            clientID = SQUARE_ID,
            redirectUrl = RetrofitClient.REDIRECT_URL,
            codeVerifier = ApiClient.codeVerifier,
            code = authorizationCode,
        )
        ApiClient.resetCodeVerifier()

        if (response.isSuccessful && response.body() != null) {

            val token = response.body()!!

            val locationRes = ApiClient.squareApi.getLocation("Bearer ${token.accessToken}")

            if (locationRes.isSuccessful && locationRes.body() != null) {
                val location = locationRes.body()!!.location
                MobilePaymentsSdk.authorizationManager()
                    .authorize(
                        token.accessToken,
                        location.locationID
                    ) { result ->
                        when (result) {
                            is Result.Success -> {
                                onSuccess(token, location)
                            }

                            is Result.Failure -> {
                                when (result.errorCode) {
                                    AuthorizeErrorCode.NO_NETWORK -> scope.launch(IO) {
                                        snackbarHostState.showSnackbar(result.errorMessage)
                                    }

                                    AuthorizeErrorCode.USAGE_ERROR -> scope.launch(IO) {
                                        snackbarHostState.showSnackbar(result.errorMessage)
                                    }
                                }
                            }
                        }
                    }

            } else {
                snackbarHostState.showSnackbar("Error: ${response.errorBody()} ${response.message()}")
            }
        }
    }
}