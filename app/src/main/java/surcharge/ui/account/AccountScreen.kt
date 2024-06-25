package surcharge.ui.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
import com.squareup.sdk.mobilepayments.core.Result
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.types.Artist
import surcharge.utils.components.Tile
import surcharge.utils.img.upload
import surcharge.utils.retrofit.Token

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    app: AppContainer,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            var viewSelectDialog by remember { mutableStateOf(false) }

            if (viewSelectDialog) {
                SelectArtist(app = app, onDismissRequest = { viewSelectDialog = false })
            }

            Tile(title = "Select Artist",
                subtitle = "This definitely matters",
                icon = Icons.Filled.SwitchAccount,
                onClick = { viewSelectDialog = true }
            )

            var viewEditArtist by remember { mutableStateOf(false) }

            if (viewEditArtist) {
                EditArtist(
                    app = app,
                    onDismissRequest = { viewEditArtist = false },
                    snackbarHostState = snackbarHostState
                )
            }

            Tile(title = "Edit Artist",
                subtitle = "Change artist profile picture",
                icon = Icons.Filled.Image,
                onClick = { viewEditArtist = true }
            )

            var viewAddDialog by remember { mutableStateOf(false) }

            if (viewAddDialog) {
                AddArtist(
                    app = app,
                    onDismissRequest = { viewAddDialog = false },
                    snackbarHostState = snackbarHostState
                )
            }

            Tile(title = "Add Artist",
                subtitle = "",
                icon = Icons.Filled.PersonAdd,
                onClick = { viewAddDialog = true }
            )
            var accessToken by remember { mutableStateOf("") }
            var token by remember { mutableStateOf(Token()) }
            LaunchedEffect(true) {
                withContext(IO) {
                    accessToken = app.settings.readSquareAccessToken().accessToken
                    token = app.settings.readSquareAccessToken()
                }
            }

            Tile(title = "Log In",
                subtitle = "Debug only",
                icon = Icons.AutoMirrored.Filled.Login,
                onClick = {
                    MobilePaymentsSdk.authorizationManager()
                        .authorize(accessToken, app.squareLocationId) { result ->
                            when (result) {
                                is Result.Success -> scope.launch(IO) {
                                    snackbarHostState.showSnackbar(
                                        result.toString()
                                    )
                                }

                                is Result.Failure -> scope.launch(IO) {
                                    snackbarHostState.showSnackbar(
                                        token.toString()
                                    )
                                }
                            }
                        }
                }
            )

            Tile(title = "Log Out",
                subtitle = "This will log you out of the app (restart app pls)",
                icon = Icons.AutoMirrored.Filled.Logout,
                onClick = {
                    MobilePaymentsSdk.authorizationManager().deauthorize()
                    scope.launch(IO) { app.settings.updateSquareAccessToken(Token()) }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectArtist(
    app: AppContainer,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Text(
                text = "Select Current Artist",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally)
            )

            var artists by remember { mutableStateOf(listOf<Artist>()) }
            var selected by remember { mutableStateOf("No Artist Selected") }

            val scope = rememberCoroutineScope()
            LaunchedEffect(true) {
                withContext(IO) {
                    artists = app.data.getArtists().getOrDefault(listOf())
                    selected = app.settings.readArtist()
                }
            }

            var artistExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = artistExpanded,
                onExpandedChange = { artistExpanded = !artistExpanded },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(),
                    readOnly = true,
                    value = selected,
                    onValueChange = {},
                    label = { Text("Artist") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = artistExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = artistExpanded,
                    onDismissRequest = { artistExpanded = false }
                ) {
                    artists.forEach { artist ->
                        DropdownMenuItem(
                            text = { Text(artist.name) },
                            onClick = {
                                selected = artist.name
                                artistExpanded = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            TextButton(
                onClick = {
                    scope.launch { withContext(IO) { app.settings.updateArtist(selected) } }
                    onDismissRequest()
                },
                modifier = Modifier
                    .padding(end = 10.dp)
                    .align(Alignment.End)
            ) {
                Text("Confirm")
            }
        }
    }
}

@Composable
private fun AddArtist(
    app: AppContainer,
    onDismissRequest: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard {
            Text(
                text = "Add New Artist",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .align(Alignment.CenterHorizontally)
            )

            HorizontalDivider()

            var name by remember { mutableStateOf("") }
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .padding(15.dp)
                    .align(Alignment.CenterHorizontally),
                placeholder = { Text("Gege Akutami") },
                label = { Text("Artist Name") }
            )

            var imageUri by remember { mutableStateOf<Uri?>(null) }
            val image = remember { mutableStateOf("") }
            val progress = remember { mutableDoubleStateOf(0.0) }
            var success by remember { mutableStateOf(false) }

            Column(Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "uploaded image",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                key(imageUri) {
                    if (imageUri != null && !success) {
                        LinearProgressIndicator(
                            progress = { progress.doubleValue.toFloat() },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                        )
                    }
                    if (success) {
                        Text(
                            text = "Upload Complete!",
                            modifier = Modifier.padding(start = 20.dp, top = 5.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    } else Spacer(Modifier.height(5.dp))
                }
            }
            val scope = rememberCoroutineScope()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri: Uri? ->
                if (uri != null) {
                    imageUri = uri
                    upload(
                        image = imageUri!!,
                        artist = name,
                        url = image,
                        progress = progress,
                        onSuccess = { success = true },
                        scope = scope,
                        snackbar = snackbarHostState
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            FilledTonalButton(
                onClick = {
                    image.value = ""
                    success = false
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier
                    .padding(start = 30.dp),
                enabled = name.isNotEmpty()
            ) {
                Text("Upload Image")
            }
            TextButton(
                onClick = {
                    scope.launch(IO) {
                        app.data.addArtist(Artist(name, image.value))
                    }
                    onDismissRequest()
                },
                modifier = Modifier.align(Alignment.End),
                enabled = success && name.isNotEmpty()
            ) {
                Text("Confirm")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditArtist(
    app: AppContainer,
    onDismissRequest: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard {
            Text(
                text = "Change Artist Profile Picture",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .align(Alignment.CenterHorizontally)
            )

            HorizontalDivider()

            var artists by remember { mutableStateOf(listOf(Artist())) }

            LaunchedEffect(true) {
                withContext(IO) {
                    artists = app.data.getArtists().getOrDefault(listOf(Artist()))
                }
            }

            var artistExpanded by remember { mutableStateOf(false) }
            var selected by remember { mutableStateOf(artists.first()) }

            ExposedDropdownMenuBox(
                expanded = artistExpanded,
                onExpandedChange = { artistExpanded = !artistExpanded },
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    readOnly = true,
                    value = selected.name,
                    onValueChange = {},
                    label = { Text("Artist") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = artistExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = artistExpanded,
                    onDismissRequest = { artistExpanded = false }
                ) {
                    artists.forEach { artist ->
                        DropdownMenuItem(
                            text = { Text(artist.name) },
                            onClick = {
                                artistExpanded = false
                                selected = artist
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            var imageUri by remember { mutableStateOf<Uri?>(null) }
            val image = remember { mutableStateOf("") }
            val progress = remember { mutableDoubleStateOf(0.0) }
            var success by remember { mutableStateOf(false) }

            Column(Modifier.fillMaxWidth()) {
                Card(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "uploaded image",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                key(imageUri) {
                    if (imageUri != null && !success) {
                        LinearProgressIndicator(
                            progress = { progress.doubleValue.toFloat() },
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth()
                        )
                    }
                    if (success) {
                        Text(
                            text = "Upload Complete!",
                            modifier = Modifier.padding(start = 20.dp, top = 5.dp),
                            style = MaterialTheme.typography.labelMedium
                        )
                    } else Spacer(Modifier.height(5.dp))
                }
            }
            val scope = rememberCoroutineScope()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri: Uri? ->
                if (uri != null) {
                    imageUri = uri
                    upload(
                        image = imageUri!!,
                        artist = selected.name,
                        url = image,
                        progress = progress,
                        onSuccess = { success = true },
                        scope = scope,
                        snackbar = snackbarHostState
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            FilledTonalButton(
                onClick = {
                    image.value = ""
                    success = false
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier.padding(start = 30.dp)
            ) {
                Text("Upload Image")
            }
            TextButton(
                onClick = {
                    selected.image = image.value
                    scope.launch(IO) {
                        app.data.addArtist(selected)
                    }
                    onDismissRequest()
                },
                modifier = Modifier.align(Alignment.End),
                enabled = success
            ) {
                Text("Confirm")
            }
        }
    }
}