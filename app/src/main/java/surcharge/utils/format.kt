package surcharge.utils

import androidx.core.text.isDigitsOnly
import surcharge.types.Artist
import surcharge.types.Sale
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


/**
 * Returns the formatted string form of [price].
 * Prices use [Int] instead of [Double] for simplicity: `formatPrice(100) = 1.00`
 */
fun formatPrice(price: Int): String {
    return "${price / 100}.${"%02d".format(price % 100)}"
}

/**
 * Returns the [Int] form of formatted a [price] string
 */
fun intPrice(price: String): Int {
    return (price.substringBefore('.', price + "00") + price.substringAfter('.', "")).toInt()
}

/**
 * Returns `true` if the string is formatted as a valid price
 */
fun validatePrice(price: String): Boolean {
    val noDecimal = price.isNotEmpty() && price.isDigitsOnly() && !price.contains('.')
    val decimal = price.length >= 4
            && price[price.length - 3] == '.'
            && price.substringAfter('.').isDigitsOnly()
            && price.substringBefore('.').isDigitsOnly()
    return price.length < 8 && (noDecimal || decimal)
}

fun formatTime(timestamp: Instant): String {
    val formatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm a").withZone(ZoneId.systemDefault())

    return formatter.format(timestamp)
}

fun formatDate(timestamp: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault())
    return formatter.format(timestamp)
}

fun quantity(sale: Sale): Int {
    return sale.prints.sumOf { it.quantity } + sale.bundles.sumOf { it.quantity }
}

fun artistTotal(sales: List<Sale>, artist: Artist): Int {
    return sales.sumOf { sale ->
        sale.prints.sumOf {
            if (it.artist == artist.name) it.price * it.quantity
            else 0
        } + sale.bundles.sumOf { bundle ->
            (bundle.price.toDouble() * (bundle.prints.sumOf {
                if (it.artist == artist.name) it.quantity * it.price
                else 0
            }.toDouble() / bundle.prints.sumOf { it.price * it.quantity }.toDouble())).toInt()
        }
    }
}

/**
 * Split sales by using total sale amount rather than considering bundle discounts separately
 */
fun altArtistTotal(sales: List<Sale>, artist: Artist): Int {
    return sales.sumOf { sale ->
        val artistEarningsInSale = sale.prints.filter { it.artist == artist.name }
            .sumOf { it.price * it.quantity } +
                sale.bundles.sumOf { bundle ->
                    bundle.prints.filter { it.artist == artist.name }
                        .sumOf { it.quantity * it.price }
                }
        val totalSaleValue = sale.prints.sumOf { it.price * it.quantity } +
                sale.bundles.sumOf { bundle ->
                    bundle.prints.sumOf { it.price * it.quantity }
                }
        ((artistEarningsInSale.toDouble() / totalSaleValue) * sale.price).toInt()
    }
}