package surcharge.ui.pointOfSale

import com.squareup.sdk.mobilepayments.MobilePaymentsSdk
import com.squareup.sdk.mobilepayments.core.Result
import com.squareup.sdk.mobilepayments.payment.CurrencyCode
import com.squareup.sdk.mobilepayments.payment.Money
import com.squareup.sdk.mobilepayments.payment.Payment
import com.squareup.sdk.mobilepayments.payment.PaymentParameters
import com.squareup.sdk.mobilepayments.payment.PromptParameters
import java.util.UUID

fun handleCardCheckout(
    total: Int,
    saleId: String,
    onSuccess: (Payment?) -> Unit,
    onError: (String) -> Unit
) {
    val paymentManager = MobilePaymentsSdk.paymentManager()
    val paymentParameters = PaymentParameters.Builder(
        amount = Money(total.toLong(), CurrencyCode.AUD),
        idempotencyKey = UUID.randomUUID().toString() // todo store this
    )
        .referenceId(saleId)
        .build()
    val paymentHandle = paymentManager.startPaymentActivity(
        paymentParameters = paymentParameters,
        promptParameters = PromptParameters()
    ) { result ->
        when (result) {
            is Result.Success -> onSuccess(result.value)
            is Result.Failure -> onError(result.errorMessage)
        }
    }
}

