package surcharge.data.prints

import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale

interface Data {
    suspend fun getPrint(name: String): Result<Print>
    suspend fun getPrints(): Result<List<Print>>
    suspend fun addPrint(print: Print)
    suspend fun editPrint(print: Print) : Boolean
    suspend fun deletePrint(print: Print): Boolean
    suspend fun getPrints(artist: Artist): Result<List<Print>>
    suspend fun getBundle(name: String): Result<Bundle>
    suspend fun getBundles(): Result<List<Bundle>>
    suspend fun editBundle(bundle: Bundle) : Boolean
    suspend fun deleteBundle(bundle: Bundle): Boolean
    suspend fun addBundle(bundle: Bundle)
    suspend fun getArtists(): Result<List<Artist>>
    suspend fun addArtist(artist: Artist)
    suspend fun deleteArtist(artist: Artist): Boolean
    suspend fun getSale(saleId: String): Result<Sale>
    suspend fun getSales(): Result<List<Sale>>
    suspend fun addSale(sale: Sale)
    suspend fun deleteSale(sale: Sale): Boolean

    suspend fun reset()
    suspend fun reload()
}