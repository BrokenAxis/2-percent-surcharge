package surcharge.utils.square

import androidx.compose.material3.SnackbarHostState
import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
import com.squareup.sdk.mobilepayments.cardreader.ReaderChangedEvent
import com.squareup.sdk.mobilepayments.cardreader.ReaderInfo
import com.squareup.sdk.mobilepayments.core.CallbackReference
import com.squareup.sdk.mobilepayments.core.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

fun observeReaderChanges(
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
): CallbackReference {
    return MobilePaymentsSdk.readerManager().setReaderChangedCallback { event ->
        when (event.change) {
            ReaderChangedEvent.Change.ADDED -> scope.launch(IO) { snackbarHostState.showSnackbar("${event.reader.name} added!") }
            ReaderChangedEvent.Change.REMOVED -> scope.launch(IO) { snackbarHostState.showSnackbar("${event.reader.name} removed!") }
            ReaderChangedEvent.Change.CHANGED_STATE -> {
                val message = when (event.reader.state) {
                    ReaderInfo.State.Ready -> "${event.reader.name} is ready"
                    ReaderInfo.State.Connecting -> "${event.reader.name} connecting"
                    ReaderInfo.State.Disabled -> "${event.reader.name} is disabled"
                    is ReaderInfo.State.Disconnected -> "${event.reader.name} disconnected"
                    is ReaderInfo.State.FailedToConnect -> "${event.reader.name} failed to connect"
                    ReaderInfo.State.UpdatingFirmware -> "${event.reader.name} updating firmware"
                }
                scope.launch(IO) { snackbarHostState.showSnackbar(message) }
            }

            ReaderChangedEvent.Change.BATTERY_THRESHOLD -> {}
            ReaderChangedEvent.Change.BATTERY_CHARGING -> {
                val message = when (event.reader.batteryStatus!!.isCharging) {
                    true -> "${event.reader.name} plugged into charger"
                    false -> "${event.reader.name} unplugged from charger"
                }
                scope.launch(IO) { snackbarHostState.showSnackbar(message) }
            }

            ReaderChangedEvent.Change.FIRMWARE_PROGRESS -> {}
        }
    }
}

fun showSettings(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    MobilePaymentsSdk.settingsManager().showSettings { result ->
        when (result) {
            is Result.Success -> {}
            is Result.Failure -> scope.launch(IO) {
                snackbarHostState.showSnackbar(
                    "Error showing settings: ${result.errorMessage}"
                )
            }
        }
    }
}