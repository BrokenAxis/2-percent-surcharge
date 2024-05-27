package surcharge.ui.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.data.prints.TempData
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.PrintItem
import surcharge.types.Size
import surcharge.types.createPrintItem
import surcharge.utils.components.gallery.Gallery
import surcharge.utils.components.gallery.Tab
import surcharge.utils.formatPrice
import surcharge.utils.intPrice
import surcharge.utils.validatePrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBundle(
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    data: Data,
    bundle: Bundle
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
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Collections, "Bundle",
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Text(
                text = "Add a Bundle",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, "Close")
            }
        }
        HorizontalDivider()

        var total by remember { mutableIntStateOf(0) }
        var name by remember { mutableStateOf("") }
        var nameError by remember { mutableStateOf(false) }
        var price by remember { mutableStateOf("0.00") }
        var priceError by remember { mutableStateOf(false) }

        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = name.isEmpty()
                },
                label = { Text("Name") },
                placeholder = { Text("Bundle") },
                leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .weight(0.5f),
                isError = nameError
            )
            OutlinedTextField(
                value = price,
                onValueChange = {
                    price = it
                    priceError = validatePrice(price)
                },
                label = { Text("Price") },
                placeholder = { Text("69") },
                leadingIcon = { Icon(Icons.Filled.Payments, contentDescription = null) },
                prefix = { Text("$") },
                supportingText = { Text("Normal Price: ${formatPrice(total)}") },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .weight(0.5f),
                isError = priceError
            )
        }
        var prints by remember { mutableStateOf(listOf<Print>()) }

        LaunchedEffect(true) {
            withContext(Dispatchers.IO) {
                prints = data.getPrints().getOrDefault(listOf())
            }
        }

        var selectedSize by remember { mutableStateOf(Size.A3) }
        val selectedPrints = remember { mutableStateListOf<PrintItem>() }
        var shownPrints = prints.filter { it.sizes.contains(selectedSize) }

        var refreshQuantity by remember { mutableIntStateOf(0) }

        Card(
            Modifier.height(320.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Gallery(Tab.Print, shownPrints, printOnClick = {
                val printIndex =
                    selectedPrints.indexOfFirst { print -> print.name == it.name && print.size == selectedSize }
                if (printIndex == -1) {
                    selectedPrints.add(createPrintItem(it, selectedSize, 1))
                } else {
                    selectedPrints[printIndex].quantity++
                    refreshQuantity++
                }
                total = selectedPrints.sumOf { item -> item.price * item.quantity }
            })
        }

        SingleChoiceSegmentedButtonRow(Modifier.padding(10.dp)) {
            Size.entries.forEachIndexed { index, size ->
                SegmentedButton(
                    selected = index == selectedSize.ordinal,
                    onClick = {
                        selectedSize = Size.entries.toTypedArray()[index]
                        shownPrints = prints.filter { it.sizes.contains(selectedSize) }
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index, count = Size.entries.size
                    )
                ) {
                    Text(size.toString())
                }
            }
        }

        Box {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Selected Prints",
                    style = MaterialTheme.typography.titleLarge
                )

                key(refreshQuantity) {
                    selectedPrints.forEach {
                        val itemTotal = it.price * it.quantity

                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { selectedPrints.remove(it) }) {
                                Icon(
                                    Icons.Filled.Close,
                                    "delete",
                                    Modifier,
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${it.quantity}x ${it.name} - ${it.size}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(10.dp),
                            )

                            Spacer(Modifier.weight(1f))
                            Text(
                                "$ ${formatPrice(itemTotal)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider()
                    }
                    Spacer(Modifier.height(50.dp))
                }
            }

            FloatingActionButton(
                onClick = {
                    nameError = name.isEmpty()
                    priceError = validatePrice(price)
                    if (!nameError && !priceError) {
                        bundle.name = name
                        bundle.price = intPrice(price)
                        bundle.prints = selectedPrints.toList()
                        onConfirm()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .absoluteOffset(x = (-10).dp, y = (-10).dp)
            ) {
                Icon(
                    Icons.Outlined.Check,
                    "Confirm Button"
                )
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
    AddBundle(onClose = {}, onConfirm = {}, data = TempData(), bundle = Bundle())
}