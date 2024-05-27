package surcharge.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DatasetLinked
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.utils.components.Tile

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
            var viewPersonalisation by remember { mutableStateOf(false) }

            if (viewPersonalisation) {
                Dialog(
                    onDismissRequest = { viewPersonalisation = false },
                    DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Personalisation(app = app, onClose = { viewPersonalisation = false })
                }
            }

            val scope = rememberCoroutineScope()

            Tile(title = "Delete Data",
                subtitle = "Wipe local data and reset app",
                icon = Icons.Filled.DeleteForever,
                onClick = { scope.launch { withContext(Dispatchers.IO) { app.data.reset() } } })

            Tile(title = "Load Test Data",
                subtitle = "Overwrite current database with a set of testing values",
                icon = Icons.Filled.DatasetLinked,
                onClick = { scope.launch { withContext(Dispatchers.IO) { app.data.reload() } } })

            Tile(title = "Sync",
                subtitle = "Attempt to sync local data with backend",
                icon = Icons.Filled.Sync,
                onClick = { scope.launch { snackbarHostState.showSnackbar("Not implemented! Gaslight a certain goomba into working on it") } })

        }

    }
}