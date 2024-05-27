package surcharge.ui.manage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.data.prints.TempData
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.utils.components.gallery.Tab
import surcharge.utils.components.gallery.TabGallery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMenu(
    data: Data = TempData(), onBack: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var refresh by remember { mutableIntStateOf(0) }
    var tab by remember { mutableStateOf(Tab.Print) }

    var openAddPrintDialog by remember { mutableStateOf(false) }
    var print by remember { mutableStateOf(Print()) }
    if (openAddPrintDialog) {
        Dialog(
            onDismissRequest = { openAddPrintDialog = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AddPrint({ openAddPrintDialog = false }, {
                openAddPrintDialog = false
                scope.launch {
                    withContext(Dispatchers.IO) {
                        data.addPrint(print)
                    }
                }
                scope.launch {
                    withContext(Dispatchers.IO) {
                        snackbarHostState.showSnackbar("Print Added!")
                    }
                }
                refresh++
            }, data, print
            )
        }
    }

    var openAddBundleDialog by remember { mutableStateOf(false) }
    var bundle by remember { mutableStateOf(Bundle()) }
    if (openAddBundleDialog) {
        Dialog(
            onDismissRequest = { openAddBundleDialog = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AddBundle(
                onClose = { openAddBundleDialog = false },
                onConfirm = {
                    refresh++
                    openAddBundleDialog = false
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            data.addBundle(bundle)
                        }
                    }
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            snackbarHostState.showSnackbar("Bundle Added!")
                        }
                    }
                },
                data = data,
                bundle = bundle
            )
        }
    }

    var viewPrint by remember { mutableStateOf(false) }
    if (viewPrint) {
        Dialog(
            onDismissRequest = { viewPrint = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ViewPrint({ viewPrint = false }, {
                refresh++
                viewPrint = false
                scope.launch {
                    withContext(Dispatchers.IO) {
                        data.editPrint(print)
                    }
                }
                scope.launch {
                    withContext(Dispatchers.IO) {
                        snackbarHostState.showSnackbar("Print Edited!")
                    }
                }
            }, data, print
            )
        }
    }

    var viewBundle by remember { mutableStateOf(false) }
    if (viewBundle) {
        Dialog(
            onDismissRequest = { viewBundle = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ViewBundle({ viewBundle = false }, {
                refresh++
                viewBundle = false
                scope.launch {
                    withContext(Dispatchers.IO) {
                        data.editBundle(bundle)
                    }
                }
                scope.launch {
                    withContext(Dispatchers.IO) {
                        snackbarHostState.showSnackbar("Bundle Edited!")
                    }
                }
            }, data, bundle
            )
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Edit Products") },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            }
        )
    }, bottomBar = {
        BottomAppBar(actions = {
//            IconButton(onClick = { /* do something */ }) {
//                Icon(Icons.Filled.FilterAlt, contentDescription = "Filter")
//            }
//            IconButton(onClick = { /* do something */ }) {
//                Icon(
//                    Icons.Filled.Search,
//                    contentDescription = "Artist",
//                )
//            }
        }, floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    when (tab) {
                        Tab.Print -> openAddPrintDialog = true
                        Tab.Bundle -> openAddBundleDialog = true
                    }
                },
            ) {
                Icon(Icons.Filled.Add, "Add Print")
                Text(
                    text = when (tab) {
                        Tab.Print -> "Add Print"
                        Tab.Bundle -> "Add Bundle"
                    }
                )
            }
        }, containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        key(refresh) {
            TabGallery(data, onSwitchTab = {
                tab = it
            }, printOnClick = {
                print = it
                viewPrint = true
            }, bundleOnClick = {
                bundle = it
                viewBundle = true
            }, innerPadding = innerPadding
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Prev() {
    EditMenu()
}