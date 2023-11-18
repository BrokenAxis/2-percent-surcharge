package surcharge.types

data class Artist(
    val name: String
)

data class Print(
    val name: String,
    val property: String,
    val url: String,
    val sizes: List<Size>,
    var stock: Map<Size, Int>,
    var price: Map<Size, Double>,
    val artist: Artist
)
data class PrintInstance(
    val name: String,
    val property: String,
    val url: String,
    val size: Size,
    var quantity: Int,
    var price: Double,
    val artist: Artist
)
enum class Size {
    A5,
    A4,
    A3,
    THICC
}

data class Bundle(
    val name: String,
    val prints: List<Print>,
    val sizes: Map<Print, Size>,
    var price: Double
)

data class BundleInstance(
    val name: String,
    val prints: List<Print>,
    val quantity: Int,
    var price: Double
)

enum class PaymentType {
    CASH,
    CARD
}

data class Sale(
    var prints: List<PrintInstance>,
    var bundles: List<BundleInstance>,
    var price: Double,
    var paymentType: PaymentType,
    var comment: String
)