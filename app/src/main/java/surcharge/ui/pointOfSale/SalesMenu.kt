package surcharge.ui.pointOfSale

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.squareup.sdk.mobilepayments.payment.Payment
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.data.prints.Firestore
import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.BundleItem
import surcharge.types.Print
import surcharge.types.Sale
import surcharge.types.createBundleItem
import surcharge.types.createPrintItem
import surcharge.utils.components.gallery.PrintImage
import surcharge.utils.components.gallery.TabGallery
import surcharge.utils.quantity
import surcharge.utils.square.observeReaderChanges
import surcharge.utils.square.showSettings
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
    var search by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(false) }
    var selectedBundle by remember { mutableStateOf(Bundle()) }
    var sale by remember { mutableStateOf(Sale()) }
    var refresh by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val readerCallback = remember { observeReaderChanges(snackbarHostState, scope) }

    var artists by remember { mutableStateOf(listOf<Artist>()) }
    var prints by remember { mutableStateOf(listOf<Print>()) }
    LaunchedEffect(true) {
        withContext(IO) {
            autoDiscount = app.settings.readDiscount()
            artists = app.data.getArtists().getOrDefault(listOf())
            prints = (app.data as Firestore).getCachedPrints().getOrDefault(listOf())
        }
    }

    if (filter) {
        Dialog(
            onDismissRequest = { filter = false }
        ) {
            Card(
                Modifier
                    .height(500.dp)
                    .fillMaxWidth()
            ) {
                val properties = prints.map { it.property }.toSet().toList()
                val propertySelected = remember { mutableStateListOf<Boolean>() }
                properties.forEach { _ -> propertySelected.add(false) }

                val artistSelected = remember { mutableStateListOf<Boolean>() }
                artists.forEach { _ -> artistSelected.add(false) }

                LazyHorizontalStaggeredGrid(
                    rows = StaggeredGridCells.Adaptive(25.dp),
                    modifier = Modifier
                        .heightIn(80.dp, 160.dp)
                        .padding(10.dp),
                    horizontalItemSpacing = 10.dp,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(artists.size) { idx ->
                        FilterChip(
                            selected = artistSelected[idx],
                            onClick = { artistSelected[idx] = !artistSelected[idx] },
                            label = { Text(artists[idx].name) }
                        )
                    }
                    items(properties.size) { idx ->
                        FilterChip(
                            selected = propertySelected[idx],
                            onClick = { propertySelected[idx] = !propertySelected[idx] },
                            label = { Text(properties[idx]) })
                    }
                }
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    prints.forEach { print ->
                        key(
                            propertySelected[properties.indexOf(print.property)],
                            artistSelected[artists.indexOfFirst { it.name == print.artist }]
                        ) {
                            if ((propertySelected[properties.indexOf(print.property)] && artistSelected[artists.indexOfFirst { it.name == print.artist }])
                                || (propertySelected[properties.indexOf(print.property)] && !artistSelected.contains(
                                    true
                                ))
                                || (!propertySelected.contains(true) && artistSelected[artists.indexOfFirst { it.name == print.artist }])
                            ) {
                                ListItem(
                                    headlineContent = { Text(print.name) },
                                    modifier = Modifier
                                        .clickable {
                                            openPrintDialog = true
                                            selectedPrint = print
                                        }
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 2.dp),
                                    supportingContent = { Text(print.sizes.toString()) },
                                    leadingContent = {
                                        PrintImage(
                                            print.url,
                                            Modifier
                                                .size(50.dp)
                                                .clip(CircleShape),
                                            ContentScale.Crop
                                        )
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)

                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (search) {
        var query by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(true) }
        Dialog(
            onDismissRequest = { search = false }
        ) {
            Card(Modifier.height(400.dp)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .semantics { isTraversalGroup = true }) {
                    SearchBar(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .semantics { traversalIndex = 0f },
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = query,
                                onQueryChange = { query = it },
                                onSearch = { expanded = false },
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = { Text("search and ye shall find") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "search"
                                    )
                                },
                            )
                        },
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            repeat(4) { idx ->
                                val resultText = "Suggestion $idx"
                                ListItem(
                                    headlineContent = { Text(resultText) },
                                    supportingContent = { Text("Additional info") },
                                    leadingContent = {
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = null
                                        )
                                    },
                                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                    modifier =
                                    Modifier
                                        .clickable {
                                            query = resultText
                                            expanded = false
                                        }
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 72.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.semantics { traversalIndex = 1f },
                    ) {
                        val list = List(100) { "Text $it" }
                        items(count = list.size) {
                            Text(
                                text = list[it],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    if (openCartDialog) {
        Dialog(
            onDismissRequest = { openCartDialog = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Cart(
                onClose = { openCartDialog = false },
                onCheckout = { payment ->
                    sale.time = Instant.now().toString()
                    scope.launch {
                        if (payment != null) {
                            val cardPayment = (payment as Payment.OnlinePayment).cardDetails!!
                            val card = cardPayment.card
                            val paymentInfo = "Card Transaction:\n" +
                                    "${card.cardholderName}\n" +
                                    "${card.brand} ${card.lastFourDigits}\n" +
                                    "Authorisation  ${cardPayment.authorizationCode}\n" +
                                    cardPayment.applicationName + "\n" +
                                    "AID: ${cardPayment.applicationId}" +
                                    cardPayment.entryMethod

                            sale.comment = paymentInfo
                        }

                        withContext(IO) {
                            app.data.addSale(sale)

                            sale.prints.forEach { item ->
                                val print = app.data.getPrint(item.name).getOrElse {
                                    snackbarHostState.showSnackbar("Error: ${it.message}")
                                    Print()
                                }

                                print.stock[item.size.toString()] =
                                    print.stock[item.size.toString()]!! - item.quantity
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

                                    print.stock[item.size.toString()] =
                                        print.stock[item.size.toString()]!! - (bundle.quantity * item.quantity)
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
            IconButton(onClick = { filter = true }) {
                Icon(Icons.Filled.FilterAlt, contentDescription = "Filter")
            }
//            IconButton(onClick = { search = true }) {
//                Icon(
//                    Icons.Filled.Search,
//                    contentDescription = "Search",
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

            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        false
                    ) -> {
                        // Precise location access granted.
                        scope.launch(IO) { snackbarHostState.showSnackbar("Precise location access granted") }
                        showSettings(scope, snackbarHostState)
                    }

                    permissions.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        false
                    ) -> {
                        // Only approximate location access granted.
                        scope.launch(IO) { snackbarHostState.showSnackbar("Approximate location access granted") }
                        showSettings(scope, snackbarHostState)
                    }

                    permissions.getOrDefault(
                        Manifest.permission.BLUETOOTH,
                        false
                    ) -> {
                        // Only approximate location access granted.
                        scope.launch(IO) { snackbarHostState.showSnackbar("Approximate location access granted") }
                        showSettings(scope, snackbarHostState)
                    }

                    else -> {
                        // No location access granted.
                        scope.launch(IO) { snackbarHostState.showSnackbar("Location access is required for square payments") }
                    }
                }
            }
            IconButton(onClick = {

                var perms = arrayOf<String>()
                if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    perms += arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    && (context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                            || context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                ) {
                    perms += arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    )

                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                    && context.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                ) {
                    perms += arrayOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                    )
                }
                if (perms.isNotEmpty()) {
                    launcher.launch(perms)
                } else {
                    showSettings(scope, snackbarHostState)
                }


            }) {
                Icon(
                    Icons.Filled.CropSquare,
                    contentDescription = "Square Settings",
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
                                    sale.prints.indexOfFirst { it.name == selectedPrint.name && it.size == selectedSize && it.price == selectedPrint.price[selectedSize.toString()] }
                                if (printIndex != -1) {
                                    sale.prints[printIndex].quantity++
                                } else {
                                    sale.prints.add(createPrintItem(selectedPrint, selectedSize))
                                }
                                sale.price += selectedPrint.price[selectedSize.toString()]!!

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