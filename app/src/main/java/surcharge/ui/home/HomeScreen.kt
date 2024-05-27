package surcharge.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import surcharge.data.AppContainer
import surcharge.utils.debounce.debounced

@Composable
fun HomeScreen(
    app: AppContainer,
    onNavigateToAccount: () -> Unit = {},
    onNavigateToManage: () -> Unit = {},
    onNavigateToShop: () -> Unit = {},
    onNavigateToSales: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 10.dp, top = 10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Surcharge",
                    style = MaterialTheme.typography.displayLarge,
                )
                IconButton(
                    onClick = debounced { onNavigateToSettings() },
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Icon(Icons.Filled.Settings, "Settings")
                }
            }

            Spacer(Modifier.weight(0.5f))

            Column(
                modifier = Modifier.padding(horizontal = 15.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Card(
                    onClick = debounced { onNavigateToAccount() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var user by remember { mutableStateOf("User") }
                        LaunchedEffect(true) {
                            user = app.settings.readArtist()
                        }

                        Text(
                            "Welcome, $user", style = MaterialTheme.typography.titleLarge
                        )
                        Icon(Icons.Filled.AccountCircle, "Account")
                    }
                }
                Page(
                    onNavigateToPage = onNavigateToManage,
                    title = "Manage Shop",
                    icon = Icons.Filled.Inventory,
                    description = "Add new prints or edit existing ones. Update stock, sizes and price. Create discounts and bundles."
                )

                Page(
                    onNavigateToPage = onNavigateToShop,
                    title = "Point of Sale",
                    icon = Icons.Filled.PointOfSale,
                    description = "Select prints and bundles for a sale. Manually add discounts or comments. Processes card payments with Square."
                )

                Page(
                    onNavigateToPage = onNavigateToSales,
                    title = "Review Sales",
                    icon = Icons.Filled.Savings,
                    description = "See sales history, income breakdown by artist and analytics"
                )
            }
        }
    }

}

@Composable
fun Page(
    onNavigateToPage: () -> Unit,
    title: String,
    icon: ImageVector,
    description: String
) {
    Card(
        onClick = debounced { onNavigateToPage() },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Icon(
                imageVector = icon,
                contentDescription = ""
            )
        }
        Text(
            text = description,
            modifier = Modifier.padding(20.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

//@Preview
//@Composable
//private fun Prev() {
//    HomeScreen()
//}
