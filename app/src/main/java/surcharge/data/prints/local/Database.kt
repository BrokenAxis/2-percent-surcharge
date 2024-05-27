package surcharge.data.prints.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Print
import surcharge.types.Sale
import java.util.UUID


@Dao
interface BaseDao<T> {
    @Insert
    fun insert(entity: T): Long

    @Insert
    fun insert(entities: List<T>)

    @Update
    fun update(entity: T): Int

    @Delete
    fun delete(entity: T): Int
}

@Dao
interface PrintDao : BaseDao<Print> {
    @Query("SELECT * FROM print")
    fun getAll(): List<Print>

    @Query("SELECT * FROM print WHERE name = :name LIMIT 1")
    fun getByName(name: String): Print

    @Query("SELECT * FROM print WHERE artist = :artist")
    fun getByArtist(artist: String): List<Print>
}

@Dao
interface BundleDao : BaseDao<Bundle> {
    @Query("SELECT * FROM bundle")
    fun getAll(): List<Bundle>

    @Query("SELECT * FROM bundle WHERE name = :name LIMIT 1")
    fun getByName(name: String): Bundle
}

@Dao
interface ArtistDao : BaseDao<Artist> {
    @Query("SELECT * FROM artist")
    fun getAll(): List<Artist>
}

@Dao
interface SaleDao : BaseDao<Sale> {
    @Query("SELECT * FROM sale")
    fun getAll(): List<Sale>

    @Query("SELECT * FROM sale WHERE saleId = :saleId LIMIT 1")
    fun getById(saleId: UUID): Sale
}

@Database(entities = [Print::class, Bundle::class, Artist::class, Sale::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun printDao(): PrintDao
    abstract fun bundleDao(): BundleDao
    abstract fun artistDao(): ArtistDao
    abstract fun saleDao(): SaleDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "local_data")
                .fallbackToDestructiveMigration().build()
        }
    }
}