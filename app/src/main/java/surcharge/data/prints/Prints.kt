package surcharge.data.prints

import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print

interface Prints {
    suspend fun getPrint(name: String?): Result<Print>
    suspend fun getPrints(): Result<List<Print>>
    suspend fun getPrints(artist: Artist): Result<List<Print>>

    suspend fun getBundles(): Result<List<Bundle>>
}