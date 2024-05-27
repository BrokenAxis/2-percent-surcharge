package surcharge.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import surcharge.data.AppContainer

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
        Column {
            Row(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Surcharge",
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(20.dp, 30.dp)
                )
                IconButton(
                    onClick = { onNavigateToSettings() },
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(Icons.Filled.Settings, "Settings")
                }
            }

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    onClick = { onNavigateToAccount() },
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
                Card(
                    onClick = { onNavigateToManage() },
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
                            "Manage Shop", Modifier, style = MaterialTheme.typography.titleLarge
                        )
                        Icon(Icons.Filled.Inventory, "Manage Shop")
                    }
                    Text(
                        "Add new prints or edit existing ones. Update stock, sizes and price. Create discounts and bundles.",
                        Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Card(
                    onClick = { onNavigateToShop() },
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
                            "Point of Sale", Modifier, style = MaterialTheme.typography.titleLarge
                        )
                        Icon(Icons.Filled.PointOfSale, "Point of Sale")
                    }
                    Text(
                        "Select prints and bundles for a sale. Manually add discounts or comments. Processes card payments with Square.",
                        Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Card(
                    onClick = { onNavigateToSales() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            2.dp
                        )
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
                            "Review Sales", Modifier, style = MaterialTheme.typography.titleLarge
                        )
                        Icon(Icons.Filled.Savings, "Review Sales")
                    }
                    Text(
                        "See sales history, income breakdown by artist and analytics",
                        Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

        }


    }
}

//@Preview
//@Composable
//private fun Prev() {
//    HomeScreen()
//}
