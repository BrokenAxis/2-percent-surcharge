package surcharge.data.prints

import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale
import java.util.UUID

// Data Interface for testing purposes
class DataImpl: Data {

    override suspend fun getPrint(name: String?): Result<Print> {
        return Result.success(prints.find { it.name == name }?: asa)
    }

    override suspend fun getPrints(): Result<List<Print>> {
        return Result.success(prints)
    }

    override suspend fun getPrints(artist: Artist): Result<List<Print>> {
        return Result.success(prints.filter { it.artist == vincent })
    }

    override suspend fun addPrint(print: Print): Boolean {
        prints += print
        return true
    }

    override suspend fun editPrint(name: String, print: Print): Boolean {
        val index = prints.indexOfFirst { it.name == name }
        prints[index].sizes = print.sizes
        prints[index].price = print.price
        return true
    }

    override suspend fun getBundles(): Result<List<Bundle>> {
        return Result.success(bundles)
    }

    override suspend fun editBundle(name: String, bundle: Bundle): Boolean {
        val index = bundles.indexOfFirst { it.name == name }
        bundles[index].prints = bundle.prints
        bundles[index].price = bundle.price
        return true
    }

    override suspend fun addBundle(bundle: Bundle): Boolean {
        bundles += bundle
        return true
    }

    override suspend fun getArtists(): Result<List<Artist>> {
        return Result.success(artists)
    }

    override suspend fun addArtist(artist: Artist): Boolean {
        artists += artist
        return true
    }

    override suspend fun getSale(saleId: UUID): Result<Sale> {
        return Result.success(sales.find { it.saleId == saleId }?: Sale())
    }

    override suspend fun getSales(): Result<List<Sale>> {
        return Result.success(sales)
    }

    override suspend fun addSale(sale: Sale): Boolean {
        sales += sale
        return true
    }
}