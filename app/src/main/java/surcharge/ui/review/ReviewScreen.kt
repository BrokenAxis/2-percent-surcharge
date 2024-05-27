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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.data.prints.TempData
import surcharge.types.Artist
import surcharge.types.Sale
import surcharge.utils.artistTotal
import surcharge.utils.components.Tile
import surcharge.utils.formatPrice
import surcharge.utils.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    data: Data,
    onNavigateToAnalytics: () -> Unit = {},
    onBack: () -> Unit
) {
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
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            var artists by remember { mutableStateOf(listOf<Artist>()) }
            var sales by remember { mutableStateOf(listOf<Sale>()) }
            LaunchedEffect(true) {
                withContext(Dispatchers.IO) {
                    artists = data.getArtists().getOrDefault(listOf())
                    sales = data.getSales().getOrDefault(listOf())
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

                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(artist.image)
                                    .crossfade(true)
                                    .build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
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
                icon = Icons.Filled.QueryStats
            ) {}

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
                sales.forEach { TransactionCard(sale = it, onClick = {}) }
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Column {
                sale.bundles.forEach { item ->
                    Text(text = "${item.quantity}x ${item.name}")
                    Spacer(Modifier.height(5.dp))
                }
                sale.prints.forEach { item ->
                    Text(text = "${item.quantity}x ${item.name} - ${item.size}")
                    Spacer(Modifier.height(5.dp))
                }
                Text(
                    text = "${sale.paymentType} on ${formatTime(sale.time)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Text(
                text = "$${formatPrice(sale.price)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

    }
}

@Preview
@Composable
private fun Prev() {
    ReviewScreen(data = TempData()) {}
}
