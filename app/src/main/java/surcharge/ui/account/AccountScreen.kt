package surcharge.ui.account

import android.app.Activity.RESULT_OK
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
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SelectAll
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.data.SQUARE_ID
import surcharge.data.SQUARE_SECRET
import surcharge.data.prints.Firestore
import surcharge.types.Artist
import surcharge.types.Group
import surcharge.types.User
import surcharge.utils.components.Tile
import surcharge.utils.components.gallery.PrintImage
import surcharge.utils.img.upload
import surcharge.utils.retrofit.ApiClient
import surcharge.utils.retrofit.Location
import surcharge.utils.retrofit.Token
import surcharge.utils.square.handleOAuth
import surcharge.utils.square.requestSquareAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    app: AppContainer,
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit
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
            var authRefresh by remember { mutableIntStateOf(0) }
            var linked by remember { mutableStateOf(false) }
            var location by remember { mutableStateOf(Location()) }
            var group by remember { mutableStateOf("No Group") }
            var isOwner by remember { mutableStateOf(false) }
            var groupMembers by remember { mutableStateOf(listOf<User>()) }
            LaunchedEffect(authRefresh) {
                withContext(IO) {
                    location = app.settings.readLocation()
                    linked = app.squareAuthState
                    if (app.data is Firestore) {
                        group = (app.data as Firestore).getGroupName()
                        isOwner = (app.data as Firestore).isOwner()
                        groupMembers = (app.data as Firestore).getGroupUsers()
                    }
                }
            }

            Card(
                modifier = Modifier.padding(5.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                val user = FirebaseAuth.getInstance().currentUser
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PrintImage(
                        url = user?.photoUrl.toString(),
                        modifier = Modifier
                            .size(200.dp)
                            .padding(20.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.FillWidth
                    )
                }
                Text(
                    text = "Name: ${user?.displayName ?: "Unknown"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )
                Text(
                    text = "Email: ${user?.email ?: "Unknown"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                Text(
                    text = "Group: $group",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                )

                if (linked) {
                    Text(
                        text = "Business Name: ${location.locationBusinessName}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                    Text(
                        text = "Location: ${location.locationName}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                    Text(
                        text = "Phone Number: ${location.locationPhoneNumber}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                } else {
                    Text(
                        text = "No Square Account Linked. This is required to take card payments",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            var accessToken by remember { mutableStateOf("") }
            var token by remember { mutableStateOf(Token()) }
            LaunchedEffect(authRefresh) {
                withContext(IO) {
                    accessToken = app.settings.readSquareAccessToken().accessToken
                    token = app.settings.readSquareAccessToken()
                }
            }

            if (linked) {
                Tile(title = "Unlink Square Account",
                    subtitle = "",
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        MobilePaymentsSdk.authorizationManager().deauthorize()
                        scope.launch(IO) {
                            app.settings.updateSquareAccessToken(Token())
                            app.settings.updateLocation(Location())
                            val response = ApiClient.squareApi.revokeToken(
                                secret = "Client $SQUARE_SECRET",
                                token = token.accessToken,
                                clientID = SQUARE_ID
                            )
                            if (response.isSuccessful) {
                                val success = response.body()
                                if (success?.success == false) {
                                    snackbarHostState.showSnackbar("Error: There was a problem invalidating access tokens. Logging out otherwise successful")
                                }
                            } else {
                                snackbarHostState.showSnackbar("Error: ${response.errorBody()}")
                            }
                            authRefresh++
                        }
                    }
                )
            } else {
                var refreshCsrf by remember { mutableIntStateOf(0) }
                var csrf by remember { mutableStateOf("") }
                var intent by remember { mutableStateOf("") }

                LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                    refreshCsrf++
                }

                LifecycleEventEffect(Lifecycle.Event.ON_START) {
                    refreshCsrf++
                }

                val launcher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                        if (result.resultCode == RESULT_OK && result.data != null) {
                            scope.launch {
                                handleOAuth(
                                    intent = result.data.toString(),
                                    csrf = csrf,
                                    scope = scope,
                                    snackbarHostState = snackbarHostState,
                                    onSuccess = { token, location ->
                                        scope.launch(IO) {
                                            app.settings.updateSquareAccessToken(token)
                                            app.settings.updateLocation(location)
                                        }
                                    }
                                )
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Error: authentication cancelled")
                            }
                        }
                        refreshCsrf++
                    }
                LaunchedEffect(refreshCsrf) {
                    withContext(IO) {
                        intent = app.settings.readIntent()
                        if (intent.isEmpty()) {
                            app.settings.refreshCsrf()
                        }
                        csrf = app.settings.readCsrf()
                    }
                }

                Tile(title = "Link a Square Account",
                    subtitle = "",
                    icon = Icons.AutoMirrored.Filled.Login,
                    onClick = {
                        requestSquareAuth(
                            clientId = app.squareId,
                            state = csrf,
                            launcher = launcher
                        )
                    }
                )
            }

            Card(
                modifier = Modifier.padding(5.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            ) {
                var selectGroupDialog by remember { mutableStateOf(false) }

                Tile(title = "Select Group",
                    subtitle = "",
                    icon = Icons.Filled.SelectAll,
                    onClick = { selectGroupDialog = true }
                )

                if (selectGroupDialog) {
                    SelectGroup(
                        app = app,
                        scope = scope,
                        onDismissRequest = { selectGroupDialog = false },
                        onConfirmRequest = {
                            selectGroupDialog = false
                            authRefresh++
                        }
                    )
                }

                if (isOwner) {
                    var editGroup by remember { mutableStateOf(false) }

                    Tile(title = "Edit Group",
                        subtitle = "",
                        icon = Icons.Filled.AddBusiness,
                        onClick = { editGroup = true }
                    )

                    if (editGroup) {
                        Dialog(onDismissRequest = { editGroup = false }) {
                            Card {
                                Text(
                                    text = "Edit Group",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .padding(top = 20.dp)
                                        .align(Alignment.CenterHorizontally)
                                )

                                var name by remember { mutableStateOf(group) }
                                var isError by remember { mutableStateOf(false) }
                                TextField(
                                    value = name,
                                    onValueChange = {
                                        name = it
                                        isError = name.isEmpty()
                                    },
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .align(Alignment.CenterHorizontally),
                                    placeholder = { Text("The Money Printers") },
                                    label = { Text("Group Name") },
                                    isError = isError
                                )

                                TextButton(
                                    onClick = {
                                        scope.launch(IO) {
                                            if (app.data is Firestore) {
                                                (app.data as Firestore).updateGroup(name)
                                            }
                                            authRefresh++
                                        }
                                        editGroup = false
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    enabled = !isError
                                ) {
                                    Text("Confirm")
                                }
                            }
                        }
                    }

                    var viewInviteUsers by remember { mutableStateOf(false) }

                    Tile(title = "Invite Users to Group",
                        subtitle = "",
                        icon = Icons.Filled.AddBusiness,
                        onClick = { viewInviteUsers = true }
                    )

                    if (viewInviteUsers) {
                        Dialog(onDismissRequest = { viewInviteUsers = false }) {
                            Card {
                                Text(
                                    text = "Invite Users by Email",
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier
                                        .padding(top = 20.dp)
                                        .align(Alignment.CenterHorizontally)
                                )

                                var email by remember { mutableStateOf("") }
                                var isError by remember { mutableStateOf(false) }
                                TextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        isError = email.isEmpty()
                                    },
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .align(Alignment.CenterHorizontally),
                                    placeholder = { Text("example@gmail.com") },
                                    label = { Text("User Email") },
                                    supportingText = { Text("Only registered users can be invited") },
                                    isError = isError
                                )
                                TextButton(
                                    onClick = {
                                        scope.launch(IO) {
                                            if (app.data is Firestore) {
                                                if ((app.data as Firestore).inviteToGroup(email)) {
                                                    snackbarHostState.showSnackbar("User successfully invited")
                                                } else snackbarHostState.showSnackbar("Error: User not found")
                                            }
                                            authRefresh++
                                        }
                                        viewInviteUsers = false
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    enabled = !isError
                                ) {
                                    Text("Confirm")
                                }
                            }
                        }
                    }
                }
                HorizontalDivider()

                Text(
                    text = "Group Members:\n${groupMembers.joinToString("\n") { it.name + " | " + it.email }}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            Card(
                modifier = Modifier.padding(5.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                )
            ) {
//                var viewSelectDialog by remember { mutableStateOf(false) }

//                if (viewSelectDialog) {
//                    SelectArtist(app = app, onDismissRequest = { viewSelectDialog = false })
//                }
//
//                Tile(title = "Select Artist",
//                    subtitle = "This definitely matters",
//                    icon = Icons.Filled.SwitchAccount,
//                    onClick = { viewSelectDialog = true }
//                )

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
            }

            Tile(title = "Log Out",
                subtitle = "",
                icon = Icons.AutoMirrored.Filled.Logout,
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    onNavigateToLogin()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectGroup(
    app: AppContainer,
    scope: CoroutineScope,
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card {
            Text(
                text = "Select Group",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally)
            )

            var groupExpanded by remember { mutableStateOf(false) }
            var groups by remember { mutableStateOf(listOf(Group(name = "Unknown Group"))) }
            var selected by remember { mutableStateOf(groups.first()) }

            LaunchedEffect(true) {
                withContext(IO) {
                    if (app.data is Firestore) {
                        groups = (app.data as Firestore).getGroups()
                        selected = groups.first()
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = groupExpanded,
                onExpandedChange = { groupExpanded = !groupExpanded },
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    readOnly = true,
                    value = selected.name,
                    onValueChange = {},
                    label = { Text("Group") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = groupExpanded,
                    onDismissRequest = { groupExpanded = false }
                ) {
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = {
                                Text(group.name)
                            },
                            onClick = {
                                groupExpanded = false
                                selected = group
                            },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            TextButton(
                onClick = {
                    scope.launch(IO) {
                        if (app.data is Firestore) {
                            (app.data as Firestore).changeGroup(selected.groupId)
                        }
                        onConfirmRequest()
                    }
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


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun SelectArtist(
//    app: AppContainer,
//    onDismissRequest: () -> Unit,
//) {
//    Dialog(onDismissRequest = onDismissRequest) {
//        Card {
//            Text(
//                text = "Select Current Artist",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier
//                    .padding(top = 20.dp)
//                    .align(Alignment.CenterHorizontally)
//            )
//
//            var artists by remember { mutableStateOf(listOf<Artist>()) }
//            var selected by remember { mutableStateOf("No Artist Selected") }
//
//            val scope = rememberCoroutineScope()
//            LaunchedEffect(true) {
//                withContext(IO) {
//                    artists = app.data.getArtists().getOrDefault(listOf())
//                    selected = app.settings.readArtist()
//                }
//            }
//
//            var artistExpanded by remember { mutableStateOf(false) }
//
//            ExposedDropdownMenuBox(
//                expanded = artistExpanded,
//                onExpandedChange = { artistExpanded = !artistExpanded },
//                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
//            ) {
//                TextField(
//                    modifier = Modifier
//                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
//                        .fillMaxWidth(),
//                    readOnly = true,
//                    value = selected,
//                    onValueChange = {},
//                    label = { Text("Artist") },
//                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = artistExpanded) }
//                )
//                ExposedDropdownMenu(
//                    expanded = artistExpanded,
//                    onDismissRequest = { artistExpanded = false }
//                ) {
//                    artists.forEach { artist ->
//                        DropdownMenuItem(
//                            text = { Text(artist.name) },
//                            onClick = {
//                                selected = artist.name
//                                artistExpanded = false
//                            },
//                            modifier = Modifier.fillMaxWidth(),
//                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
//                        )
//                    }
//                }
//            }
//            TextButton(
//                onClick = {
//                    scope.launch { withContext(IO) { app.settings.updateArtist(selected) } }
//                    onDismissRequest()
//                },
//                modifier = Modifier
//                    .padding(end = 10.dp)
//                    .align(Alignment.End)
//            ) {
//                Text("Confirm")
//            }
//        }
//    }
//}

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