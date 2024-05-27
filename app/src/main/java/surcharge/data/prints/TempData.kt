package surcharge.data.prints

import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale
import java.util.UUID

// Data Interface for testing purposes
class TempData: Data {

    private var data = TestData()

    override suspend fun getPrint(name: String): Result<Print> {
        return Result.success(data.prints.find { it.name == name } ?: Print())
    }

    override suspend fun getPrints(): Result<List<Print>> {
        return Result.success(data.prints)
    }

    override suspend fun getPrints(artist: Artist): Result<List<Print>> {
        return Result.success(data.prints.filter { it.artist == artist.name })
    }

    override suspend fun addPrint(print: Print) {
        data.prints += print
    }

    override suspend fun editPrint(print: Print): Boolean {
        val index = data.prints.indexOfFirst { it.name == print.name }
        data.prints[index].sizes = print.sizes
        data.prints[index].price = print.price
        return true
    }

    override suspend fun getBundles(): Result<List<Bundle>> {
        return Result.success(data.bundles)
    }

    override suspend fun editBundle(bundle: Bundle): Boolean {
        val index = data.bundles.indexOfFirst { it.name == bundle.name }
        data.bundles[index].prints = bundle.prints
        data.bundles[index].price = bundle.price
        return true
    }

    override suspend fun addBundle(bundle: Bundle) {
        data.bundles += bundle
    }

    override suspend fun getArtists(): Result<List<Artist>> {
        return Result.success(data.artists)
    }

    override suspend fun addArtist(artist: Artist) {
        data.artists += artist
    }

    override suspend fun getSale(saleId: UUID): Result<Sale> {
        return Result.success(data.sales.find { it.saleId == saleId } ?: Sale())
    }

    override suspend fun getSales(): Result<List<Sale>> {
        return Result.success(data.sales)
    }

    override suspend fun addSale(sale: Sale) {
        data.sales += sale
    }

    override suspend fun reset() {
        data.prints = listOf()
        data.bundles = listOf()
        data.artists = listOf()
        data.sales = listOf()
    }

    override suspend fun reload() {
        data = TestData()
    }
}