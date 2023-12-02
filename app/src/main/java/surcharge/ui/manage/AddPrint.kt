package surcharge.ui.manage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import surcharge.data.prints.Prints
import surcharge.data.prints.PrintsImpl
import surcharge.types.Artist
import surcharge.types.Print
import surcharge.types.Size
import surcharge.ui.PrintImage
import surcharge.utils.formatPrice
import surcharge.utils.intPrice
import surcharge.utils.validatePrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPrint(
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    data: Prints,
    print: Print
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        var sizeDialog by remember { mutableStateOf(false) }

        if (sizeDialog) {
            SizeDialog(print = print, onConfirm = { /*TODO*/ }, onClose = { sizeDialog = false })
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp)) {
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
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        OutlinedTextField(
            value = print.name,
            onValueChange = { print.name = it },
            label = { Text("Print Name") },
            placeholder = { Text("Yeah, Mona Lisa, ayy") },
            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth(),
        )

        var artists by remember { mutableStateOf(listOf<Artist>()) }

        LaunchedEffect(true) {
            artists = data.getArtists().getOrDefault(listOf())
        }

        var artistExpanded by remember { mutableStateOf(false) }
        var selectedArtist by remember { mutableStateOf(Artist("Artist")) }
        ExposedDropdownMenuBox(
            expanded = artistExpanded,
            onExpandedChange = { artistExpanded = !artistExpanded },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = selectedArtist.name,
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
                            selectedArtist = artist
                            artistExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }

        OutlinedTextField(
            value = print.property,
            onValueChange = { print.property = it },
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

        Text(
            "Sizes",
            Modifier.padding(horizontal = 20.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(10.dp))

        val selectedSizes = remember { mutableStateListOf<Size>() }

        val sizes = Size.values()

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
        var showPreview by remember { mutableStateOf(false) }
        var image by remember { mutableStateOf("") }
        if (showPreview) {
            Card(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(20.dp)
            ) {
                Card(Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                Text(
                    "Preview",
                    Modifier.padding(10.dp),
                    fontWeight = FontWeight.Bold
                )
            }

        }

        val launcher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            )
            { uri: Uri? ->
                imageUri = uri
                showPreview = true
            }

        FilledTonalButton(
            onClick = {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
        ) {
            Text("Upload Image")
        }
        Row(horizontalArrangement = Arrangement.End) {
            Spacer(modifier = Modifier.weight(1f))
            FloatingActionButton(
                onClick = {
                    // TODO: disable while form incomplete/invalid
                    print.sizes = selectedSizes.toList()
                    sizeDialog = true
                },
                modifier = Modifier.padding(30.dp)
            ) {
                Icon(
                    Icons.Outlined.Check,
                    "Confirm Button"
                )
            }
        }
    }
}

// dialog to handle setting price and stock for each size of the print
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeDialog(print: Print, onConfirm: () -> Unit, onClose: () -> Unit) {
    BasicAlertDialog(onDismissRequest = onClose) {
        Card(
            Modifier.padding(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {

            var isError by remember { mutableStateOf(false) }
            val prices = remember { mutableListOf<String>() }
            val stock = remember { mutableListOf<String>() }
            print.sizes.forEach { _ ->
                prices.add("0.00")
                stock.add("0")
            }
            prices.forEachIndexed { index, price ->
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        prices[index] = it
                        isError = !validatePrice(price)
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
                        isError = !it.isDigitsOnly()
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
            }
            TextButton(
                onClick = {
                    print.sizes.forEachIndexed { index, size ->
                        print.price[size] = intPrice(prices[index])
                        print.stock[size] = stock[index].toInt()
                    }
                    onConfirm()
                },
                enabled = !isError
            ) {
                Text("Confirm")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPrintPreview() {
    AddPrint({}, {}, PrintsImpl(), Print())
}