package surcharge.ui.manage

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import surcharge.data.prints.Data
import surcharge.types.Bundle
import surcharge.utils.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBundle(
    onClose: () -> Unit,
    onEdit: () -> Unit,
    data: Data,
    bundle: Bundle
) {
    var editBundle by remember { mutableStateOf(false) }
    if (editBundle) {
        BasicAlertDialog(onDismissRequest = { editBundle = false }) {
            EditBundle(
                { editBundle = false },
                onEdit,
                bundle
            )
        }
    }

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Box {
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(bundle.prints.first().url)
                        .crossfade(true)
                        .build(),
                    contentDescription = bundle.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            FilledIconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        2.dp
                    )
                )
            ) {
                Icon(Icons.Filled.Close, "Close")
            }
        }

        Text(
            text = bundle.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )

        HorizontalDivider()

        Spacer(Modifier.height(20.dp))

        Row {
            Text(
                text = "Price:",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 25.dp)
            )
            Spacer(Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(horizontal = 25.dp)
            ) {
                Text(
                    text = "$${formatPrice(bundle.price)}",
                    style = MaterialTheme.typography.titleLarge
                )
                val total = bundle.prints.sumOf { it.price * it.quantity }
                Row {
                    Text(
                        text = "(${100 - ((bundle.price.toDouble() / total.toDouble()) * 100).toInt()}% off) ",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "$${formatPrice(total)}",
                        style = MaterialTheme.typography.titleSmall,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

        }

        Spacer(Modifier.height(15.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                bundle.prints.forEach { print ->
                    Row(Modifier.padding(horizontal = 25.dp)) {
                        Text(
                            text = "${print.quantity}x ${print.name} - ${print.size}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "$ ${formatPrice(print.price)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(Modifier.height(50.dp))
            }
            FloatingActionButton(
                onClick = { editBundle = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .absoluteOffset(x = (-15).dp, y = (-15).dp)
            ) {
                Icon(Icons.Filled.Edit, "Edit")
            }
        }

    }


}