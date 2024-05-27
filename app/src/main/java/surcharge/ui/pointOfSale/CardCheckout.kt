package surcharge.ui.pointOfSale

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.squareup.sdk.pos.ChargeRequest
import com.squareup.sdk.pos.CurrencyCode
import com.squareup.sdk.pos.PosClient

fun handleCardCheckout(
    total: Int,
    posClient: PosClient,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onError: (String) -> Unit
) {
    val request = ChargeRequest.Builder(
        total,
        CurrencyCode.AUD
    )
        .build()
    try {
        val intent: Intent = posClient.createChargeIntent(request)
        launcher.launch(intent)
    } catch (e: ActivityNotFoundException) {
        onError(e.localizedMessage ?: "")
    }
}

