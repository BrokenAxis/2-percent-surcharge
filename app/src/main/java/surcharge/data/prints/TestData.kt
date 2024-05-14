package surcharge.data.prints

import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.PaymentType
import surcharge.types.Print
import surcharge.types.Sale
import surcharge.types.Size
import surcharge.types.createPrintItem

val matthew = Artist("Matthew", "https://res.cloudinary.com/domzlxwcp/image/upload/v1715668790/display%20pictures/oebvh5o0idwqywtf7qz5.png")
val vincent = Artist("Vincent", "https://res.cloudinary.com/domzlxwcp/image/upload/v1715668790/display%20pictures/s9pxpslgulwg0hslahug.png")

var artists = listOf(matthew, vincent)

val asa = Print(
    "Asa and Yoru",
    "Chainsaw Man",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2FAsaAndYoru.6d6ffc83.jpg&f=webp",
    listOf(Size.A5, Size.A3),
    mutableMapOf(Size.A5 to 10, Size.A3 to 5),
    mutableMapOf(Size.A5 to 3000, Size.A3 to 5000),
    "Matthew"
)

val link = Print(
    "Link",
    "The Legend of Zelda",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fof+the+wild.a8b65806.jpg&f=webp",
    listOf(Size.A5, Size.A3),
    mutableMapOf(Size.A5 to 10, Size.A3 to 5),
    mutableMapOf(Size.A5 to 3000, Size.A3 to 5000),
    "Matthew"
)

val quanxi = Print(
    "Quanxi",
    "Chainsaw Man",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2FQuanxi.2819ef9d.png&f=webp",
    listOf(Size.A5, Size.A3),
    mutableMapOf(Size.A5 to 10, Size.A3 to 5),
    mutableMapOf(Size.A5 to 3000, Size.A3 to 5000),
    "Matthew"
)

val daijin = Print(
    "Daijin",
    "Suzume",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fdaijin.92ac5ad6.jpg&f=webp",
    listOf(Size.A5, Size.A3),
    mutableMapOf(Size.A5 to 10, Size.A3 to 5),
    mutableMapOf(Size.A5 to 3000, Size.A3 to 5000),
    "Matthew"
)

val spot = Print(
    "The Spot",
    "Spiderverse",
    "https://res.cloudinary.com/domzlxwcp/image/upload/v1701323925/prints/the%20spot.jpg",
    listOf(Size.A5, Size.A3),
    mutableMapOf(Size.A5 to 10, Size.A3 to 5),
    mutableMapOf(Size.A5 to 3000, Size.A3 to 5000),
    "Vincent"
)

val angel = Print(
    "Angel Devil",
    "Chainsaw Man",
    "https://res.cloudinary.com/domzlxwcp/image/upload/v1701324151/prints/angel%20devil.jpg",
    listOf(Size.A5, Size.A3),
    mutableMapOf(Size.A5 to 10, Size.A3 to 5),
    mutableMapOf(Size.A5 to 3000, Size.A3 to 5000),
    "Vincent"
)

var prints = listOf(asa, link, quanxi, daijin, spot, angel)

val chainsawBundle = Bundle(
    "Chainsaw Man Bundle",
    listOf(createPrintItem(asa, Size.A3), createPrintItem(angel, Size.A3)),
    8000
)

val randomBundle = Bundle(
    "Random Bundle",
    listOf(createPrintItem(link, Size.A3), createPrintItem(spot, Size.A3)),
    8000
)

var bundles = listOf(chainsawBundle, randomBundle)

val img = listOf(
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2FAsaAndYoru.6d6ffc83.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fof+the+wild.a8b65806.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2FQuanxi.2819ef9d.png&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fdaijin.92ac5ad6.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fezreal.6ece496d.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fmakina+shrine.077071ad.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fsuzume.c8483f36.jpg&f=webp"
)

val img2 = listOf(
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fof+the+wild.a8b65806.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2FQuanxi.2819ef9d.png&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fdaijin.92ac5ad6.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fezreal.6ece496d.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fmakina+shrine.077071ad.jpg&f=webp",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fsuzume.c8483f36.jpg&f=webp"
)

val sale1 = Sale(
    prints = arrayListOf(
        createPrintItem(asa, Size.A5, 2),
        createPrintItem(spot, Size.A5, 2),
    ),
    price = 6969,
    paymentType = PaymentType.CARD,
    comment = "hi there"
)

var sales = listOf(sale1,)