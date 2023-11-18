package surcharge.ui.pointOfSale

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import surcharge.data.prints.Prints
import surcharge.data.prints.PrintsImpl
import surcharge.ui.PrintLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesMenu(
    prints: Prints = PrintsImpl(),
    onBack: () -> Unit = {}
) {
    var openCartDialog by remember { mutableStateOf(false) }

    if (openCartDialog) {
        Dialog(
            onDismissRequest = { openCartDialog = false },
            DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Cart { openCartDialog = false }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Sales Menu") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { TODO() }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(Icons.Filled.FilterAlt, contentDescription = "Filter")
                    }
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Artist",
                        )
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { openCartDialog = true }) {
                        Icon(Icons.Filled.ShoppingCart, "Cart")
                    }
                }
            )
        },
    ) { innerPadding ->
        PrintLayout(data = prints, innerPadding = innerPadding)

    }

}

@Preview
@Composable
private fun Prev() {
    SalesMenu()
}