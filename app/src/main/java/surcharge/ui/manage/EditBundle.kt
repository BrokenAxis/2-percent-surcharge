package surcharge.ui.manage

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import surcharge.types.Bundle
import surcharge.utils.formatPrice
import surcharge.utils.intPrice
import surcharge.utils.validatePrice

@Composable
fun EditBundle(
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    bundle: Bundle
) {
    Card(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp)) {
            Icon(Icons.Filled.Collections, "Bundle")
            Text(
                text = "Edit Bundle Price",
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

        var price by remember { mutableStateOf(formatPrice(bundle.price)) }
        var isError by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = price,
            onValueChange = {
                price = it
                isError = !validatePrice(price)
            },
            label = { Text("Price") },
            placeholder = { Text("69") },
            prefix = { Text("$") },
            leadingIcon = { Icon(Icons.Filled.Payments, contentDescription = "Price") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .fillMaxWidth(),
            isError = isError
        )

        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = {
                bundle.price = intPrice(price)
                onConfirm()
            },
            enabled = !isError,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Confirm")
        }
    }
}