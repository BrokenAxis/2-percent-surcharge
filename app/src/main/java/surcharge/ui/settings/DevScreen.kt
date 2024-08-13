package surcharge.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DatasetLinked
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale
import surcharge.utils.components.Tile
import surcharge.utils.formatPrice
import surcharge.utils.gson


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevScreen(
    app: AppContainer,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Developer Options") },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val scope = rememberCoroutineScope()

            var delete by remember { mutableStateOf(false) }
            if (delete) {
                AlertDialog(
                    onDismissRequest = { delete = false },
                    confirmButton = {
                        TextButton(onClick = {
                            scope.launch(IO) { app.data.reset() }
                            delete = false
                        }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { delete = false }) {
                            Text("Cancel")
                        }
                    },
                    icon = { Icon(Icons.Filled.DeleteForever, "Delete Data") },
                    title = { Text("Delete Data") },
                    text = { Text("Are you sure you want to delete all local data?") },
                )
            }

            Tile(title = "Delete Data",
                subtitle = "Wipe local data and reset app",
                icon = Icons.Filled.DeleteForever,
                onClick = { delete = true }
            )

            Tile(title = "Load Test Data",
                subtitle = "Overwrite current database with a set of testing values",
                icon = Icons.Filled.DatasetLinked,
                onClick = { scope.launch { withContext(IO) { app.data.reload() } } }
            )

            val context = LocalContext.current
            var artists by remember { mutableStateOf(listOf<Artist>()) }
            var prints by remember { mutableStateOf(listOf<Print>()) }
            var bundles by remember { mutableStateOf(listOf<Bundle>()) }
            var sales by remember { mutableStateOf(listOf<Sale>()) }
            LaunchedEffect(true) {
                withContext(IO) {
                    artists = app.data.getArtists().getOrDefault(listOf())
                    prints = app.data.getPrints().getOrDefault(listOf())
                    bundles = app.data.getBundles().getOrDefault(listOf())
                    sales = app.data.getSales().getOrDefault(listOf())
                }
            }

            val l = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/x-sqlite3")
            ) {
                if (it != null) {
                    val file = context.contentResolver.openOutputStream(it)

                    val json = Gson().toJson(Data(artists, prints, bundles))
                    file?.write(json.toByteArray())
                    file?.close()
                }
            }

            Tile(
                title = "Export Prints",
                subtitle = "Export artists, prints and bundles to an external file",
                icon = Icons.Filled.ImportExport,
                onClick = {
                    l.launch("surcharge_db")
                }
            )

            val importLauncher =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
                    if (it != null) {
                        val file = context.contentResolver.openInputStream(it)
                        if (file != null) {
                            val json = file.readBytes()
                            val newData =
                                Gson().fromJson(json.decodeToString(), Data::class.java)
                            scope.launch(IO) {
                                newData.artists.forEach { artist ->
                                    app.data.addArtist(artist)
                                }
                                newData.prints.forEach { print ->
                                    app.data.addPrint(print)
                                }
                                newData.bundles.forEach { bundle ->
                                    app.data.addBundle(bundle)
                                }
                            }
                        }
                        file?.close()
                    }
                }

            Tile(
                title = "Import Prints",
                subtitle = "Add prints, bundles and artists from an external file. Will overwrite prints and bundles with the same name and artist. Will overwrite artists of the same name. Do not add the wrong file.",
                icon = Icons.Filled.DatasetLinked,
                onClick = {
                    importLauncher.launch(arrayOf("*/*"))
                }
            )

            val salesImport =
                rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) {
                    if (it != null) {
                        val file = context.contentResolver.openInputStream(it)
                        if (file != null) {
                            val json = file.readBytes()
                            val newSales =
                                gson.fromJson(json.decodeToString(), Array<Sale>::class.java)
                            scope.launch(IO) {
                                newSales.forEach { sale ->
                                    app.data.addSale(sale)
                                }
                            }
                        }
                        file?.close()
                    }
                }
            val salesLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("application/x-sqlite3")
            ) {
                if (it != null) {
                    val file = context.contentResolver.openOutputStream(it)

                    val json = gson.toJson(sales)
                    file?.write(json.toByteArray())
                    file?.close()
                }
            }

            Tile(title = "Export Sales",
                subtitle = "Export sales to an external file",
                icon = Icons.Filled.ImportExport,
                onClick = {
                    salesLauncher.launch("sales_db")
                }
            )

            Tile(title = "Import Sales",
                subtitle = "Import sales to an external file. Does not delete existing sales",
                icon = Icons.Filled.DatasetLinked,
                onClick = {
                    salesImport.launch(arrayOf("*/*"))
                }
            )

            val csvLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("text/csv")
            ) { path ->

                if (path != null) {
                    val file = context.contentResolver.openOutputStream(path)
                    file?.write("saleId,item,size,quantity,artist,price\n".toByteArray())
                    sales.forEach { sale ->
                        // export per print
                        sale.bundles.forEach { bundle ->
                            bundle.prints.forEach { print ->
                                val row =
                                    "${sale.saleId},${print.name},${print.size},${print.quantity},${print.artist},${
                                        formatPrice(print.price)
                                    }\n"
                                file?.write(row.toByteArray())
                            }
                            val row =
                                "${sale.saleId},${bundle.name},\"\",\"\",\"\",${formatPrice(bundle.price - bundle.prints.sumOf { it.price * it.quantity })}\n"
                            file?.write(row.toByteArray())
                        }
                        sale.prints.forEach { print ->
                            val row =
                                "${sale.saleId},${print.name},${print.size},${print.quantity},${print.artist},${
                                    formatPrice(print.price)
                                }\n"
                            file?.write(row.toByteArray())
                        }
                        // export per sale
//                        file?.write("saleId,item,size,quantity,price,payment type,comment,time\n".toByteArray())
//                        val row =
//                            "${sale.saleId},\"${sale.prints.map { "${it.name} x ${it.quantity}" }}\",\"${sale.bundles.map { "${it.name} x ${it.quantity}: " + it.prints.map { print -> "${print.name} x ${print.quantity}" } }}\",${
//                                formatPrice(sale.price)
//                            },${sale.paymentType},\"${sale.comment}\",\"${formatTime(sale.time)}\"\n"
//                        file?.write(row.toByteArray())
                    }
                    file?.close()
                }
            }

            Tile(title = "Export Sales to CSV",
                subtitle = "Export to a CSV file, (excel or google sheets compatible)",
                icon = Icons.Filled.DatasetLinked,
                onClick = {
                    csvLauncher.launch("sales.csv")
                }
            )

            Tile(title = "Sync",
                subtitle = "Attempt to sync local data with backend",
                icon = Icons.Filled.Sync,
                onClick = { scope.launch { snackbarHostState.showSnackbar("Not implemented! Gaslight a certain goomba into working on it") } }
            )
        }
    }
}

data class Data(
    val artists: List<Artist>,
    val prints: List<Print>,
    val bundles: List<Bundle>
)