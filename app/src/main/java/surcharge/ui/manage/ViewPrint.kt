package surcharge.ui.manage

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.types.Print
import surcharge.utils.formatPrice

@Composable
fun ViewPrint(
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
    data: Data,
    print: Print
) {
    var editPrint by remember { mutableStateOf(false) }
    if (editPrint) {
        Dialog(
            onDismissRequest = { editPrint = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            EditPrint(
                { editPrint = false },
                onEdit,
                print
            )
        }
    }
    val scope = rememberCoroutineScope()
    var archivePrint by remember { mutableStateOf(false) }
    if (archivePrint) {
        AlertDialog(
            onDismissRequest = { archivePrint = false },
            title = { Text("Archive Item") },
            text = {
                Text(
                    text = "Are you sure you want to archive this item? Currently this will delete the item. Sales history will not correctly display discounts of deleted items",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        archivePrint = false
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                data.deletePrint(print)
                            }
                        }
                        onArchive()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        archivePrint = false
                    }
                ) {
                    Text("Dismiss")
                }
            },
            icon = { Icon(Icons.Outlined.Archive, "") }
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
        Box {
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(500.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(print.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = print.name,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = print.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 20.dp, top = 10.dp)
            )

            IconButton(onClick = { archivePrint = true }) {
                Icon(Icons.Filled.Archive, "archive")
            }
        }

        Text(
            text = "by ${print.artist}",
            fontStyle = FontStyle.Italic,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
        )

        HorizontalDivider()

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Stock",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 30.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                print.sizes.forEach { size ->

                    Row(Modifier.padding(10.dp)) {
                        Text(
                            text = "$size x ${print.stock[size]}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "$ ${formatPrice(print.price[size] ?: 0)}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                }
                Spacer(Modifier.height(50.dp))
            }
            FloatingActionButton(
                onClick = { editPrint = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .absoluteOffset(x = (-15).dp, y = (-15).dp)
            ) {
                Icon(Icons.Filled.Edit, "Edit")
            }

        }

    }
}