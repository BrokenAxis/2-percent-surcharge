package surcharge.ui.review

import android.text.TextUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.rememberLegendItem
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.Shape
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.types.Artist
import surcharge.types.Sale
import surcharge.types.Size
import surcharge.utils.components.rememberMarker
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    data: Data,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
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
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            var sales by remember { mutableStateOf(listOf<Sale>()) }
            var artists by remember { mutableStateOf(listOf<Artist>()) }
            val salesModelProducer = remember { CartesianChartModelProducer.build() }
            val bestsellerModelProducer = remember { CartesianChartModelProducer.build() }
            var view by remember { mutableIntStateOf(0) }
            val labelListKey = ExtraStore.Key<List<String>>()
            var sellerFormatter by remember { mutableStateOf(CartesianValueFormatter.decimal()) }
            val xToHourMapKey = ExtraStore.Key<List<Instant>>()
            var perHourFormatter by remember { mutableStateOf(CartesianValueFormatter.decimal()) }

            LaunchedEffect(true) {
                withContext(IO) {
                    sales = data.getSales().getOrDefault(listOf())
                    artists = data.getArtists().getOrDefault(listOf())

                    // bestseller
                    val saleMapA3 = mutableMapOf<String, Int>()
                    val saleMapA5 = mutableMapOf<String, Int>()
                    sales.forEach { sale ->
                        sale.prints.forEach { print ->
                            when (print.size) {
                                Size.A3 -> {
                                    val entry = saleMapA3.getOrDefault(print.name, 0)
                                    saleMapA3[print.name] = entry + print.quantity
                                    saleMapA5.putIfAbsent(print.name, 0)
                                }

                                Size.A5 -> {
                                    saleMapA5[print.name] =
                                        saleMapA5.getOrDefault(print.name, 0) + print.quantity
                                    saleMapA3.putIfAbsent(print.name, 0)
                                }

                                Size.A4 -> {}
                                Size.THICC -> {}
                            }
                        }
                        sale.bundles.forEach { bundle ->
                            bundle.prints.forEach { print ->
                                when (print.size) {
                                    Size.A3 -> {
                                        val entry = saleMapA3.getOrDefault(print.name, 0)
                                        saleMapA3[print.name] = entry + print.quantity
                                        saleMapA5.putIfAbsent(print.name, 0)
                                    }

                                    Size.A5 -> {
                                        val entry = saleMapA5.getOrDefault(print.name, 0)
                                        saleMapA5[print.name] = entry + print.quantity
                                        saleMapA3.putIfAbsent(print.name, 0)
                                    }

                                    Size.A4 -> {}
                                    Size.THICC -> {}
                                }
                            }
                        }
                    }

                    val bestsellersA3 = saleMapA3.toList()
                        .sortedByDescending { it.second + saleMapA5.getOrDefault(it.first, 0) }
                        .toMap()
                    val bestsellersA5 = saleMapA5.toList()
                        .sortedByDescending { it.second + saleMapA3.getOrDefault(it.first, 0) }
                        .toMap()
                    bestsellerModelProducer.tryRunTransaction {
                        columnSeries {
                            series(bestsellersA3.values)
                            series(bestsellersA5.values)
                            updateExtras {
                                it[labelListKey] =
                                    bestsellersA3.keys.toList()
                            }
                        }
                    }
                    sellerFormatter =
                        CartesianValueFormatter { x, chartValues, _ -> chartValues.model.extraStore[labelListKey][x.toInt()] }


                    // sales per hour
                    val salesPerHour = mutableMapOf<String, MutableMap<Instant, Int>>()

                    artists.forEach { artist ->
                        salesPerHour[artist.name] = mutableMapOf()
                    }

                    sales.forEach { sale ->
                        val hour = sale.time.truncatedTo(ChronoUnit.HOURS)
                        sale.prints.forEach { print ->
                            salesPerHour.putIfAbsent(print.artist, mutableMapOf())
                            salesPerHour[print.artist]!![hour] =
                                salesPerHour[print.artist]!!.getOrDefault(hour, 0) + print.quantity
                            salesPerHour.forEach { it.value.putIfAbsent(hour, 0) }
                        }

                        sale.bundles.forEach { bundle ->
                            bundle.prints.forEach { print ->
                                salesPerHour.putIfAbsent(print.artist, mutableMapOf())
                                salesPerHour[print.artist]!![hour] =
                                    salesPerHour[print.artist]!!.getOrDefault(
                                        hour,
                                        0
                                    ) + print.quantity
                                salesPerHour.forEach { it.value.putIfAbsent(hour, 0) }
                            }
                        }
                    }

                    val sortedSalesPerHour =
                        salesPerHour.mapValues { it.value.toSortedMap() }.values.toList()

                    salesModelProducer.tryRunTransaction {
                        lineSeries {
                            sortedSalesPerHour.forEach {
                                series(it.values)
                            }
                        }
                        updateExtras {
                            it[xToHourMapKey] = sortedSalesPerHour.first().keys.toList()
                        }
                    }
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("E hh a").withZone(
                        ZoneId.systemDefault()
                    )
                    perHourFormatter = CartesianValueFormatter { x, chartValues, _ ->
                        dateTimeFormatter.format(chartValues.model.extraStore[xToHourMapKey][x.toInt()])
                    }
                    view++
                }
            }

            key(view) {
                Text(
                    text = "Top Sellers",
                    Modifier.clickable { view++ }
                )
                ProvideVicoTheme(theme = rememberM3VicoTheme()) {
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberColumnCartesianLayer(
                                columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                                    vicoTheme.lineCartesianLayerColors.map { color ->
                                        rememberLineComponent(
                                            color,
                                            16.dp,
                                            Shape.rounded(40),
                                        )
                                    }
                                ),
                                mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                                spacing = 60.dp
                            ),
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(
                                label = rememberAxisLabelComponent(lineCount = 5),
                                valueFormatter = sellerFormatter,
                                labelRotationDegrees = 0f
                            ),
                            legend = rememberHorizontalLegend(
                                items = listOf(
                                    rememberLegendItem(
                                        icon = rememberShapeComponent(
                                            Shape.Pill,
                                            vicoTheme.lineCartesianLayerColors[0]
                                        ),
                                        label = rememberTextComponent(
                                            color = vicoTheme.textColor,
                                            textSize = MaterialTheme.typography.labelLarge.fontSize
                                        ),
                                        labelText = "A3"
                                    ), rememberLegendItem(
                                        icon = rememberShapeComponent(
                                            Shape.Pill,
                                            vicoTheme.lineCartesianLayerColors[1]
                                        ),
                                        label = rememberTextComponent(
                                            color = vicoTheme.textColor,
                                            textSize = MaterialTheme.typography.labelLarge.fontSize
                                        ),
                                        labelText = "A5"
                                    )
                                ),
                                iconSize = 20.dp,
                                iconPadding = 10.dp,
                                spacing = 20.dp
                            )
                        ),
                        modifier = Modifier
                            .padding(10.dp)
                            .height(400.dp),
                        modelProducer = bestsellerModelProducer,
                        scrollState = rememberVicoScrollState(),
                        zoomState = rememberVicoZoomState()
                    )
                }

                Text(
                    text = "Sales per Hour"
                )
                val marker = rememberMarker()
                ProvideVicoTheme(theme = rememberM3VicoTheme()) {
                    CartesianChartHost(
                        chart = rememberCartesianChart(
                            rememberLineCartesianLayer(),
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(
                                label = rememberAxisLabelComponent(ellipsize = TextUtils.TruncateAt.MARQUEE),
                                valueFormatter = perHourFormatter,
                                labelRotationDegrees = 60f,
                            ),
                            legend = rememberHorizontalLegend(
                                items = artists.mapIndexed { idx, artist ->
                                    rememberLegendItem(
                                        icon = rememberShapeComponent(
                                            Shape.Pill,
                                            vicoTheme.lineCartesianLayerColors[idx]
                                        ),
                                        label = rememberTextComponent(
                                            color = vicoTheme.textColor,
                                            textSize = MaterialTheme.typography.labelLarge.fontSize
                                        ),
                                        labelText = artist.name
                                    )
                                },
                                iconSize = 20.dp,
                                iconPadding = 10.dp,
                                spacing = 20.dp
                            )
                        ),
                        modelProducer = salesModelProducer,
                        modifier = Modifier
                            .padding(10.dp)
                            .height(400.dp),
                        marker = marker,
                        scrollState = rememberVicoScrollState(),
                        zoomState = rememberVicoZoomState()
                    )
                }
            }
        }
    }
}