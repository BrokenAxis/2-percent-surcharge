package surcharge.ui.manage

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Size
import surcharge.utils.components.gallery.Tab
import surcharge.utils.components.gallery.TabGallery
import surcharge.utils.formatPrice
import surcharge.utils.intPrice
import surcharge.utils.validatePrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMenu(
    app: AppContainer, onBack: () -> Unit = {}
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
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AddPrint(
                onClose = { openAddPrintDialog = false },
                onConfirm = {
                    openAddPrintDialog = false
                    scope.launch {
                        withContext(IO) {
                            app.data.addPrint(print)
                        }
                    }
                    scope.launch {
                        withContext(IO) {
                            snackbarHostState.showSnackbar("Print Added!")
                        }
                    }
                    refresh++
                },
                app = app,
                snackbarHostState = snackbarHostState,
                print = print
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
                        withContext(IO) {
                            app.data.addBundle(bundle)
                        }
                    }
                    scope.launch {
                        withContext(IO) {
                            snackbarHostState.showSnackbar("Bundle Added!")
                        }
                    }
                },
                data = app.data,
                bundle = bundle
            )
        }
    }

    var viewPrint by remember { mutableStateOf(false) }
    if (viewPrint) {
        Dialog(
            onDismissRequest = { viewPrint = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ViewPrint(
                onClose = { viewPrint = false },
                onEdit = {
                    refresh++
                    viewPrint = false
                    scope.launch {
                        withContext(IO) {
                            app.data.editPrint(print)
                        }
                    }
                    scope.launch {
                        withContext(IO) {
                            snackbarHostState.showSnackbar("Print Edited!")
                        }
                    }
                },
                onArchive = {
                    refresh++
                    viewPrint = false
                },
                data = app.data,
                print = print
            )
        }
    }

    var viewBundle by remember { mutableStateOf(false) }
    if (viewBundle) {
        Dialog(
            onDismissRequest = { viewBundle = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ViewBundle(
                onClose = { viewBundle = false },
                onEdit = {
                    refresh++
                    viewBundle = false
                    scope.launch {
                        withContext(IO) {
                            app.data.editBundle(bundle)
                        }
                    }
                    scope.launch {
                        withContext(IO) {
                            snackbarHostState.showSnackbar("Bundle Edited!")
                        }
                    }
                },
                onArchive = {
                    refresh++
                    viewBundle = false
                },
                data = app.data,
                bundle = bundle
            )
        }
    }

    var viewCash by remember { mutableStateOf(false) }
    if (viewCash) {
        Dialog(onDismissRequest = { viewCash = false }) {
            ElevatedCard {
                var cash by remember { mutableStateOf("") }
                LaunchedEffect(true) {
                    withContext(IO) { cash = formatPrice(app.settings.readCash()) }
                }

                TextField(
                    value = cash,
                    onValueChange = {
                        if (validatePrice(it)) {
                            cash = it
                        }
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally),
                    label = { Text("Cash On Hand") },
                    prefix = { Text("$ ") },
                    supportingText = { Text("Update amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                TextButton(
                    onClick = {
                        scope.launch {
                            withContext(IO) {
                                app.settings.updateCash(intPrice(cash))
                            }
                        }
                        viewCash = false
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Confirm")
                }
            }
        }
    }

    var viewDefaultPrice by remember { mutableStateOf(false) }
    if (viewDefaultPrice) {
        Dialog(
            onDismissRequest = { viewDefaultPrice = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ElevatedCard {
                Text(
                    text = "Default Print Prices",
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                val sizes = Size.entries.toTypedArray()
                val prices = remember { List(sizes.size) { formatPrice(0) }.toMutableStateList() }
                LaunchedEffect(true) {
                    withContext(IO) {
                        val default =
                            app.settings.readDefaultPrices().mapValues { formatPrice(it.value) }
                        default.forEach { prices[it.key.ordinal] = it.value }
                    }
                }

                val isError = remember { List(Size.entries.size) { false }.toMutableStateList() }

                prices.forEachIndexed { index, price ->
                    TextField(
                        value = prices[index],
                        onValueChange = {
                            prices[index] = it
                            isError[index] = !validatePrice(it)
                        },
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally),
                        label = { Text(sizes[index].name) },
                        prefix = { Text("$ ") },
                        supportingText = { Text("Update amount") },
                        isError = isError[index],
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                TextButton(
                    onClick = {
                        scope.launch {
                            withContext(IO) {
                                val success =
                                    app.settings.updateDefaultPrices(prices.map { intPrice(it) }
                                        .mapIndexed { index, price -> sizes[index] to price }
                                        .toMap())

                                snackbarHostState.showSnackbar(success.toString())
                            }
                        }
                        viewDefaultPrice = false
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = !isError.contains(true)
                ) {
                    Text("Confirm")
                }
            }
        }
    }

    var viewChangePrice by remember { mutableStateOf(false) }
    if (viewChangePrice) {
        Dialog(
            onDismissRequest = { viewChangePrice = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ElevatedCard {
                Text(
                    text = "Change All Print Prices",
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                var selectedSize by remember { mutableStateOf(Size.A5) }

                SingleChoiceSegmentedButtonRow(Modifier.padding(horizontal = 20.dp)) {
                    Size.entries.forEachIndexed { index, size ->
                        SegmentedButton(
                            selected = index == selectedSize.ordinal,
                            onClick = {
                                selectedSize = Size.entries.toTypedArray()[index]
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index, count = Size.entries.size
                            )
                        ) {
                            Text(size.name)
                        }
                    }
                }

                var price by remember { mutableStateOf("") }
                var isError by remember { mutableStateOf(true) }

                TextField(
                    value = price,
                    onValueChange = {
                        price = it
                        isError = !validatePrice(it)
                    },
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally),
                    label = { Text("Price") },
                    prefix = { Text("$ ") },
                    supportingText = { Text("Update amount") },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                TextButton(
                    onClick = {
                        scope.launch(IO) {
                            val prints = app.data.getPrints().getOrDefault(listOf())
                            prints.forEach { print ->
                                if (print.sizes.contains(selectedSize)) {
                                    print.price[selectedSize] = intPrice(price)
                                    app.data.editPrint(print)
                                }
                            }
                            refresh++
                        }
                        viewChangePrice = false
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = !isError
                ) {
                    Text("Confirm")
                }
            }
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
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { viewCash = true }) {
                        Icon(Icons.Filled.Money, "Cash On Hand")
                    }
                    IconButton(onClick = { viewDefaultPrice = true }) {
                        Icon(Icons.Filled.PriceChange, "Change Default Price")
                    }
                    IconButton(onClick = { viewChangePrice = true }) {
                        Icon(Icons.Filled.Edit, "Change Print Price")
                    }
//            IconButton(onClick = { /* do something */ }) {
//                Icon(Icons.Filled.FilterAlt, contentDescription = "Filter")
//            }
//            IconButton(onClick = { /* do something */ }) {
//                Icon(
//                    Icons.Filled.Search,
//                    contentDescription = "Artist",
//                )
//            }
                },
                floatingActionButton = {
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
                },
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        key(refresh) {
            TabGallery(
                data = app.data,
                onSwitchTab = {
                    tab = it
                },
                printOnClick = {
                    print = it
                    viewPrint = true
                },
                bundleOnClick = {
                    bundle = it
                    viewBundle = true
                },
                innerPadding = innerPadding
            )
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//private fun Prev() {
//    EditMenu()
//}