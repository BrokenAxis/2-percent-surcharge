package surcharge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import surcharge.data.prints.Prints
import surcharge.data.prints.PrintsImpl
import surcharge.types.Bundle
import surcharge.types.Print

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintLayout(
    data: Prints = PrintsImpl(),
    innerPadding: PaddingValues
) {
    var state by remember { mutableIntStateOf(0) }

    val titles = listOf("Prints", "Bundles")
    val icons = listOf(Icons.Filled.Palette, Icons.Filled.Collections)

    Column(modifier = Modifier.padding(innerPadding)) {
        PrimaryTabRow(
            selectedTabIndex = state,
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                    icon = { Icon(icons[index], title) }
                )
            }
        }

        val prints = remember { mutableStateOf(listOf<Print>()) }
        val bundles = remember { mutableStateOf(listOf<Bundle>()) }

        LaunchedEffect(true) {
            prints.value = data.getPrints().getOrDefault(listOf())
            bundles.value = data.getBundles().getOrDefault(listOf())
        }
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(200.dp),
            verticalItemSpacing = 6.dp,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            content = {

                items(
                    when (state) {
                        0 -> prints.value.size
                        else -> bundles.value.size
                    }
                ) { index ->

                    if (state == 0) {
                        Card (
                            onClick = {}
                        ){
                            Card{ PrintImage(prints.value[index].url) }

                            Text(
                                prints.value[index].name,
                                Modifier.padding(10.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Card {
                            Card { PrintImage(bundles.value[index].prints[0].url) }
                            Text(
                                bundles.value[index].name,
                                Modifier.padding(10.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .absolutePadding(top = 10.dp)
        )
    }

}

@Composable
fun PrintImage(url: String) {
    AsyncImage(
        model = url,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
    )
}