package surcharge.ui.settings

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.DatasetLinked
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.data.prints.LocalData
import surcharge.utils.components.Tile
import java.security.MessageDigest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevScreen(
    app: AppContainer,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer Options") },
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
            val scope = rememberCoroutineScope()

            Tile(title = "Delete Data",
                subtitle = "Wipe local data and reset app",
                icon = Icons.Filled.DeleteForever,
                onClick = { scope.launch { withContext(IO) { app.data.reset() } } }
            )

            Tile(title = "Load Test Data",
                subtitle = "Overwrite current database with a set of testing values",
                icon = Icons.Filled.DatasetLinked,
                onClick = { scope.launch { withContext(IO) { app.data.reload() } } }
            )

            val context = LocalContext.current

            var writePermission by remember { mutableStateOf(false) }
            writePermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            val l = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/x-sqlite3")
            ) {
                if (it != null) {
                    val file = context.contentResolver.openOutputStream(it)
                    val localData = context.getDatabasePath("local_data")
                    if (localData == null) {
                        scope.launch {
                            withContext(IO) {
                                snackbarHostState.showSnackbar("local_data not found")
                            }
                        }

                    } else {
                        if (!localData.canRead()) {
                            scope.launch {
                                withContext(IO) {
                                    snackbarHostState.showSnackbar("can't read local_data")
                                }
                            }
                        } else {
                            val localFile = localData.readBytes()
                            file?.write(localFile)
                        }
                    }
                    file?.close()
                }
            }

            Tile(title = "Export Database",
                subtitle = "Export most data to an external file",
                icon = Icons.Filled.ImportExport,
                onClick = {
                    l.launch("surcharge_db")
                }
            )

            val importLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
                    if (it != null) {
                        val file = context.contentResolver.openInputStream(it)
                        if (file != null) {
                            val newData = file.readBytes()
                            val localData = context.getDatabasePath("local_data")
                            if (!localData.canWrite()) {
                                scope.launch {
                                    withContext(IO) {
                                        snackbarHostState.showSnackbar("can't edit local_data")
                                    }
                                }
                            } else {
                                if (app.data is LocalData) (app.data as LocalData).close()
                                localData.writeBytes(newData)
                            }
                        }
                        file?.close()
                    }
                }
            Tile(title = "Import Database",
                subtitle = "Overwrite local data from an external file",
                icon = Icons.Filled.DatasetLinked,
                onClick = {
                    importLauncher.launch(arrayOf("*/*"))
                }
            )

            Tile(title = "Sync",
                subtitle = "Attempt to sync local data with backend",
                icon = Icons.Filled.Sync,
                onClick = { scope.launch { snackbarHostState.showSnackbar("Not implemented! Gaslight a certain goomba into working on it") } }
            )

            var viewSquare by remember { mutableStateOf(false) }
            if (viewSquare) {
                Dialog(onDismissRequest = { viewSquare = false }) {
                    ElevatedCard {

                        val packageName = LocalContext.current.packageName

                        val clipboard =
                            LocalContext.current.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                        OutlinedTextField(
                            value = packageName,
                            onValueChange = {},
                            modifier = Modifier.padding(20.dp),
                            readOnly = true,
                            label = { Text("Package Name") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    clipboard.setPrimaryClip(
                                        ClipData.newPlainText("package name", packageName)
                                    )
                                }) {
                                    Icon(Icons.Filled.ContentCopy, "")
                                }
                            }
                        )

                        val info = LocalContext.current.packageManager.getPackageInfo(
                            packageName,
                            PackageManager.GET_SIGNING_CERTIFICATES
                        )

                        if (info.signingInfo.hasMultipleSigners()) {
                            Text(info.signingInfo.apkContentsSigners.toString())
                        } else {
                            val signature = info.signingInfo.signingCertificateHistory.first()
                            val md = MessageDigest.getInstance("SHA1")
                            md.update(signature.toByteArray())
                            val digest = md.digest()
                            val toRet = StringBuilder()
                            for (i in digest.indices) {
                                if (i != 0) toRet.append(":")
                                val b = digest[i].toInt() and 0xff
                                val hex = Integer.toHexString(b).uppercase()
                                if (hex.length == 1) toRet.append("0")
                                toRet.append(hex)
                            }
                            val s = toRet.toString()

                            OutlinedTextField(
                                value = s,
                                onValueChange = {},
                                modifier = Modifier.padding(20.dp),
                                readOnly = true,
                                label = { Text("SHA1 Fingerprint") },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        clipboard.setPrimaryClip(
                                            ClipData.newPlainText("fingerprint", s)
                                        )
                                    }) {
                                        Icon(Icons.Filled.ContentCopy, "")
                                    }
                                },
                                supportingText = { Text("An Android package tied to your Square account must match this package name and fingerprint") }
                            )

                            var id by remember { mutableStateOf("") }

                            LaunchedEffect(true) {
                                withContext(IO) {
                                    id = app.settings.readSquareID()
                                }
                            }

                            TextField(
                                value = id,
                                onValueChange = { id = it },
                                modifier = Modifier.padding(20.dp),
                                label = { Text("Square Application ID") },
                                supportingText = { Text("Change this to match the 'Production Application ID' of your Square Developer Application") }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            withContext(IO) {
                                                app.settings.updateSquareID(id)
                                            }
                                        }
                                    }
                                ) {
                                    Text("Confirm ID Change")
                                }

                                TextButton(onClick = { viewSquare = false }) {
                                    Text("Close")
                                }
                            }


                        }
                    }
                }
            }

            Tile(title = "Square Integration",
                subtitle = "View SHA-1 hash, change Square application ID",
                icon = Icons.Filled.CropSquare,
                onClick = { viewSquare = true }
            )

        }

    }
}