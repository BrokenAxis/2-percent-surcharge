package surcharge.types

import java.time.Instant
import java.util.UUID

data class Artist(
    val name: String = ""
)

data class Print(
    val name: String = "",
    val property: String = "",
    val url: String = "",
    val sizes: List<Size> = listOf(),
    var stock: Map<Size, Int> = mapOf(),
    var price: Map<Size, Int> = mapOf(),
    val artist: Artist = Artist()
)

interface Item {
    val name: String
    val price: Int
    var quantity: Int
}

data class PrintItem(
    override val name: String,
    val property: String,
    val url: String,
    val size: Size,
    override var quantity: Int,
    override var price: Int,
    val artist: Artist
) : Item

fun createPrintItem(print: Print, size: Size, quantity: Int = 1, price: Int? = null): PrintItem {
    return PrintItem(
        print.name,
        print.property,
        print.url,
        size,
        quantity,
        price?: print.price[size]!!, // TODO sussy
        print.artist
    )
}

enum class Size {
    A5,
    A4,
    A3,
    THICC
}

data class Bundle(
    val name: String = "",
    val prints: List<PrintItem> = listOf(),
    var price: Int = 0
)

data class BundleItem (
    override val name: String,
    val prints: List<PrintItem>,
    override var quantity: Int,
    override var price: Int
) : Item

fun createBundleItem(bundle: Bundle, quantity: Int = 1, price: Int? = null): BundleItem {
    return BundleItem(
        bundle.name,
        bundle.prints,
        quantity,
        price?: bundle.price
    )
}

enum class PaymentType {
    CASH,
    CARD
}

data class Sale(
    val saleId: UUID = UUID.randomUUID(),
    var items: ArrayList<Item> = arrayListOf(),
    var price: Int = 0,
    var paymentType: PaymentType = PaymentType.CASH,
    var comment: String = "",
    var time: Instant = Instant.now()
)