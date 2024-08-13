package surcharge.ui.pointOfSale

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import com.squareup.sdk.mobilepayments.payment.Payment
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.data.prints.Firestore
import surcharge.types.Bundle
import surcharge.types.Item
import surcharge.types.PaymentType
import surcharge.types.Print
import surcharge.types.PrintItem
import surcharge.types.Sale
import surcharge.utils.formatPrice
import surcharge.utils.intPrice
import surcharge.utils.validatePrice
import kotlin.math.ceil

@Composable
fun Cart(
    onClose: () -> Unit,
    onCheckout: (Payment?) -> Unit,
    onCheckoutError: (error: String) -> Unit,
    sale: Sale,
    app: AppContainer
) {
    val scope = rememberCoroutineScope()
    var cashCheckout by remember { mutableStateOf(false) }
    var total by remember { mutableIntStateOf(sale.prints.sumOf { it.price * it.quantity } + sale.bundles.sumOf { it.price * it.quantity }) }
    if (cashCheckout) {
        key(total) {
            CashCheckout(
                onConfirm = {
                    cashCheckout = false
                    sale.price = total
                    scope.launch(IO) {
                        (app.data as Firestore).addCashOnHand(total)
                    }
                    onCheckout(null)
                },
                onDismiss = { cashCheckout = false }, total = total
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
        var selectedItem: Item by remember {
            mutableStateOf(PrintItem())
        }

        if (editItem) {
            val originalPrice by remember { mutableIntStateOf(selectedItem.price) }
            EditDialog(
                app = app,
                item = selectedItem,
                onDismissRequest = {
                    total += (selectedItem.price - originalPrice) * selectedItem.quantity
                    editItem = false
                }
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
                    Column {
                        Row {
                            Text(
                                text = "${item.quantity}x ${item.name}",
                                modifier = Modifier.padding(bottom = 10.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.weight(1f))
                            Text(
                                "$ ${formatPrice(itemTotal)}",
                                modifier = Modifier.padding(start = 10.dp, bottom = 10.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item.prints.forEach { print ->
                            Text(
                                text = "${print.quantity}x ${print.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 10.dp, bottom = 5.dp),
                            )
                        }
                    }
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

        var selectedIndex by remember { mutableStateOf(PaymentType.CARD) }
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
                    text = "Total:",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                when (selectedIndex) {
                    PaymentType.CASH -> {
                        Text(
                            "$ ${formatPrice(total)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    PaymentType.CARD -> {
                        Text(
                            "$ ${formatPrice(ceil(total.toDouble() * 1.02).toInt())}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                FloatingActionButton(onClick = {
                    if (total != 0) {
                        sale.price = total
                        sale.paymentType = selectedIndex
                        when (selectedIndex) {
                            PaymentType.CASH -> cashCheckout = true
                            PaymentType.CARD -> handleCardCheckout(
                                total = (total.toDouble() * 1.02).toInt(),
                                saleId = sale.saleId,
                                onSuccess = onCheckout,
                                onError = onCheckoutError
                            )
                        }
                    }
                }, modifier = Modifier.padding(30.dp)) {
                    Icon(Icons.Filled.ShoppingCartCheckout, "Checkout")
                }
            }
        }
    }
}

@Composable
fun EditDialog(
    app: AppContainer,
    item: Item,
    onDismissRequest: () -> Unit,
) {
    var original by remember { mutableIntStateOf(item.price) }
    var percentage by remember { mutableIntStateOf(0) }
    var discounted by remember { mutableStateOf(formatPrice(item.price)) }
    LaunchedEffect(item) {
        withContext(IO) {
            app.data.getPrint(item.name).getOrElse { Print() }
            original = if (item is PrintItem) {
                app.data.getPrint(item.name)
                    .getOrElse { Print(price = mutableMapOf(item.size.toString() to 0)) }.price[item.size.toString()]
                    ?: 0
            } else {
                app.data.getBundle(item.name).getOrElse { Bundle() }.price
            }
            percentage =
                ceil((((original - intPrice(discounted)).toDouble() / original.toDouble()) * 100)).toInt()
        }
    }

    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard {
            Column(modifier = Modifier.padding(20.dp)) {

                OutlinedTextField(
                    value = formatPrice(original),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Original Price") },
                    prefix = { Text("$ ") }
                )

                HorizontalDivider(Modifier.padding(vertical = 10.dp))

                var isError by remember { mutableStateOf(false) }
                TextField(
                    value = discounted,
                    onValueChange = {
                        discounted = it
                        isError = !validatePrice(discounted)
                        if (!isError) {
                            percentage =
                                ceil((((original - intPrice(discounted)).toDouble() / original.toDouble()) * 100)).toInt()
                        }
                    },
                    label = { Text("Discounted Price") },
                    prefix = { Text("$ ") },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(Modifier.height(10.dp))

                TextField(
                    value = percentage.toString(),
                    onValueChange = {
                        if (it.isNotEmpty() && it.isDigitsOnly()) percentage = it.toInt()
                        isError = !(it.isNotEmpty() && it.isDigitsOnly() && it.toInt() <= 100)
                        if (!isError) {
                            discounted =
                                formatPrice((((100 - percentage.toDouble()) / 100) * original.toDouble()).toInt())
                        }
                    },
                    label = { Text("Percentage Discount") },
                    suffix = { Text("% off") },
                    supportingText = { Text("Apply a flat or percentage discount") },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
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

//@Preview
//@Composable
//private fun Prev() {
//    Cart({}, {}, {}, Sale())
//
//}