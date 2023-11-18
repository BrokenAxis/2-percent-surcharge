package surcharge.data.prints

import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print

class PrintsImpl: Prints {

    override suspend fun getPrint(name: String?): Result<Print> {
        return Result.success(prints.find { it.name == name }?: asa)
    }
    override suspend fun getPrints(): Result<List<Print>> {
        return Result.success(prints)
    }
    override suspend fun getPrints(artist: Artist): Result<List<Print>> {
        return Result.success(prints.filter { it.artist == vincent })
    }

    override suspend fun getBundles(): Result<List<Bundle>> {
        return Result.success(bundles)
    }
}