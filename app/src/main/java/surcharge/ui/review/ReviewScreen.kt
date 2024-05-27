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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.data.prints.TempData
import surcharge.types.Artist
import surcharge.types.Sale
import surcharge.utils.artistTotal
import surcharge.utils.components.Tile
import surcharge.utils.components.gallery.PrintImage
import surcharge.utils.formatPrice
import surcharge.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    data: Data,
    onNavigateToAnalytics: () -> Unit = {},
    onBack: () -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review Sales") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbar) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            var refresh by remember { mutableIntStateOf(0) }
            var artists by remember { mutableStateOf(listOf<Artist>()) }
            var sales by remember { mutableStateOf(listOf<Sale>()) }
            LaunchedEffect(refresh) {
                withContext(IO) {
                    artists = data.getArtists().getOrDefault(listOf())
                    sales = data.getSales().getOrDefault(listOf())
                }
            }
            val scope = rememberCoroutineScope()
            var sale by remember { mutableStateOf(Sale()) }
            var viewSale by remember { mutableStateOf(false) }
            if (viewSale) {
                Dialog(
                    onDismissRequest = { viewSale = false },
                    properties = DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Surface(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .padding(10.dp),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation
                    ) {
                        Column {
                            TransactionCard(sale = sale) {}
                            Text("Total discount: WIP", Modifier.padding(start = 10.dp))
                            TextButton(onClick = {
                                scope.launch {
                                    withContext(IO) {
                                        if (!data.deleteSale(sale)) snackbar.showSnackbar("Error deleting sale!")
                                    }
                                    refresh++
                                    viewSale = false
                                }
                            }) {
                                Text("Delete Sale")
                            }
                        }
                    }
                }
            }

            val horizontalScroll = rememberScrollState()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScroll),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                artists.forEach { artist ->
                    Card(
                        onClick = {},
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(bottom = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = artist.name,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            PrintImage(
                                url = artist.image,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .size(160.dp)
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = "$${formatPrice(artistTotal(sales, artist))}",
                                style = MaterialTheme.typography.displayMedium
                            )
                        }
                    }
                }
            }

            Tile(
                title = "Sales Data",
                subtitle = "stats for nerds",
                icon = Icons.Filled.QueryStats,
                onClick = onNavigateToAnalytics
            )

            Tile(
                title = "Stock",
                subtitle = "review stock, cash on hand",
                icon = Icons.Filled.AllInbox
            ) {}

            Text("Recent Transactions", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                sales.forEach {
                    TransactionCard(
                        sale = it,
                        onClick = {
                            viewSale = true
                            sale = it
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCard(sale: Sale, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            sale.bundles.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${item.quantity}x ${item.name}")
                    Text(text = "$${formatPrice(item.price * item.quantity)}")
                }

                Spacer(Modifier.height(5.dp))
                item.prints.forEach { print ->
                    Text(
                        text = "${print.quantity}x ${print.name} - ${print.size}",
                        Modifier.padding(start = 10.dp)
                    )
                    Spacer(Modifier.height(5.dp))
                }
            }
            sale.prints.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${item.quantity}x ${item.name} - ${item.size}")
                    Text(text = "$${formatPrice(item.price * item.quantity)}")
                }
                Spacer(Modifier.height(5.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "${sale.paymentType} on ${formatTime(sale.time)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "$${formatPrice(sale.price)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
    ReviewScreen(data = TempData()) {}
}
