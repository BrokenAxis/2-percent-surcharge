package surcharge.ui.pointOfSale

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import surcharge.utils.formatPrice
import surcharge.utils.intPrice
import surcharge.utils.validatePrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashCheckout(onConfirm: () -> Unit, onDismiss: () -> Unit, total: Int) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            var tender by remember { mutableStateOf(formatPrice(total)) }
            var isError by remember { mutableStateOf(false) }
            var change by remember { mutableStateOf("0.00") }
            Column(
                Modifier.padding(20.dp),
                Arrangement.spacedBy(20.dp),
                Alignment.CenterHorizontally
            ) {
                Text("Cash Checkout", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = formatPrice(total),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Total Owed") },
                    placeholder = { Text("69") },
                    prefix = { Text("$") },
                    leadingIcon = { Icon(Icons.Filled.PointOfSale, contentDescription = "Tender") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                )
                HorizontalDivider()
                TextField(
                    value = tender,
                    onValueChange = {
                        tender = it
                        isError = !(validatePrice(tender) && intPrice(tender) >= total)
                        if (!isError) change =
                            formatPrice((tender.toDouble() * 100).toInt() - total)
                    },
                    label = { Text("Tender") },
                    placeholder = { Text("69") },
                    prefix = { Text("$") },
                    leadingIcon = { Icon(Icons.Filled.Payments, contentDescription = "Tender") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    isError = isError,
                    supportingText = {
                        Text(
                            when (isError) {
                                true -> "Invalid Input"
                                false -> "Input amount tendered to calculate required change"
                            }
                        )
                    }
                )

                TextField(
                    value = change,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Change") },
                    prefix = { Text("$") },
                    leadingIcon = { Icon(Icons.Filled.PriceChange, contentDescription = "Change") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    isError = isError
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = onConfirm,
                        enabled = !isError
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
    CashCheckout({}, {}, 10000)
}