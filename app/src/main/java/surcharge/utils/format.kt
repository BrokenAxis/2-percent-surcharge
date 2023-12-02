package surcharge.utils

import androidx.core.text.isDigitsOnly
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun formatPrice(price: Int): String {
    return when (price % 100) {
        0 -> "${price / 100}.00"
        else -> "${price / 100}.${price % 100}"
    }
}

// get the Int from of formatted a price string
fun intPrice(price: String): Int {
    return (price.substringBefore('.') + price.substringAfter('.')).toInt()
}

// confirm that a price is formatted correctly
fun validatePrice(price: String): Boolean {
    return  price.length >= 4
            && price[price.length - 3] == '.'
            && price.substringAfter('.').isDigitsOnly()
            && price.substringBefore('.').isDigitsOnly()
}

fun formatTime(timestamp: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a")
        .withZone(ZoneId.systemDefault())

    return formatter.format(timestamp)
}