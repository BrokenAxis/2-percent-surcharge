package surcharge.types

import java.time.Instant
import java.util.UUID

data class Artist(
    val name: String = ""
)

data class Print(
    var name: String = "",
    var property: String = "",
    var url: String = "",
    var sizes: List<Size> = listOf(),
    var stock: MutableMap<Size, Int> = mutableMapOf(),
    var price: MutableMap<Size, Int> = mutableMapOf(),
    var artist: Artist = Artist()
)

interface Item {
    val name: String
    val price: Int
    var quantity: Int
}

data class PrintItem(
    override val name: String = "",
    val property: String = "",
    val url: String = "",
    val size: Size = Size.A5,
    override var quantity: Int = 0,
    override var price: Int = 0,
    val artist: Artist = Artist()
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
    var name: String = "",
    var prints: List<PrintItem> = listOf(),
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