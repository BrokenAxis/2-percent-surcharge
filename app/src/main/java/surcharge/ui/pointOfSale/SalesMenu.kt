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
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.squareup.sdk.pos.PosSdk
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.types.Bundle
import surcharge.types.BundleItem
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
    app: AppContainer, onBack: () -> Unit = {}
) {
    var openCartDialog by remember { mutableStateOf(false) }
    var openPrintDialog by remember { mutableStateOf(false) }
    var openDiscountDialog by remember { mutableStateOf(false) }
    var autoDiscount by remember { mutableStateOf(false) }
    var selectedPrint by remember { mutableStateOf(Print()) }
    var openBundleDialog by remember { mutableStateOf(false) }
    var bundleClicked by remember { mutableStateOf(false) }
    var selectedBundle by remember { mutableStateOf(Bundle()) }
    var sale by remember { mutableStateOf(Sale()) }
    var refresh by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var applicationId by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        withContext(IO) {
            applicationId = app.settings.readSquareID()
            autoDiscount = app.settings.readDiscount()
        }
    }

    if (openCartDialog) {
        Dialog(
            onDismissRequest = { openCartDialog = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Cart(
                onClose = { openCartDialog = false },
                onCheckout = {
                    sale.time = Instant.now()
                    scope.launch {
                        withContext(IO) {
                            app.data.addSale(sale)

                            sale.prints.forEach { item ->
                                val print = app.data.getPrint(item.name).getOrElse {
                                    snackbarHostState.showSnackbar("Error: ${it.message}")
                                    Print()
                                }

                                print.stock[item.size] = print.stock[item.size]!! - item.quantity
                                if (!app.data.editPrint(print)) {
                                    snackbarHostState.showSnackbar("Error: $print")
                                }
                            }

                            sale.bundles.forEach { bundle ->
                                bundle.prints.forEach { item ->
                                    val print = app.data.getPrint(item.name).getOrElse {
                                        snackbarHostState.showSnackbar("Error: ${it.message}")
                                        Print()
                                    }

                                    print.stock[item.size] =
                                        print.stock[item.size]!! - (bundle.quantity * item.quantity)
                                    if (!app.data.editPrint(print)) {
                                        snackbarHostState.showSnackbar("Error: $print")
                                    }
                                }
                            }
                            sale = Sale()
                        }
                        snackbarHostState.showSnackbar("Transaction Completed!")
                    }
                    openCartDialog = false
                },
                onCheckoutError = { error ->
                    scope.launch {
                        withContext(IO) {
                            snackbarHostState.showSnackbar("Error with card checkout!: \n $error")
                        }
                    }
                },
                posClient = PosSdk.createClient(LocalContext.current, applicationId),
                sale = sale,
                app = app
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
                    withContext(IO) {
                        snackbarHostState.showSnackbar("Cart Deleted!")
                    }
                }
            }) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete Cart",
                )
            }
            IconButton(onClick = {
                openDiscountDialog = true
            }) {
                Icon(
                    Icons.Filled.Discount,
                    contentDescription = "Create Bundle",
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
        TabGallery(
            data = app.data, printOnClick = {
            openPrintDialog = true
                selectedPrint = it
        }, bundleOnClick = {
            bundleClicked = true
                selectedBundle = it
                scope.launch {
                    withContext(IO) {
                        snackbarHostState.showSnackbar("Bundle Added to Cart!")
                    }
                }
        }, bundleOnLongPress = {
            openBundleDialog = true
                selectedBundle = it
        }, innerPadding = innerPadding
        )

        if (openPrintDialog) {
            BasicAlertDialog(onDismissRequest = { openPrintDialog = false }) {
                var selectedSize by remember { mutableStateOf(selectedPrint.sizes.first()) }

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
                        Text(selectedPrint.name, style = MaterialTheme.typography.headlineLarge)

                        Spacer(Modifier.height(30.dp))

                        SingleChoiceSegmentedButtonRow {
                            selectedPrint.sizes.forEachIndexed { index, size ->
                                SegmentedButton(
                                    selected = index == selectedPrint.sizes.indexOf(selectedSize),
                                    onClick = { selectedSize = selectedPrint.sizes[index] },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index, count = selectedPrint.sizes.size
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
                                    sale.prints.indexOfFirst { it.name == selectedPrint.name && it.size == selectedSize && it.price == selectedPrint.price[selectedSize] }
                                if (printIndex != -1) {
                                    sale.prints[printIndex].quantity++
                                } else {
                                    sale.prints.add(createPrintItem(selectedPrint, selectedSize))
                                }
                                sale.price += selectedPrint.price[selectedSize]!!

                                // buy two get one free auto magic
                                if (autoDiscount) {
                                    val sameSize = sale.prints.filter { it.size == selectedSize }
                                    var count = 0
                                    sameSize.forEach { count += it.quantity }
                                    if (count == 3) {
                                        val bundle = BundleItem(
                                            name = "Buy 2 get 1 free",
                                            prints = sameSize,
                                            quantity = 1,
                                            price = sameSize.first().price * 2
                                        )
                                        sale.prints.removeAll(sameSize.toSet())
                                        sale.bundles.add(bundle)
                                    }
                                }

                                refresh++

                                scope.launch {
                                    withContext(IO) {
                                        snackbarHostState.showSnackbar("Print Added to Cart!")
                                    }
                                }
                            }, modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }

        if (openDiscountDialog) {
            AlertDialog(
                onDismissRequest = { openDiscountDialog = false },
                title = { Text("Automatic Discount Application") },
                text = {
                    Text(
                        text = "Are you sure you want to enable automatic discount application? (Buy two get one free). Experimental feature",
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            openDiscountDialog = false
                            scope.launch {
                                withContext(IO) {
                                    app.settings.updateDiscount(true)
                                }
                            }
                            autoDiscount = true
                        }
                    ) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            openDiscountDialog = false
                            scope.launch {
                                withContext(IO) {
                                    app.settings.updateDiscount(false)
                                }
                            }
                            autoDiscount = false
                        }
                    ) {
                        Text("Disable")
                    }
                },
                icon = { Icon(Icons.Outlined.Discount, "") }
            )
        }

        // clicking on a bundle adds it to the cart, no dialog
        if (bundleClicked) {
            val bundleIndex =
                sale.bundles.indexOfFirst { it.name == selectedBundle.name && it.price == selectedBundle.price }
            if (bundleIndex != -1) {
                sale.bundles[bundleIndex].quantity++
            } else {
                sale.bundles.add(createBundleItem(selectedBundle))
            }
            sale.price += selectedBundle.price
            refresh++
            bundleClicked = false
        }
    }
}

//@Preview
//@Composable
//private fun Prev() {
//    SalesMenu()
//}