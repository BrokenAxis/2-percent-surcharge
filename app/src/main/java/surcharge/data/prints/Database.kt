package surcharge.data.prints

import android.content.Context
import surcharge.data.prints.local.AppDatabase
import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale
import java.util.UUID

class LocalData(appContext: Context): Data {

    private val db = AppDatabase.getInstance(appContext)

    override suspend fun getPrint(name: String): Result<Print> = runCatching {
        db.printDao().getByName(name)
    }

    override suspend fun getPrints(): Result<List<Print>> = runCatching {
        db.printDao().getAll()
    }

    override suspend fun getPrints(artist: Artist): Result<List<Print>> = runCatching {
        db.printDao().getByArtist(artist.name)
    }

    override suspend fun addPrint(print: Print) {
        db.printDao().insert(print)
    }

    override suspend fun editPrint(print: Print): Boolean {
        return db.printDao().update(print) == 1
    }

    override suspend fun getBundles(): Result<List<Bundle>> = runCatching {
        db.bundleDao().getAll()
    }

    override suspend fun editBundle(bundle: Bundle): Boolean {
        return db.bundleDao().update(bundle) == 1
    }

    override suspend fun addBundle(bundle: Bundle) {
        db.bundleDao().insert(bundle)
    }

    override suspend fun getArtists(): Result<List<Artist>> = runCatching {
        db.artistDao().getAll()
    }

    override suspend fun addArtist(artist: Artist) {
        db.artistDao().insert(artist)
    }

    override suspend fun getSale(saleId: UUID): Result<Sale> = runCatching {
        db.saleDao().getById(saleId)
    }

    override suspend fun getSales(): Result<List<Sale>> = runCatching {
        db.saleDao().getAll()
    }

    override suspend fun addSale(sale: Sale) {
        db.saleDao().insert(sale)
    }

    override suspend fun reset() {
        db.clearAllTables()
    }

    override suspend fun reload() {
        reset()

        val data = TestData()
        db.printDao().insert(data.prints)
        db.bundleDao().insert(data.bundles)
        db.artistDao().insert(data.artists)
        db.saleDao().insert(data.sales)
    }
}