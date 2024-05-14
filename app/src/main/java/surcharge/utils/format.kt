package surcharge.utils

import androidx.core.text.isDigitsOnly
import surcharge.types.Artist
import surcharge.types.Sale
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
    return price.length >= 4
            && price[price.length - 3] == '.'
            && price.substringAfter('.').isDigitsOnly()
            && price.substringBefore('.').isDigitsOnly()
}

fun formatTime(timestamp: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm a")
        .withZone(ZoneId.systemDefault())

    return formatter.format(timestamp)
}

fun quantity(sale: Sale): Int {
    return sale.prints.sumOf { it.quantity } + sale.bundles.sumOf { it.quantity }
}

fun artistTotal(sales: List<Sale>, artist: Artist): Int {
    return sales.sumOf { sale ->
        sale.prints.sumOf {
            if (it.artist == artist.name) it.price
            else 0
        } + sale.bundles.sumOf { bundle ->

            (bundle.price.toDouble() * (bundle.prints.count {
                it.artist == artist.name
            }.toDouble() / sale.bundles.size.toDouble())).toInt()
        }
    }
}