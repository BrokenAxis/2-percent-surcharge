package surcharge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import surcharge.data.AppContainer
import surcharge.ui.theme.earth_seed
import surcharge.ui.theme.ocean_seed
import surcharge.ui.theme.primaryLight
import surcharge.utils.components.Tile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    app: AppContainer,
    onNavigateToDev: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
    }) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            var viewPersonalisation by remember { mutableStateOf(false) }

            if (viewPersonalisation) {
                Dialog(
                    onDismissRequest = { viewPersonalisation = false },
                    DialogProperties(usePlatformDefaultWidth = false)
                ) {
                    Personalisation(app = app, onClose = { viewPersonalisation = false })
                }
            }

            Tile(title = "Personalisation",
                subtitle = "Themes, memes",
                icon = Icons.Filled.Contrast,
                onClick = { viewPersonalisation = true })

            Tile(title = "Developer Options",
                subtitle = "Debug tools",
                icon = Icons.Filled.DeveloperMode,
                onClick = { onNavigateToDev() })

            var checked by remember { mutableStateOf(false) }
            LaunchedEffect(true) {
                checked = withContext(IO) { app.settings.readAlternateSale() }
            }
            val scope = rememberCoroutineScope()
            Tile(
                title = "Alternate Sales Calculation",
                subtitle = "Calculate sales by using total sale amount rather than considering bundle discounts separately",
                icon = Icons.Filled.Calculate,
                onClick = {},
                switch = {
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            scope.launch(IO) {
                                app.settings.updateAlternateSale(checked)
                            }
                        },
                        Modifier.padding(20.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun Personalisation(
    app: AppContainer, onClose: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ), modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp)) {
            Icon(Icons.Filled.Palette, "Theme")
            Text(
                text = "Choose a Theme",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, "Close")
            }
        }
        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        Spacer(Modifier.height(10.dp))

        var selected by remember { mutableIntStateOf(0) }

        LaunchedEffect(true) {
            withContext(IO) {
                selected = app.settings.readTheme()
            }
        }

        LaunchedEffect(selected) {
            withContext(IO) {
                if (selected != app.settings.readTheme()) {
                    app.settings.updateTheme(selected)
                    app.theme.intValue = selected
                }
            }
        }

        Theme(
            onClick = {
                selected = 0
            }, name = "Ocean", colour = ocean_seed
        )

        Theme(
            onClick = {
                selected = 1
            }, name = "Earth", colour = earth_seed
        )

        Theme(
            onClick = {
                selected = 2
            }, name = "Limitless", colour = primaryLight
        )
    }

}

@Composable
fun Theme(
    onClick: () -> Unit, name: String, colour: Color

) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Circle,
                contentDescription = "$name Colour",
                tint = colour
            )

            Spacer(Modifier.width(20.dp))

            Text(text = name, style = MaterialTheme.typography.titleLarge)
        }
    }

}

//@Preview
//@Composable
//private fun Prev() {
//    SettingsScreen() {}
//}
