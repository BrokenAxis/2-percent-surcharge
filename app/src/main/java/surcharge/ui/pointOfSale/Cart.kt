package surcharge.ui.pointOfSale

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.squareup.sdk.pos.PosClient
import com.squareup.sdk.pos.PosSdk
import io.github.cdimascio.dotenv.dotenv
import surcharge.types.Item
import surcharge.types.PaymentType
import surcharge.types.Sale
import surcharge.utils.formatPrice
import surcharge.utils.intPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cart(
    onClose: () -> Unit,
    onCheckout: () -> Unit,
    onCheckoutError: (error: String) -> Unit,
    sale: Sale
) {
    var cashCheckout by remember { mutableStateOf(false) }
    var onResult by remember { mutableStateOf(false) }
    var result: ActivityResult? = null
    var total by remember { mutableIntStateOf(sale.prints.sumOf { it.price * it.quantity } + sale.bundles.sumOf { it.price * it.quantity }) }
    if (cashCheckout) {
        key(total) {
            CashCheckout(onConfirm = {
                cashCheckout = false
                sale.price = total
                onCheckout()
            }, onDismiss = { cashCheckout = false }, total = total
            )
        }
    }

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ), modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        var editItem by remember { mutableStateOf(false) }
        var selectedItem =
            if (sale.prints.isNotEmpty()) sale.prints.first() else sale.bundles.first()

        if (editItem) {
            EditDialog(
                item = selectedItem,
                onDismissRequest = { editItem = false }
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp)) {
            Icon(Icons.Filled.ShoppingCart, "Cart")
            Text(
                text = "Cart",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { onClose() }) {
                Icon(Icons.Filled.Close, "Close")
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        key(total) {
            sale.bundles.forEach { item ->
                val itemTotal = item.price * item.quantity

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clickable {
                            selectedItem = item
                            editItem = true
                        }
                ) {
                    IconButton(onClick = {
                        sale.bundles.remove(item)
                        total = sale.bundles.sumOf { it.price * it.quantity }
                    }) {
                        Icon(
                            Icons.Filled.Close,
                            "Delete",
                            Modifier,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${item.quantity}x ${item.name}",
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
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

            }

            sale.prints.forEach { item ->
                val itemTotal = item.price * item.quantity

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clickable {
                            selectedItem = item
                            editItem = true
                        }
                ) {
                    IconButton(onClick = {
                        sale.prints.remove(item)
                        total = sale.prints.sumOf { it.price * it.quantity }
                    }) {
                        Icon(
                            Icons.Filled.Close,
                            "Delete",
                            Modifier,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${item.quantity}x ${item.name} - ${item.size}",
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
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        var selectedIndex by remember { mutableStateOf(PaymentType.CASH) }
        val options = listOf("Cash", "Card")
        SingleChoiceSegmentedButtonRow(Modifier.padding(10.dp)) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = {
                        selectedIndex = when (index) {
                            0 -> PaymentType.CASH
                            else -> PaymentType.CARD
                        }
                    },
                    selected = index == selectedIndex.ordinal
                ) {
                    Text(label)
                }
            }
        }

        ElevatedCard(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOTAL:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "$ ${formatPrice(total)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                val dotenv = dotenv {
                    directory = "/assets"
                    filename = "env" // instead of '.env', use 'env'
                }

                val applicationId = dotenv["APPLICATION_ID"]
                val posClient = PosSdk.createClient(LocalContext.current, applicationId)
                val launcher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = {
                            result = it
                            onResult = true
                        })

                FloatingActionButton(onClick = {
                    if (total != 0) {
                        when (selectedIndex) {
                            PaymentType.CASH -> cashCheckout = true
                            PaymentType.CARD -> handleCardCheckout(total, posClient, launcher)
                        }
                    }

                }, modifier = Modifier.padding(30.dp)) {
                    Icon(Icons.Filled.ShoppingCartCheckout, "Checkout")
                }

                if (onResult) result?.let {
                    OnCardCheckoutResult(sale, it, posClient, onCheckout, onCheckoutError)
                }
            }
        }
    }
}

@Composable
fun EditDialog(
    item: Item,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard {
            Column(modifier = Modifier.padding(20.dp)) {

                val original = item.price

                OutlinedTextField(
                    value = formatPrice(original),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Original Price") }
                )

                Spacer(Modifier.height(10.dp))

                var discounted by remember { mutableStateOf(formatPrice(item.price)) }
                var percentage by remember { mutableIntStateOf(0) }
                TextField(
                    value = discounted,
                    onValueChange = {
                        discounted = it
                        percentage =
                            (((original - intPrice(discounted)).toDouble() / original.toDouble()) * 100).toInt()
                    },
                    label = { Text("Discounted Price") },
                    supportingText = { Text("$percentage% off") }
                )

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick = {
                        item.price = intPrice(discounted)
                        onDismissRequest()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Confirm")
                }

            }
        }

    }
}

@Composable
fun OnCardCheckoutResult(
    sale: Sale,
    result: ActivityResult,
    posClient: PosClient,
    onCheckout: () -> Unit,
    onCheckoutError: (error: String) -> Unit
) {

    if (result.resultCode == Activity.RESULT_OK) {
        val success = posClient.parseChargeSuccess(result.data!!)
        sale.comment = success.requestMetadata.toString()
        onCheckout()
    } else {
        val error = posClient.parseChargeError(result.data!!)
        onCheckoutError(error.debugDescription)
    }
}

@Preview
@Composable
private fun Prev() {
    Cart({}, {}, {}, Sale())

}