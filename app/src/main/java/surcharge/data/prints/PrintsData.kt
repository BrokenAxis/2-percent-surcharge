package surcharge.data.prints

import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Size

val matthew = Artist("Matthew")
val vincent = Artist("Vincent")

val asa = Print(
    "Asa and Yoru",
    "Chainsaw Man",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2FAsaAndYoru.6d6ffc83.jpg&f=webp",
    listOf(Size.A5, Size.A3),
    mapOf(Size.A5 to 10, Size.A3 to 5),
    mapOf(Size.A5 to 30.0, Size.A3 to 50.0),
    matthew
)

val link = Print(
    "Link",
    "The Legend of Zelda",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fof+the+wild.a8b65806.jpg&f=webp",
    listOf(Size.A5, Size.A3),
    mapOf(Size.A5 to 10, Size.A3 to 5),
    mapOf(Size.A5 to 30.0, Size.A3 to 50.0),
    matthew
)

val quanxi = Print(
    "Quanxi",
    "Chainsaw Man",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2FQuanxi.2819ef9d.png&f=webp",
    listOf(Size.A5, Size.A3),
    mapOf(Size.A5 to 10, Size.A3 to 5),
    mapOf(Size.A5 to 30.0, Size.A3 to 50.0),
    matthew
)

val daijin = Print(
    "Daijin",
    "Suzume",
    "https://jongjeh.vercel.app/_image?href=%2F_astro%2Fdaijin.92ac5ad6.jpg&f=webp",
    listOf(Size.A5, Size.A3),
    mapOf(Size.A5 to 10, Size.A3 to 5),
    mapOf(Size.A5 to 30.0, Size.A3 to 50.0),
    matthew
)

val spot = Print(
    "The Spot",
    "Spiderverse",
    "https://res.cloudinary.com/domzlxwcp/image/upload/v1701323925/prints/the%20spot.jpg",
    listOf(Size.A5, Size.A3),
    mapOf(Size.A5 to 10, Size.A3 to 5),
    mapOf(Size.A5 to 30.0, Size.A3 to 50.0),
    vincent
)

val angel = Print(
    "Angel Devil",
    "Chainsaw Man",
    "https://res.cloudinary.com/domzlxwcp/image/upload/v1701324151/prints/angel%20devil.jpg",
    listOf(Size.A5, Size.A3),
    mapOf(Size.A5 to 10, Size.A3 to 5),
    mapOf(Size.A5 to 30.0, Size.A3 to 50.0),
    vincent
)

val prints = listOf(asa, link, quanxi, daijin, spot, angel)

val chainsawBundle = Bundle(
    "Chainsaw Man Bundle",
    listOf(asa, angel),
    mapOf(asa to Size.A3, angel to Size.A3),
    80.0
)

val randomBundle = Bundle(
    "Random Bundle",
    listOf(link, spot),
    mapOf(link to Size.A3, spot to Size.A3),
    80.0
)

val bundles = listOf(chainsawBundle, randomBundle)

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