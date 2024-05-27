package surcharge.ui.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import surcharge.data.prints.Data
import surcharge.types.Sale
import surcharge.utils.components.rememberMarker
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.min

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
            modifier = Modifier.padding(innerPadding)
        ) {
            var sales by remember { mutableStateOf(listOf<Sale>()) }
            val salesModelProducer = remember { CartesianChartModelProducer.build() }
            val bestsellerModelProducer = remember { CartesianChartModelProducer.build() }
            var view by remember { mutableIntStateOf(0) }
            val labelListKey = ExtraStore.Key<List<String>>()
            var sellerFormatter by remember { mutableStateOf(CartesianValueFormatter.decimal()) }
            val xToHourMapKey = ExtraStore.Key<List<Instant>>()
            var perHourFormatter by remember { mutableStateOf(CartesianValueFormatter.decimal()) }
            val salesPerHour = remember { mutableMapOf<Instant, Int>() }

            LaunchedEffect(true) {
                withContext(Dispatchers.IO) {
                    sales = data.getSales().getOrDefault(listOf())

                    // bestseller
                    val saleMap = mutableMapOf<String, Int>()
                    sales.forEach { sale ->
                        sale.prints.forEach { print ->
                            val entry = saleMap.getOrElse(print.name) { 0 }
                            saleMap[print.name] = entry + print.quantity
                        }
                        sale.bundles.forEach { bundle ->
                            bundle.prints.forEach { print ->
                                val entry = saleMap.getOrElse(print.name) { 0 }
                                saleMap[print.name] = entry + print.quantity
                            }
                        }
                    }

                    val bestsellers = saleMap.toList().sortedByDescending { it.second }
                        .slice(IntRange(0, min(6, saleMap.size - 1))).toMap()
                    bestsellerModelProducer.tryRunTransaction {
                        columnSeries {
                            series(bestsellers.values)
                            updateExtras { it[labelListKey] = bestsellers.keys.toList() }
                        }
                    }
                    sellerFormatter =
                        CartesianValueFormatter { x, chartValues, _ -> chartValues.model.extraStore[labelListKey][x.toInt()] }


                    // sales per hour
                    sales.forEach { sale ->
                        val hour = sale.time.truncatedTo(ChronoUnit.HOURS)
                        sale.prints.forEach { print ->
                            val entry = salesPerHour.getOrElse(hour) { 0 }
                            salesPerHour[hour] = entry + print.quantity
                        }

                        sale.bundles.forEach { bundle ->
                            bundle.prints.forEach { print ->
                                val entry = salesPerHour.getOrElse(hour) { 0 }
                                salesPerHour[hour] = entry + print.quantity
                            }
                        }
                    }

                    val sortedSalesPerHour = salesPerHour.toSortedMap()

                    salesModelProducer.tryRunTransaction {
                        lineSeries { series(sortedSalesPerHour.values) }
                        updateExtras { it[xToHourMapKey] = sortedSalesPerHour.keys.toList() }
                    }
                    val dateTimeFormatter = DateTimeFormatter.ofPattern("E, hh:mm a").withZone(
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
                            rememberColumnCartesianLayer(),
                            startAxis = rememberStartAxis(),
                            bottomAxis = rememberBottomAxis(valueFormatter = sellerFormatter),
                        ),
                        modifier = Modifier.padding(10.dp),
                        modelProducer = bestsellerModelProducer,
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
                            bottomAxis = rememberBottomAxis(valueFormatter = perHourFormatter),
                            persistentMarkers = mapOf(salesPerHour.size.toFloat() to marker)
                        ),
                        modelProducer = salesModelProducer,
                        modifier = Modifier.padding(10.dp),
                        marker = marker
                    )
                }
                Text(
                    text = "Sales per Day (Fake)"
                )

                val modelProducer = remember { CartesianChartModelProducer.build() }
                LaunchedEffect(Unit) {
                    modelProducer.tryRunTransaction {
                        lineSeries {
                            series(4, 12, 8, 16)
                            series(2, 4, 6, 8)
                        }
                    }
                }
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        startAxis = rememberStartAxis(),
                        bottomAxis = rememberBottomAxis(),
                    ),
                    modelProducer,
                )
            }
        }
    }
}