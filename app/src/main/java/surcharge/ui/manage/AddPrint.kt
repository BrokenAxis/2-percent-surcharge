package surcharge.ui.manage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.types.Artist
import surcharge.types.Print
import surcharge.types.Size
import surcharge.utils.debounce.debounced
import surcharge.utils.formatPrice
import surcharge.utils.img.upload
import surcharge.utils.intPrice
import surcharge.utils.validatePrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrint(
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    app: AppContainer,
    snackbarHostState: SnackbarHostState,
    print: Print
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 20.dp, top = 10.dp)
        ) {
            Icon(Icons.Filled.Image, "Print")
            Text(
                text = "Add a Print",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, "Close")
            }
        }
        HorizontalDivider()

        var name by remember { mutableStateOf("") }

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Print Name") },
            placeholder = { Text("Yeah, Mona Lisa, ayy") },
            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth(),
        )

        var artists by remember { mutableStateOf(listOf<Artist>()) }

        LaunchedEffect(true) {
            withContext(IO) {
                artists = app.data.getArtists().getOrDefault(listOf())
            }
        }

        var artistExpanded by remember { mutableStateOf(false) }

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
                value = print.artist,
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
                            print.artist = artist.name
                            artistExpanded = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        var property by remember { mutableStateOf("") }

        OutlinedTextField(
            value = property,
            onValueChange = { property = it },
            label = { Text("Property") },
            placeholder = { Text("original :)") },
            leadingIcon = { Icon(Icons.Filled.LocalOffer, contentDescription = null) },
            supportingText = { Text("Put 'original' or the IP of the fan-art here") },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth()
        )

        val selectedSizes = remember { mutableStateListOf<Size>() }

        val sizes = Size.entries.toTypedArray()

        MultiChoiceSegmentedButtonRow(Modifier.padding(horizontal = 20.dp)) {
            sizes.forEachIndexed { index, size ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = sizes.size
                    ),
                    onCheckedChange = {
                        if (sizes[index] in selectedSizes) {
                            selectedSizes.remove(sizes[index])
                        } else {
                            selectedSizes.add(sizes[index])
                        }
                    },
                    checked = sizes[index] in selectedSizes
                ) {
                    Text(size.toString())
                }
            }
        }

        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val image = remember { mutableStateOf("") }
        val progress = remember { mutableDoubleStateOf(0.0) }
        var success by remember { mutableStateOf(false) }
        var sizeDialog by remember { mutableStateOf(false) }

        if (sizeDialog) {
            SizeDialog(
                app = app,
                print = print,
                onConfirm = {
                    sizeDialog = false
                    onConfirm()
                },
                onClose = { sizeDialog = false })
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Card(modifier = Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp)) {
                    Text(
                        text = "Preview",
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 5.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(.75f),
                        shape = RectangleShape,
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
                                contentScale = ContentScale.Fit
                            )
                        }
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
                    } else if (success) {
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
                        artist = print.artist,
                        name = name,
                        url = image,
                        progress = progress,
                        onSuccess = { success = true },
                        scope = scope,
                        snackbar = snackbarHostState
                    )
                }
            }

            FilledTonalButton(
                onClick = {
                    image.value = ""
                    success = false
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier
                    .padding(start = 30.dp, bottom = 30.dp)
                    .align(Alignment.BottomStart),
                enabled = print.artist.isNotEmpty() && name.isNotEmpty()
            ) {
                Text("Upload Image")
            }

            FloatingActionButton(
                onClick = {
                    if (name.isNotEmpty()
                        && selectedSizes.isNotEmpty()
                        && print.artist.isNotEmpty()
                        && property.isNotEmpty()
                        && success
                    ) {
                        print.name = name
                        print.property = property
                        print.sizes = selectedSizes.toList()
                        print.url = image.value
                        sizeDialog = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .absoluteOffset(x = (-15).dp, y = (-15).dp)
            ) {
                Icon(Icons.Filled.Check, "Confirm")
            }
        }
    }
}

// dialog to handle setting price and stock for each size of the print
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeDialog(app: AppContainer, print: Print, onConfirm: () -> Unit, onClose: () -> Unit) {
    BasicAlertDialog(onDismissRequest = onClose) {
        Card(
            Modifier.padding(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                "Add Price and Stock by Size",
                Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyLarge
            )

            val isError = remember { List(print.sizes.size) { false }.toMutableStateList() }
            val prices = remember { List(print.sizes.size) { "0.00" }.toMutableStateList() }
            val stock = remember { List(print.sizes.size) { "0" }.toMutableStateList() }

            LaunchedEffect(true) {
                withContext(IO) {
                    val defaultPrices = app.settings.readDefaultPrices()

                    print.sizes.forEachIndexed { index, size ->
                        prices[index] = formatPrice(defaultPrices[size] ?: 0)
                    }
                }
            }

            prices.forEachIndexed { index, price ->
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        prices[index] = it
                        isError[index] = !(validatePrice(it) && stock[index].isDigitsOnly())
                    },
                    label = { Text("${print.sizes[index].name} Price") },
                    placeholder = { Text("69") },
                    prefix = { Text("$") },
                    leadingIcon = { Icon(Icons.Filled.Payments, contentDescription = "Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                )
                OutlinedTextField(
                    value = stock[index],
                    onValueChange = {
                        stock[index] = it
                        isError[index] = !(it.isDigitsOnly() && validatePrice(prices[index]))
                    },
                    label = { Text("${print.sizes[index].name} Stock") },
                    placeholder = { Text("69") },
                    prefix = { Text("x") },
                    leadingIcon = { Icon(Icons.Filled.Inventory, contentDescription = "Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                )
                if (index != prices.lastIndex) HorizontalDivider(Modifier.padding(horizontal = 10.dp))
            }
            TextButton(
                onClick = debounced {
                    print.sizes.forEachIndexed { index, size ->
                        print.price[size.toString()] = intPrice(prices[index])
                        print.stock[size.toString()] = stock[index].toInt()
                    }
                    onConfirm()
                },
                modifier = Modifier.align(Alignment.End),
                enabled = !isError.contains(true)
            ) {
                Text("Confirm")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AddPrintPreview() {
//    AddPrint({}, {}, TempData(), Print())
//}