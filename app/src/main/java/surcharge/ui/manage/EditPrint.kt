package surcharge.ui.manage

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import surcharge.types.Print
import surcharge.utils.intPrice
import surcharge.utils.validatePrice

@Composable
fun EditPrint(
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    print: Print
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp)) {
            Icon(Icons.Filled.Image, "Print")
            Text(
                text = "Edit Price and Stock by Size",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, "Close")
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        Spacer(Modifier.height(10.dp))


        val isError = remember { List(print.sizes.size) { false }.toMutableStateList() }
        val prices = remember { List(print.sizes.size) { "0.00" }.toMutableStateList() }
        val stock = remember { List(print.sizes.size) { "0" }.toMutableStateList() }

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
            onClick = {
                print.sizes.forEachIndexed { index, size ->
                    print.price[size] = intPrice(prices[index])
                    print.stock[size] = stock[index].toInt()
                }

                onConfirm()
            },
            modifier = Modifier.align(Alignment.End).padding(10.dp),
            enabled = !isError.contains(true)
        ) {
            Text("Confirm")
        }
    }
}