package surcharge.data.prints

import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale
import java.util.UUID

interface Data {
    suspend fun getPrint(name: String?): Result<Print>
    suspend fun getPrints(): Result<List<Print>>
    suspend fun addPrint(print: Print): Boolean
    suspend fun editPrint(name: String, print: Print) : Boolean
    suspend fun getPrints(artist: Artist): Result<List<Print>>
    suspend fun getBundles(): Result<List<Bundle>>
    suspend fun editBundle(name: String, bundle: Bundle) : Boolean
    suspend fun addBundle(bundle: Bundle): Boolean
    suspend fun getArtists(): Result<List<Artist>>
    suspend fun addArtist(artist: Artist): Boolean
    suspend fun getSale(saleId: UUID): Result<Sale>
    suspend fun getSales(): Result<List<Sale>>
    suspend fun addSale(sale: Sale): Boolean
}