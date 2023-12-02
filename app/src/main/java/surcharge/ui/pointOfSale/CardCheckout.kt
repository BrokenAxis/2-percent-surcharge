package surcharge.ui.pointOfSale

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import com.squareup.sdk.pos.ChargeRequest
import com.squareup.sdk.pos.CurrencyCode
import com.squareup.sdk.pos.PosClient
import com.squareup.sdk.pos.PosSdk
import io.github.cdimascio.dotenv.dotenv


class CardCheckout : Activity() {

    companion object {
        private val dotenv = dotenv {
            directory = "/assets"
            filename = "env" // instead of '.env', use 'env'
        }

        private val applicationId = dotenv["APPLICATION_ID"]
    }

    private lateinit var posClient: PosClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        posClient = PosSdk.createClient(this, applicationId)
    }

    // create a new charge request and initiate a Point of Sale transaction
    private val chargeRequestCode = 1
    fun startTransaction(total: Int) {
        val request = ChargeRequest.Builder(
            total,
            CurrencyCode.AUD
        )
            .build()
        try {
            val intent: Intent = posClient.createChargeIntent(request)
            startActivityForResult(intent, chargeRequestCode)
        } catch (e: ActivityNotFoundException) {
            // lol
            posClient.openPointOfSalePlayStoreListing()
        }
    }

}

fun handleCardCheckout(
    total: Int,
    posClient: PosClient,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {

        val request = ChargeRequest.Builder(
            total,
            CurrencyCode.AUD
        )
            .build()
        try {
            val intent: Intent = posClient.createChargeIntent(request)
            launcher.contract
            launcher.launch(intent)
    } catch (e: ActivityNotFoundException) {
        print(e.message)
    }


}

