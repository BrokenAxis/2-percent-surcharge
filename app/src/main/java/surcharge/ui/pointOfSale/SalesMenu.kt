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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.data.prints.TempData
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale
import surcharge.types.createBundleItem
import surcharge.types.createPrintItem
import surcharge.utils.components.gallery.TabGallery
import surcharge.utils.quantity
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesMenu(
    data: Data = TempData(), onBack: () -> Unit = {}
) {
    var openCartDialog by remember { mutableStateOf(false) }
    var openPrintDialog by remember { mutableStateOf(false) }
    var print by remember { mutableStateOf(Print()) }
    var openBundleDialog by remember { mutableStateOf(false) }
    var bundleClicked by remember { mutableStateOf(false) }
    var bundle by remember { mutableStateOf(Bundle()) }
    var sale by remember { mutableStateOf(Sale()) }
    var refresh by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (openCartDialog) {
        Dialog(
            onDismissRequest = { openCartDialog = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Cart(onClose = { openCartDialog = false }, onCheckout = {
                sale.time = Instant.now()
                scope.launch {
                    withContext(Dispatchers.IO) {
                        data.addSale(sale)
                        sale = Sale()
                    }
                }
                scope.launch {
                    withContext(Dispatchers.IO) {
                        snackbarHostState.showSnackbar("Transaction Completed!")
                    }
                }
                openCartDialog = false
            }, onCheckoutError = { error ->
                scope.launch {
                    withContext(Dispatchers.IO) {
                        snackbarHostState.showSnackbar("Error with card checkout!: \n $error")
                    }
                }
            }, sale = sale
            )
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Sales Menu") },
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
//            IconButton(onClick = { /* TODO */ }) {
//                Icon(Icons.Filled.FilterAlt, contentDescription = "Filter")
//            }
//            IconButton(onClick = { /* TODO */ }) {
//                Icon(
//                    Icons.Filled.Search,
//                    contentDescription = "Artist",
//                )
//            }
            IconButton(onClick = {
                sale = Sale()
                scope.launch {
                    withContext(Dispatchers.IO) {
                        snackbarHostState.showSnackbar("Cart Deleted!")
                    }
                }
            }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete Cart",
                )
            }
        }, floatingActionButton = {
            BadgedBox(badge = {
                Badge {
                    key(refresh) { Text(quantity(sale).toString()) }
                }
            }) {
                FloatingActionButton(onClick = { openCartDialog = true }) {
                    Icon(Icons.Filled.ShoppingCart, "Cart")
                }
            }
        }, containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        TabGallery(data = data, printOnClick = {
            openPrintDialog = true
            print = it
        }, bundleOnClick = {
            bundleClicked = true
            bundle = it
        }, bundleOnLongPress = {
            openBundleDialog = true
            bundle = it
        }, innerPadding = innerPadding
        )

        if (openPrintDialog) {
            BasicAlertDialog(onDismissRequest = { openPrintDialog = false }) {
                var selectedSize by remember { mutableStateOf(print.sizes.first()) }

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
                                val printIndex =
                                    sale.prints.indexOfFirst { it.name == print.name && it.size == selectedSize && it.price == print.price[selectedSize] }
                                if (printIndex != -1) {
                                    sale.prints[printIndex].quantity++
                                } else {
                                    sale.prints.add(createPrintItem(print, selectedSize))
                                }
                                sale.price += print.price[selectedSize]!!
                                refresh++
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
            val bundleIndex =
                sale.bundles.indexOfFirst { it.name == bundle.name && it.price == bundle.price }
            if (bundleIndex != -1) {
                sale.bundles[bundleIndex].quantity++
            } else {
                sale.bundles.add(createBundleItem(bundle))
            }
            sale.price += bundle.price
            refresh++
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