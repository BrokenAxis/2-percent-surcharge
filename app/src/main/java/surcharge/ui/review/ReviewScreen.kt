package surcharge.ui.review

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import surcharge.data.prints.Data
import surcharge.data.prints.DataImpl
import surcharge.types.PrintItem
import surcharge.types.Sale
import surcharge.utils.formatPrice
import surcharge.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    data: Data,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { TODO() }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val horizontalScroll = rememberScrollState()
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                Text("Items", Modifier.width(150.dp), style = MaterialTheme.typography.titleSmall)
                Text("Total", Modifier.width(70.dp), style = MaterialTheme.typography.titleSmall)
                Text(
                    "Payment Type",
                    Modifier.width(70.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Timestamp",
                    Modifier.width(100.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                Text("Comment", Modifier.width(200.dp), style = MaterialTheme.typography.titleSmall)
            }

            Spacer(Modifier.height(5.dp))
            HorizontalDivider()
            Spacer(Modifier.height(5.dp))

            var sales by remember { mutableStateOf(listOf<Sale>()) }
            LaunchedEffect(true) {
                sales = data.getSales().getOrDefault(listOf())
            }

            sales.forEach { sale ->
                Spacer(Modifier.height(5.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScroll),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(Modifier.width(150.dp)) {
                        sale.items.forEach {item ->
                            Text(text = "${item.quantity}x ${item.name}${
                                when (item is PrintItem) {
                                    true -> " - ${item.size}"
                                    else -> ""
                                }
                            }",)
                            Spacer(Modifier.height(5.dp))
                        }
                    }

                    Text("$  ${formatPrice(sale.price)}", Modifier.width(70.dp), style = MaterialTheme.typography.titleSmall)
                    Text(
                        sale.paymentType.toString(),
                        Modifier.width(70.dp),
                    )
                    Text(
                        formatTime(sale.time),
                        Modifier.width(100.dp),
                    )
                    Text(sale.comment, Modifier.width(200.dp))
                }
                Spacer(Modifier.height(5.dp))
                HorizontalDivider()
            }
        }

    }
}

@Preview
@Composable
private fun Prev() {
    ReviewScreen(data = DataImpl()) {}
}
