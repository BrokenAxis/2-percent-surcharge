package surcharge.ui.login

//import androidx.credentials.CredentialManager
//import androidx.credentials.CustomCredential
//import androidx.credentials.GetCredentialRequest
//import androidx.credentials.exceptions.GetCredentialException
//import com.google.android.libraries.identity.googleid.GetGoogleIdOption
//import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
//import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import kotlinx.coroutines.launch
import surcharge.R
import surcharge.data.AppContainer
import surcharge.data.prints.Firestore
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
//        var refreshCsrf by remember { mutableIntStateOf(0) }
//        var csrf by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
//        var intent by remember { mutableStateOf("") }
        var authenticating by remember { mutableStateOf(false) }

//        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
//            refreshCsrf++
//        }
//
//        LaunchedEffect(refreshCsrf) {
//            withContext(IO) {
//                intent = app.settings.readIntent()
//                if (intent.isEmpty()) {
//                    app.settings.refreshCsrf()
//                }
//                csrf = app.settings.readCsrf()
//            }
//        }

//        if (intent.isNotEmpty()) {
//            val uri = intent.toUri()
//            val authorizationCode = uri.getQueryParameter("code")
//            val state = uri.getQueryParameter("state")
//
//            if (state != csrf) {
//                LaunchedEffect(true) {
//                    snackbarHostState.showSnackbar("Error: authentication state mismatch. Please try again")
//                }
//                intent = ""
//                refreshCsrf++
//            } else if (authorizationCode != null) {
//                LaunchedEffect(true) {
//                    val response = ApiClient.squareApi.getToken(
//                        clientID = app.squareId,
//                        redirectUrl = RetrofitClient.REDIRECT_URL,
//                        codeVerifier = ApiClient.codeVerifier,
//                        code = authorizationCode,
//                    )
//                    ApiClient.resetCodeVerifier()
//
//                    if (response.isSuccessful && response.body() != null) {
//
//                        val token = response.body()!!
//
//                        val location = ApiClient.squareApi.getLocation("Bearer ${token.accessToken}")
//
//                        if (location.isSuccessful && location.body() != null) {
//                            MobilePaymentsSdk.authorizationManager()
//                                .authorize(token.accessToken, location.body()!!.location.locationID) { result ->
//                                    when (result) {
//                                        is Result.Success -> {
//                                            scope.launch(IO) {
//                                                app.settings.updateSquareAccessToken(token)
//                                                app.settings.updateLocation(location.body()!!.location)
//                                            }
//                                            onNavigateToHome()
//                                        }
//                                        is Result.Failure -> {
//                                            when (result.errorCode) {
//                                                AuthorizeErrorCode.NO_NETWORK -> scope.launch(IO) { snackbarHostState.showSnackbar(result.errorMessage) }
//                                                AuthorizeErrorCode.USAGE_ERROR -> scope.launch(IO) { snackbarHostState.showSnackbar(result.errorMessage) }
//                                            }
//                                            intent = ""
//                                            refreshCsrf++
//                                        }
//                                    }
//                                }
//                        }
//                    } else {
//                        snackbarHostState.showSnackbar("Error: ${response.errorBody()} ${response.message()}")
//                        intent = ""
//                        refreshCsrf++
//                    }
//                }
//            }
//        }

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

                val signInLauncher = rememberLauncherForActivityResult(
                    FirebaseAuthUIActivityResultContract(),
                ) { res ->
                    val response = res.idpResponse
                    if (res.resultCode == RESULT_OK) {
                        scope.launch { (app.data as Firestore).addUser() }
                        onNavigateToHome()
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("${response?.error}") }
                    }
                }
                val providers = arrayListOf(
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )

                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()

                OutlinedCard(
                    modifier = Modifier
                        .padding(30.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    )
                ) {
                    if (authenticating) {
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

                    } else {
                        ExtendedFloatingActionButton(
                            text = { Text("Sign in with Google") },
                            icon = {
                                Image(
                                    painterResource(R.drawable.ic_logo_google),
                                    contentDescription = "Google",
                                )
                            },
                            onClick = {
                                signInLauncher.launch(signInIntent)
                                authenticating = true
                            },
                            modifier = Modifier
                                .padding(30.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        /*
                        Credential Manager + Square Login
                        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { refreshCsrf++ }
                        val context = LocalContext.current
                        val credentialManager = CredentialManager.create(LocalContext.current)
                        ExtendedFloatingActionButton(
                            text = { Text("Sign in with Google") },
                            icon = {
                                Image(
                                    painterResource(R.drawable.ic_logo_google),
                                    contentDescription = "Google",
                                )
                            },
                            onClick = {
//                                val googleIdOption = GetSignInWithGoogleOption.Builder(R.string.default_web_client_id)
//                                    .setNonce("")
//                                    .build()
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(true)
                                    .setServerClientId(context.getString(R.string.default_web_client_id))
                                    .setAutoSelectEnabled(true)
                                .build()
                                val request = GetCredentialRequest(listOf(googleIdOption))

                                scope.launch {
                                    try {
                                        val result = credentialManager.getCredential(
                                            context = context,
                                            request = request,
                                        )
                                        when(result.credential) {
                                            is CustomCredential -> {
                                                if (result.credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                                    try {
                                                        // Use googleIdTokenCredential and extract id to validate and
                                                        // authenticate on your server.
                                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
//                                                        val auth = Firebase.auth
//                                                        val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
//                                                        auth.signInWithCredential(credential).addOnCompleteListener { task->
//                                                            when (task.isSuccessful) {
//                                                                true -> onNavigateToHome()
//                                                                false -> {
//                                                                    scope.launch { snackbarHostState.showSnackbar("Error: ${task.exception}") }
//                                                                }
//                                                            }
//                                                        }

                                                    } catch (e: GoogleIdTokenParsingException) {
                                                        snackbarHostState.showSnackbar("CredentialError: Only Google Sign-in is supported")
                                                    }
                                                } else {
                                                    snackbarHostState.showSnackbar("Credential Error: Only Google Sign-in is supported")
                                                }
                                            }
                                            else -> snackbarHostState.showSnackbar("Credential Error: Only Google Sign-in is supported")
                                        }
                                    } catch (e: GetCredentialException) {
                                        snackbarHostState.showSnackbar("Credential Error: Only Google Sign-in is supported: ${e.type} ${e.message}")
                                    }
                                }
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
                        */
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