package surcharge.ui.pointOfSale

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import surcharge.data.prints.Prints
import surcharge.data.prints.PrintsImpl
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale
import surcharge.types.Size
import surcharge.types.createBundleItem
import surcharge.types.createPrintItem
import surcharge.ui.PrintLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesMenu(
    prints: Prints = PrintsImpl(), onBack: () -> Unit = {}
) {
    var openCartDialog by remember { mutableStateOf(false) }
    var openPrintDialog by remember { mutableStateOf(false) }
    var print by remember { mutableStateOf(Print()) }
    var openBundleDialog by remember { mutableStateOf(false) }
    var bundleClicked by remember { mutableStateOf(false) }
    var bundle by remember { mutableStateOf(Bundle()) }
    var sale by remember { mutableStateOf(Sale()) }
    var checkoutComplete by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (openCartDialog) {
        Dialog(
            onDismissRequest = { openCartDialog = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Cart({ openCartDialog = false }, {
                checkoutComplete = true
                scope.launch { snackbarHostState.showSnackbar("Transaction Completed!") }
            }, sale)
        }
    }

    if (checkoutComplete) {
        // TODO update sales data
        sale = Sale()
        checkoutComplete = false
        openCartDialog = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Menu") },
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
            BottomAppBar(actions = {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Filled.FilterAlt, contentDescription = "Filter")
                }
                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Artist",
                    )
                }
            }, floatingActionButton = {
                FloatingActionButton(onClick = { openCartDialog = true }) {
                    Icon(Icons.Filled.ShoppingCart, "Cart")
                }
            })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        PrintLayout(
            data = prints,
            printOnClick = {
                openPrintDialog = true
                print = it
            },
            bundleOnClick = {
                bundleClicked = true
                bundle = it
            },
            bundleOnLongPress = {
                openBundleDialog = true
                bundle = it
            },
            innerPadding = innerPadding
        )

        if (openPrintDialog) {
            BasicAlertDialog(onDismissRequest = { openPrintDialog = false }) {
                var selectedSize by remember { mutableStateOf(Size.A3) }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large,
                    tonalElevation = AlertDialogDefaults.TonalElevation
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(print.name, style = MaterialTheme.typography.headlineLarge)

                        Spacer(Modifier.height(30.dp))

                        SingleChoiceSegmentedButtonRow {
                            print.sizes.forEachIndexed { index, size ->
                                SegmentedButton(
                                    selected = index == print.sizes.indexOf(selectedSize),
                                    onClick = { selectedSize = print.sizes[index] },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index, count = print.sizes.size
                                    )
                                ) {
                                    Text(size.toString())
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        TextButton(
                            onClick = {
                                openPrintDialog = false
                                sale.items.add(createPrintItem(print, selectedSize))
                                sale.price += sale.items.last().price
                            }, modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }

        // clicking on a bundle adds it to the cart, no dialog
        if (bundleClicked) {
            val bundleIndex = sale.items.indexOfFirst { it.name == bundle.name }
            if (bundleIndex != -1) {
                sale.items[bundleIndex].quantity += 1
            } else {
                sale.items.add(createBundleItem(bundle))
            }
            sale.price += bundle.price
            bundleClicked = false
        }

        // long press for quantity, apply discount etc

    }

}

@Preview
@Composable
private fun Prev() {
    SalesMenu()
}