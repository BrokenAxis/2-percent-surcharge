package surcharge.utils.components.gallery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.data.prints.TempData
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.ui.manage.AddBundle
import surcharge.utils.debounce.debounced

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
    val pager = rememberPagerState(tab.ordinal) { Tab.entries.size }
    val titles = listOf("Prints", "Bundles")
    val icons = listOf(Icons.Filled.Palette, Icons.Filled.Collections)

    val scope = rememberCoroutineScope()

    if (pager.currentPage != tab.ordinal) tab = Tab.entries[pager.currentPage]

    Column(modifier = Modifier.padding(innerPadding)) {
        PrimaryTabRow(
            selectedTabIndex = tab.ordinal,
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = tab.ordinal == index,
                    onClick = {
                        tab = Tab.entries.toTypedArray()[index]
                        onSwitchTab(tab)
                        scope.launch { pager.animateScrollToPage(tab.ordinal) }
                    },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
                    icon = { Icon(icons[index], title) }
                )
            }
        }

        var prints by remember { mutableStateOf(listOf<Print>()) }
        var bundles by remember { mutableStateOf(listOf<Bundle>()) }

        LaunchedEffect(true) {
            withContext(IO) {
                prints = data.getPrints().getOrDefault(listOf())
                bundles = data.getBundles().getOrDefault(listOf())
            }
        }

        HorizontalPager(state = pager) { page ->
            when (page) {
                Tab.Print.ordinal -> {
                    Gallery(
                        prints = prints,
                        printOnClick = printOnClick
                    )
                }

                Tab.Bundle.ordinal -> {
                    Gallery(
                        bundles = bundles,
                        bundleOnClick = bundleOnClick
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
fun PrintImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .size(Size.ORIGINAL)
            .build(),
        contentScale = ContentScale.Fit
    )
    var visible by remember { mutableStateOf(true) }

    key(visible) {
        val state = painter.state
        when (state) {
            is AsyncImagePainter.State.Empty -> {
                visible = false
            }

            is AsyncImagePainter.State.Error -> {
                visible = false
            }

            is AsyncImagePainter.State.Loading -> {
                visible = false
            }

            is AsyncImagePainter.State.Success -> {
                visible = true
            }
        }
    }

    val density = LocalDensity.current
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { -40.dp.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .crossfade(true)
                .build(),
            contentScale = contentScale,
            contentDescription = null,
            modifier = modifier
        )
    }

    if (!visible) {
        CircularProgressIndicator()
    }
}

@Composable
fun Gallery(
    prints: List<Print>? = null,
    printOnClick: (print: Print) -> Unit = {},
    bundles: List<Bundle>? = null,
    bundleOnClick: (bundle: Bundle) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(130.dp),
        modifier = Modifier.fillMaxSize(),
        verticalItemSpacing = 10.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (prints != null) {
            items(prints.size) { index ->
                Card(
                    onClick = debounced { printOnClick(prints[index]) },
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                    ),
                ) {
                    Card(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = CardDefaults.cardColors(Color.Transparent)
                    ) {
                        PrintImage(prints[index].url)
                    }

                    Text(
                        prints[index].name,
                        Modifier.padding(10.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else if (bundles != null) {
            items(bundles.size) { index ->
                Card(
                    onClick = debounced { bundleOnClick(bundles[index]) },
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                    )
                ) {
                    Card(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = CardDefaults.cardColors(Color.Transparent)
                    ) {
                        PrintImage(bundles[index].prints[0].url)
                    }
                    Text(
                        bundles[index].name,
                        Modifier.padding(10.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
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