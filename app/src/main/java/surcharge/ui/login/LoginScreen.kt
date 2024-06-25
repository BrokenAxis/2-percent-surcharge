package surcharge.ui.login

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.utils.retrofit.ApiClient
import surcharge.utils.retrofit.RetrofitClient
import surcharge.utils.retrofit.generateCodeChallenge

@Composable
fun LoginScreen(
    app: AppContainer,
    onNavigateToHome: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        var refreshCsrf by remember { mutableIntStateOf(0) }
        var refresh by remember { mutableIntStateOf(0) }
        var csrf by remember { mutableStateOf("") }
        var auth by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        key(refresh) {
            auth =
                MobilePaymentsSdk.authorizationManager().authorizationState.isAuthorized.toString() + ", " + MobilePaymentsSdk.authorizationManager().authorizationState.isAuthorizationInProgress.toString()
        }
        var intent by remember { mutableStateOf("") }

        LaunchedEffect(refreshCsrf, refresh) {
            withContext(IO) {
                intent = app.settings.readIntent()
                if (intent.isEmpty()) {
                    app.settings.refreshCsrf()
                }
                csrf = app.settings.readCsrf()
            }
        }

        if (intent.isNotEmpty()) {
            val uri = intent.toUri()
            val authorizationCode = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")

            if (state != csrf) {
                LaunchedEffect(true) {
                    snackbarHostState.showSnackbar("Error: authentication state mismatch. Please try again")
                }
                intent = ""
            } else if (authorizationCode != null) {
                LaunchedEffect(true) {
                    val response = ApiClient.squareApi.getToken(
                        clientID = app.squareId,
                        grantType = "authorization_code",
                        redirectUrl = RetrofitClient.REDIRECT_URL,
                        codeVerifier = ApiClient.codeVerifier,
                        code = authorizationCode,
                    )
                    ApiClient.resetCodeVerifier()

                    if (response.isSuccessful && response.body() != null) {

                        val token = response.body()!!

                        val location =
                            ApiClient.squareApi.getLocation("Bearer " + token.accessToken)

                        if (location.isSuccessful && location.body() != null) {
                            MobilePaymentsSdk.authorizationManager()
                                .authorize(
                                    token.accessToken,
                                    location.body()!!.location.locationID
                                ) {}
                            withContext(IO) {
                                app.settings.updateSquareAccessToken(token)
                                app.settings.updateLocation(location.body()!!.location)
                            }
                        }

                        onNavigateToHome()
                    } else {
                        snackbarHostState.showSnackbar("Error: ${response.errorBody()} ${response.message()}")
                        intent = ""
                    }
                }
            }
        }

        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { _ ->
                refreshCsrf++
            }

        Surface {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.weight(1f))

                Text(
                    text = "Login to Surcharge",
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.weight(2f))

                TextButton(onClick = { refresh++ }) {
                    Text("Refresh")
                }

                OutlinedCard(
                    modifier = Modifier
                        .padding(30.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    )
                ) {
                    if (intent.isEmpty()) {
                        ExtendedFloatingActionButton(
                            text = { Text("Continue with Google") },
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Login,
                                    contentDescription = "Add"
                                )
                            },
                            onClick = {
                                scope.launch(IO) {
                                    snackbarHostState.showSnackbar("yeah nice try buddy you think I'm bothered")
                                }
                                onNavigateToHome()
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 20.dp),
                            elevation = FloatingActionButtonDefaults.elevation(0.5.dp)
                        )

                        Spacer(Modifier.height(20.dp))

                        ExtendedFloatingActionButton(
                            text = { Text("Continue with Square") },
                            icon = { Icon(Icons.Filled.OpenInBrowser, contentDescription = "Add") },
                            onClick = {
                                squareAuth(
                                    clientId = app.squareId,
                                    state = csrf,
                                    launcher = launcher
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 20.dp),
                            elevation = FloatingActionButtonDefaults.elevation(1.dp)
                        )
                    } else {
                        Text(
                            "Authenticating...", modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(30.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
                Spacer(Modifier.height(50.dp))
            }
        }
    }
}

fun squareAuth(
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