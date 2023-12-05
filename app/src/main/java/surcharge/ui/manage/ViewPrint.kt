package surcharge.ui.manage

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import surcharge.data.prints.Data
import surcharge.types.Print
import surcharge.utils.formatPrice

@Composable
fun ViewPrint(
    onClose: () -> Unit,
    onEdit: () -> Unit,
    data: Data,
    print: Print
) {

    var editPrint by remember { mutableStateOf(false) }
    if (editPrint) {
        Dialog(
            onDismissRequest = { editPrint = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            EditPrint(
                { editPrint = false },
                onEdit,
                print
            )
        }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            Modifier
                .fillMaxWidth()
                .height(500.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(print.url)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
            Spacer(Modifier.width(30.dp))
            Text(
                text = print.name,
                style = MaterialTheme.typography.headlineMedium,
            )
            IconButton(onClick = { editPrint = true }) {
                Icon(Icons.Filled.Edit, "Edit")
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, "Close")
            }
        }
        Text(
            text = "by ${print.artist.name}",
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 25.dp)
        )
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Stock",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 30.dp)
        )

        print.sizes.forEach { size ->

            Row(Modifier.padding(10.dp)) {
                Text(
                    text = "$size - ${print.stock[size]}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$ ${formatPrice(print.price[size]?: 0)}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }

        }
    }
}