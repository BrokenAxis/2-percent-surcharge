package surcharge.types

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

// note default values will not be initialised into the database schema
// this only becomes a problem if directly using SQL statements instead of the interface
// change to the following if necessary:
// @ColumnProperties(defaultValue = "") var a: String

@Entity
data class Artist(
    @PrimaryKey val name: String = "",
    var image: String = "https://cdn.britannica.com/90/236590-050-27422B8D/Close-up-of-mushroom-growing-on-field.jpg"
)

@Entity(primaryKeys = ["name", "artist"])
data class Print(
    var name: String = "",
    var property: String = "",
    var url: String = "",
    var sizes: List<Size> = listOf(),
    var stock: MutableMap<Size, Int> = mutableMapOf(),
    var price: MutableMap<Size, Int> = mutableMapOf(),
    var artist: String = ""
)

interface Item {
    val name: String
    var price: Int
    var quantity: Int
}

data class PrintItem(
    override val name: String = "",
    val property: String = "",
    val url: String = "",
    val size: Size = Size.A5,
    override var quantity: Int = 0,
    override var price: Int = 0,
    val artist: String = ""
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

@Entity
data class Bundle(
    @PrimaryKey var name: String = "",
    var prints: List<PrintItem> = listOf(),
    var price: Int = 0
)

data class BundleItem (
    override val name: String = "",
    val prints: List<PrintItem> = listOf(),
    override var quantity: Int = 0,
    override var price: Int = 0
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

@Entity
data class Sale(
    @PrimaryKey val saleId: UUID = UUID.randomUUID(),
    var prints: ArrayList<PrintItem> = arrayListOf(),
    var bundles: ArrayList<BundleItem> = arrayListOf(),
    var price: Int = 0,
    var paymentType: PaymentType = PaymentType.CASH,
    var comment: String = "",
    var time: Instant = Instant.now()
)