package surcharge.ui.manage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import surcharge.data.prints.Prints
import surcharge.data.prints.PrintsImpl
import surcharge.types.Print
import surcharge.ui.PrintLayout
import surcharge.ui.pointOfSale.Cart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMenu(
    prints: Prints = PrintsImpl(),
    onBack: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var addItem by remember { mutableStateOf(false) }

    var openAddPrintDialog by remember { mutableStateOf(false) }
    var print by remember { mutableStateOf(Print()) }
    if (openAddPrintDialog) {
        Dialog(
            onDismissRequest = { openAddPrintDialog = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AddPrint(
                { openAddPrintDialog = false },
                {
                    addItem = true
                    scope.launch { snackbarHostState.showSnackbar("Print Added!") }
                },
                prints,
                print
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Products") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { TODO() }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.FilterAlt, contentDescription = "Filter")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Artist",
                        )
                    }
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = { openAddPrintDialog = true },
                    ) {
                        Icon(Icons.Filled.Add, "Add Print")
                        Text(text = "Add Print")
                    }
                }
            )
        },
    ) { innerPadding ->
        PrintLayout(prints, innerPadding = innerPadding)
    }
}


@Preview(showBackground = true)
@Composable
private fun Prev() {
    EditMenu()
}