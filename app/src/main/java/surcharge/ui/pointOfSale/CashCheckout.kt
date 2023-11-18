package surcharge.ui.pointOfSale

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CashCheckout(innerPadding: PaddingValues = PaddingValues()) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .padding(innerPadding)
    ) {
        val total = 100.00
        val cashOnHand = 500.00

        var text by rememberSaveable { mutableStateOf(total.toString()) }

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tender") },
            placeholder = { Text("69") },
            prefix = { Text("$") },
            leadingIcon = { Icon(Icons.Filled.Payments, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp),
        )

        TextField(
            value = (text.toDouble() - total).toString(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Change") },
            prefix = { Text("$") },
            leadingIcon = { Icon(Icons.Filled.PriceChange, contentDescription = null) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp),
        )

        // calculate cash to hand back
    }
}

@Preview
@Composable
private fun Prev() {
    CashCheckout()
}