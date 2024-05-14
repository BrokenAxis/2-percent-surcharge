package surcharge.utils.components.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.data.prints.TempData
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.ui.manage.AddBundle

enum class Tab {
    Print,
    Bundle
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabGallery(
    data: Data = TempData(),
    printOnClick: (print: Print) -> Unit = {},
    printOnLongPress: (print: Print) -> Unit = {},
    bundleOnClick: (bundle: Bundle) -> Unit = {},
    bundleOnLongPress: (bundle: Bundle) -> Unit = {},
    onSwitchTab: (tab: Tab) -> Unit = {},
    innerPadding: PaddingValues
) {
    var tab by remember { mutableStateOf(Tab.Print) }

    val titles = listOf("Prints", "Bundles")
    val icons = listOf(Icons.Filled.Palette, Icons.Filled.Collections)

    Column(modifier = Modifier.padding(innerPadding)) {
        PrimaryTabRow(
            selectedTabIndex = tab.ordinal,
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = tab.ordinal == index,
                    onClick = {
                        tab = Tab.values()[index]
                        onSwitchTab(tab)
                    },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                    icon = { Icon(icons[index], title) }
                )
            }
        }

        var prints by remember { mutableStateOf(listOf<Print>()) }
        var bundles by remember { mutableStateOf(listOf<Bundle>()) }

        LaunchedEffect(true) {
            withContext(Dispatchers.IO) {
                prints = data.getPrints().getOrDefault(listOf())
                bundles = data.getBundles().getOrDefault(listOf())
            }
        }

        Gallery(tab, prints, bundles, printOnClick, bundleOnClick)
    }
}

@Composable
fun PrintImage(url: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Fit,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
    )
}

@Composable
fun Gallery(
    tab: Tab,
    prints: List<Print> = listOf(),
    bundles: List<Bundle> = listOf(),
    printOnClick: (print: Print) -> Unit = {},
    bundleOnClick: (bundle: Bundle) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(180.dp),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        content = {
            when (tab) {
                Tab.Print -> items(prints.size) { index ->
                    Card(
                        onClick = { printOnClick(prints[index]) },
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                        )
                    ) {
                        Card { PrintImage(prints[index].url) }

                        Text(
                            prints[index].name,
                            Modifier.padding(10.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Tab.Bundle -> items(bundles.size) { index ->
                    Card(
                        onClick = { bundleOnClick(bundles[index]) },
                        colors = CardDefaults.cardColors(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(
                                2.dp
                            )
                        )
                    ) {
                        Card { PrintImage(bundles[index].prints[0].url) }
                        Text(
                            bundles[index].name,
                            Modifier.padding(10.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}

@Preview
@Composable
private fun Prev() {
    AddBundle(
        onClose = {},
        onConfirm = {},
        data = TempData(),
        bundle = Bundle()
    )
}