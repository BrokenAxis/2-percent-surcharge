package surcharge.ui.review

import android.text.TextUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import surcharge.data.prints.Firestore
import surcharge.types.Artist
import surcharge.types.Sale
import surcharge.types.Size
import surcharge.utils.components.rememberMarker
import surcharge.utils.formatDate
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
            var selectedSales by remember { mutableStateOf(listOf<Sale>()) }
            var artists by remember { mutableStateOf(listOf<Artist>()) }
            var selectedArtists by remember { mutableStateOf(listOf<Artist>()) }
            val salesModelProducer = remember { CartesianChartModelProducer.build() }
            val bestsellerModelProducer = remember { CartesianChartModelProducer.build() }
            var view by remember { mutableIntStateOf(0) }

            val labelListKey = remember { ExtraStore.Key<List<String>>() }
            var sellerFormatter =
                remember { CartesianValueFormatter { x, chartValues, _ -> chartValues.model.extraStore[labelListKey][x.toInt()] } }

            val xToHourMapKey = remember { ExtraStore.Key<List<Instant>>() }

            val dateTimeFormatter = remember {
                DateTimeFormatter.ofPattern("E hh a").withZone(
                    ZoneId.systemDefault()
                )
            }
            val perHourFormatter by remember {
                mutableStateOf(CartesianValueFormatter { x, chartValues, _ ->
                    dateTimeFormatter.format(chartValues.model.extraStore[xToHourMapKey][x.toInt()])
                })
            }

            var dateStart by remember { mutableStateOf(Instant.MIN) }
            var dateEnd by remember { mutableStateOf(Instant.MAX) }
            var refreshData by remember { mutableIntStateOf(0) }

            LaunchedEffect(true) {
                sales = (data as Firestore).getCachedSales().getOrDefault(listOf())
                artists = data.getArtists().getOrDefault(listOf())
                selectedArtists = artists
                refreshData++
            }

            LaunchedEffect(refreshData) {
                withContext(IO) {
                    selectedSales = sales.filter { sale ->
                        val time = Instant.parse(sale.time)
                        time.isAfter(dateStart) && time.isBefore(dateEnd)
                    }

                    if (selectedSales.isNotEmpty() && selectedArtists.isNotEmpty()) {

                        // bestseller
                        val saleMapA3 = mutableMapOf<String, Int>()
                        val saleMapA4 = mutableMapOf<String, Int>()
                        val saleMapA5 = mutableMapOf<String, Int>()
                        selectedSales.forEach { sale ->
                            sale.prints.forEach { print ->
                                if (selectedArtists.any { it.name == print.artist }) {
                                    when (print.size) {
                                        Size.A3 -> {
                                            val entry = saleMapA3.getOrDefault(print.name, 0)
                                            saleMapA3[print.name] = entry + print.quantity
                                            saleMapA4.putIfAbsent(print.name, 0)
                                            saleMapA5.putIfAbsent(print.name, 0)
                                        }

                                        Size.A4 -> {
                                            saleMapA4[print.name] = saleMapA4.getOrDefault(
                                                print.name,
                                                0
                                            ) + print.quantity
                                            saleMapA3.putIfAbsent(print.name, 0)
                                            saleMapA5.putIfAbsent(print.name, 0)
                                        }

                                        Size.A5 -> {
                                            saleMapA5[print.name] =
                                                saleMapA5.getOrDefault(
                                                    print.name,
                                                    0
                                                ) + print.quantity
                                            saleMapA3.putIfAbsent(print.name, 0)
                                            saleMapA4.putIfAbsent(print.name, 0)
                                        }

                                        Size.THICC -> {}
                                    }
                                }
                            }
                            sale.bundles.forEach { bundle ->
                                bundle.prints.forEach { print ->
                                    if (selectedArtists.any { it.name == print.artist }) {
                                        when (print.size) {
                                            Size.A3 -> {
                                                val entry = saleMapA3.getOrDefault(print.name, 0)
                                                saleMapA3[print.name] = entry + print.quantity
                                                saleMapA4.putIfAbsent(print.name, 0)
                                                saleMapA5.putIfAbsent(print.name, 0)
                                            }

                                            Size.A4 -> {
                                                saleMapA4[print.name] = saleMapA4.getOrDefault(
                                                    print.name,
                                                    0
                                                ) + print.quantity
                                                saleMapA3.putIfAbsent(print.name, 0)
                                                saleMapA5.putIfAbsent(print.name, 0)
                                            }

                                            Size.A5 -> {
                                                saleMapA5[print.name] =
                                                    saleMapA5.getOrDefault(
                                                        print.name,
                                                        0
                                                    ) + print.quantity
                                                saleMapA3.putIfAbsent(print.name, 0)
                                                saleMapA4.putIfAbsent(print.name, 0)
                                            }

                                            Size.THICC -> {}
                                        }
                                    }
                                }
                            }
                        }

                        val bestsellersA3 = saleMapA3.toList()
                            .sortedByDescending {
                                it.second + saleMapA5.getOrDefault(
                                    it.first,
                                    0
                                ) + saleMapA4.getOrDefault(it.first, 0)
                            }
                            .toMap()
                        val bestsellersA4 = saleMapA4.toList()
                            .sortedByDescending {
                                it.second + saleMapA3.getOrDefault(
                                    it.first,
                                    0
                                ) + saleMapA5.getOrDefault(it.first, 0)
                            }
                            .toMap()
                        val bestsellersA5 = saleMapA5.toList()
                            .sortedByDescending {
                                it.second + saleMapA3.getOrDefault(
                                    it.first,
                                    0
                                ) + saleMapA4.getOrDefault(it.first, 0)
                            }
                            .toMap()

                        bestsellerModelProducer.tryRunTransaction {
                            columnSeries {
                                series(bestsellersA3.values)
                                series(bestsellersA4.values)
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

                        selectedArtists.forEach { artist ->
                            salesPerHour[artist.name] = mutableMapOf()
                        }

                        selectedSales.forEach { sale ->
                            val hour = Instant.parse(sale.time).truncatedTo(ChronoUnit.HOURS)
                            sale.prints.forEach { print ->
                                if (selectedArtists.any { it.name == print.artist }) {
                                    salesPerHour.putIfAbsent(print.artist, mutableMapOf())
                                    salesPerHour[print.artist]!![hour] =
                                        salesPerHour[print.artist]!!.getOrDefault(
                                            hour,
                                            0
                                        ) + print.quantity
                                    salesPerHour.forEach { it.value.putIfAbsent(hour, 0) }
                                }
                            }

                            sale.bundles.forEach { bundle ->
                                bundle.prints.forEach { print ->
                                    if (selectedArtists.any { it.name == print.artist }) {
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
                        view++
                    }
                }
            }

            var openDateRangePickerDialog by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (dateStart != Instant.MIN && dateEnd != Instant.MAX) {
                    Text(
                        text = "${formatDate(dateStart)} - ${formatDate(dateEnd)}",
                        modifier = Modifier.padding(20.dp)
                    )
                } else {
                    Text(
                        text = "No Date Range Set",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                FilledTonalButton(
                    onClick = { openDateRangePickerDialog = true },
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text("Choose Date Range")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val artistSelected = remember { mutableStateListOf<Boolean>() }
                if (artistSelected.isEmpty()) artists.forEach { _ ->
                    artistSelected.add(
                        true
                    )
                }
                artists.forEachIndexed { idx, artist ->
                    FilterChip(
                        label = { Text(artist.name) },
                        modifier = Modifier.padding(5.dp),
                        selected = artistSelected[idx],
                        onClick = {
                            artistSelected[idx] = !artistSelected[idx]
                            selectedArtists = artists.filterIndexed { i, _ -> artistSelected[i] }
                            refreshData++
                        },
                    )
                }
            }


            if (openDateRangePickerDialog) {
                val dateRangePickerState = rememberDateRangePickerState(
                    initialSelectedStartDateMillis = 1721397600000,
                    initialSelectedEndDateMillis = 1721484000000
                )
                val confirmEnabled = remember {
                    derivedStateOf { dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null }
                }
                DatePickerDialog(
                    onDismissRequest = {
                        openDateRangePickerDialog = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDateRangePickerDialog = false
                                dateStart =
                                    Instant.ofEpochMilli(dateRangePickerState.selectedStartDateMillis!!)
                                dateEnd =
                                    Instant.ofEpochMilli(dateRangePickerState.selectedEndDateMillis!!)
                                        .plus(1, ChronoUnit.DAYS)
                                refreshData++
                            },
                            enabled = confirmEnabled.value
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            openDateRangePickerDialog = false
                        }) { Text("Cancel") }
                    }
                ) {
                    DateRangePicker(
                        state = dateRangePickerState
                    )

                }
            }

            if (selectedSales.isEmpty() || selectedArtists.isEmpty()) {
                Text(
                    text = "No Sales Found With Current Filters",
                    modifier = Modifier.padding(20.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            } else {
                key(view) {
                    Text(
                        text = "Top Sellers",
                        Modifier.padding(start = 20.dp),
                        style = MaterialTheme.typography.titleLarge
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
                                        ),
                                        rememberLegendItem(
                                            icon = rememberShapeComponent(
                                                Shape.Pill,
                                                vicoTheme.lineCartesianLayerColors[1]
                                            ),
                                            label = rememberTextComponent(
                                                color = vicoTheme.textColor,
                                                textSize = MaterialTheme.typography.labelLarge.fontSize
                                            ),
                                            labelText = "A4"
                                        ),
                                        rememberLegendItem(
                                            icon = rememberShapeComponent(
                                                Shape.Pill,
                                                vicoTheme.lineCartesianLayerColors[2]
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
                        text = "Sales per Hour",
                        modifier = Modifier.padding(start = 20.dp),
                        style = MaterialTheme.typography.titleLarge
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
                                    items = selectedArtists.mapIndexed { idx, artist ->
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
}