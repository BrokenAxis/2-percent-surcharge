package surcharge.utils

import androidx.core.text.isDigitsOnly

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