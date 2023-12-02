package surcharge.ui.pointOfSale

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
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
import com.squareup.sdk.pos.PosClient
import com.squareup.sdk.pos.PosSdk
import io.github.cdimascio.dotenv.dotenv
import surcharge.types.PaymentType
import surcharge.types.PrintItem
import surcharge.types.Sale
import surcharge.utils.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cart(
    onClose: () -> Unit,
    onCheckout: () -> Unit,
    sale: Sale
) {
    var cashCheckout by remember { mutableStateOf(false) }
    var onResult by remember { mutableStateOf(false) }
    var result: ActivityResult? = null

    if (cashCheckout) {
        CashCheckout(
            onConfirm = {
                cashCheckout = false
                onCheckout()
            },
            onDismiss = { cashCheckout = false },
            total = sale.price
        )
    }

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp)) {
            Icon(Icons.Filled.AttachMoney, "Cart")
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

        var total by remember { mutableIntStateOf(sale.items.sumOf { it.price * it.quantity }) }

        key(total) {
            sale.items.forEach { item ->
                val itemTotal = item.price * item.quantity

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    IconButton(onClick = {
                        sale.items.remove(item)
                        total = sale.items.sumOf { it.price * it.quantity }
                    }) {
                        Icon(
                            Icons.Filled.Close,
                            "Delete",
                            Modifier,
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${item.quantity}x ${item.name}${
                            when (item is PrintItem) {
                                true -> " - ${item.size}"
                                else -> ""
                            }
                        }",
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
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        result = it
                        onResult = true
                    }
                )

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

                if (onResult) result?.let { OnCardCheckoutResult(it, posClient) }
            }
        }
    }
}

@Composable
fun OnCardCheckoutResult(result: ActivityResult, posClient: PosClient) {
    val error = posClient.parseChargeError(result.data!!)
    error.code
    error.debugDescription
}

@Preview
@Composable
private fun Prev() {
    Cart({}, {}, Sale())

}