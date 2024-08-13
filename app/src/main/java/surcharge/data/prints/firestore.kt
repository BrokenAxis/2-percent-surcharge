package surcharge.data.prints

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import surcharge.data.settings.SettingsDataStore
import surcharge.types.Artist
import surcharge.types.Bundle
import surcharge.types.Group
import surcharge.types.Print
import surcharge.types.Sale
import surcharge.types.User
import java.time.Instant

class Firestore(settingsDataStore: SettingsDataStore) : Data {
    private val db = Firebase.firestore
    private val settings = settingsDataStore
    private lateinit var groupID: String
    private lateinit var groupDB: DocumentReference

    init {
        db.persistentCacheIndexManager?.enableIndexAutoCreation()

        runBlocking(IO) {
            val groups = getGroups()
            if (groups.isEmpty()) {
                val uID = FirebaseAuth.getInstance().currentUser?.uid.toString()

                val group = db.collection("groups")
                    .add(
                        mapOf(
                            "owner" to uID,
                            "users" to listOf(uID),
                            "name" to "Your Group"
                        )
                    ).await()
                groupID = group.id
            } else {
                val selected = settings.readGroup()
                if (groups.any { it.groupId == selected }) {
                    groupID = selected
                } else {
                    settings.updateGroup(groups[0].groupId)
                    groupID = groups[0].groupId
                }
            }
        }
        groupDB = db.collection("groups").document(groupID)
    }

    override suspend fun getPrint(name: String): Result<Print> {
        val docRef = groupDB.collection("prints").document(name)
        return try {
            val print = docRef.get().await()
            Result.success(print.toObject(Print::class.java)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPrints(): Result<List<Print>> {
        val docRef = groupDB.collection("prints")

        return try {
            val prints = docRef.get().await()
            val list = prints.toObjects(Print::class.java)
            list.sortBy { it.property }
            list.sortBy { it.artist }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPrints(artist: Artist): Result<List<Print>> {
        val docRef = groupDB.collection("prints").whereEqualTo("artist", artist.name)

        return try {
            val prints = docRef.get().await()
            val list = prints.toObjects(Print::class.java)
            list.sortBy { it.property }
            list.sortBy { it.artist }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedPrints(): Result<List<Print>> {
        val docRef = groupDB.collection("prints")

        return try {
            val prints = docRef.get(Source.CACHE).await()
            val list = prints.toObjects(Print::class.java)
            list.sortBy { it.property }
            list.sortBy { it.artist }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPrint(print: Print) {
        val docRef = groupDB.collection("prints").document(print.name)
        docRef.set(print)
    }

    override suspend fun editPrint(print: Print): Boolean {
        val docRef = groupDB.collection("prints").document(print.name)
        return try {
            docRef.set(print).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deletePrint(print: Print): Boolean {
        val docRef = groupDB.collection("prints").document(print.name)
        return try {
            docRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getBundle(name: String): Result<Bundle> {
        val docRef = groupDB.collection("bundles").document(name)
        return try {
            val bundle = docRef.get().await()
            Result.success(bundle.toObject(Bundle::class.java)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBundles(): Result<List<Bundle>> {
        val docRef = groupDB.collection("bundles")
        return try {
            val bundles = docRef.get().await()
            Result.success(bundles.toObjects(Bundle::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun editBundle(bundle: Bundle): Boolean {
        val docRef = groupDB.collection("bundles").document(bundle.name)
        return try {
            docRef.set(bundle).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteBundle(bundle: Bundle): Boolean {
        val docRef = groupDB.collection("bundles").document(bundle.name)
        return try {
            docRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun addBundle(bundle: Bundle) {
        val docRef = groupDB.collection("bundles").document(bundle.name)
        docRef.set(bundle)
    }

    override suspend fun getArtists(): Result<List<Artist>> {
        val docRef = groupDB.collection("artists")
        return try {
            val artists = docRef.get().await()
            Result.success(artists.toObjects(Artist::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addArtist(artist: Artist) {
        val docRef = groupDB.collection("artists").document(artist.name)
        docRef.set(artist)
    }

    override suspend fun deleteArtist(artist: Artist): Boolean {
        val docRef = groupDB.collection("artists").document(artist.name)
        return try {
            docRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getSale(saleId: String): Result<Sale> {
        val docRef = groupDB.collection("sales").document(saleId)
        return try {
            val sale = docRef.get().await()
            Result.success(sale.toObject(Sale::class.java)!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSales(): Result<List<Sale>> {
        val docRef = groupDB.collection("sales")
        return try {
            val sales = docRef.get().await()
            val list = sales.toObjects(Sale::class.java)
            list.sortByDescending { Instant.parse(it.time) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedSales(): Result<List<Sale>> {
        val docRef = groupDB.collection("sales")
        return try {
            val sales = docRef.get(Source.CACHE).await()
            val list = sales.toObjects(Sale::class.java)
            list.sortByDescending { Instant.parse(it.time) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecentSales(limit: Int): Result<List<Sale>> {
        val docRef = groupDB.collection("sales")
        return try {
            val sales = docRef.get().await()
            val list = sales.toObjects(Sale::class.java)
            list.sortByDescending { Instant.parse(it.time) }
            Result.success(list.take(limit))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addSale(sale: Sale) {
        val docRef = groupDB.collection("sales").document(sale.saleId)
        docRef.set(sale)
    }

    override suspend fun deleteSale(sale: Sale): Boolean {
        val docRef = groupDB.collection("sales").document(sale.saleId)
        return try {
            docRef.delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun reset() {

    }

    override suspend fun reload() {
        val data = TestData()
        data.prints.forEach { addPrint(it) }
        data.bundles.forEach { addBundle(it) }
        data.artists.forEach { addArtist(it) }
        data.sales.forEach { addSale(it) }
    }

    suspend fun getGroupName(): String {
        return (groupDB.get().await().data?.get("name") ?: "Unknown Group").toString()
    }

    suspend fun changeGroup(id: String) {
        groupID = id
        groupDB = db.collection("groups").document(groupID)
        settings.updateGroup(id)
    }

    suspend fun inviteToGroup(email: String): Boolean {
        val user = db.collection("users").whereEqualTo("email", email).limit(1).get().await()

        if (user.size() != 1) return false

        groupDB.update(
            "users",
            FieldValue.arrayUnion(user.documents.first().id)
        ).await()
        return true
    }

    suspend fun getGroupUserIds(): List<String> {
        val users = groupDB.get().await().data?.get("users")
        return if (users is List<*>) {
            users.map { it.toString() }
        } else {
            listOf()
        }
    }

    suspend fun getGroupUsers(): List<User> {
        val ids = getGroupUserIds()
        return ids.map { user ->
            val u = db.collection("users").document(user).get().await()
            User(
                name = u.data?.get("name").toString(),
                email = u.data?.get("email").toString()
            )
        }
    }

    suspend fun getGroups(): List<Group> {
        return db.collection("groups")
            .whereArrayContains("users", FirebaseAuth.getInstance().currentUser?.uid.toString())
            .get().await().map {
                Group(
                    groupId = it.id,
                    name = (it.data["name"] ?: "Unknown Group Name").toString(),
                    owner = it.data["owner"].toString()
                )
            }
    }

    suspend fun updateGroup(name: String) {
        groupDB.update("name", name).await()
    }

    /**
     * Adds user to database. Does nothing if user is already in database
     */
    suspend fun addUser() {
        val user = FirebaseAuth.getInstance().currentUser
        db.collection("users").document(user?.uid ?: "Unknown User").set(
            mapOf(
                "name" to user?.displayName,
                "email" to user?.email,
            )
        )
    }

    suspend fun isOwner(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        val owner = groupDB.get().await().data?.get("owner")
        return owner == user?.uid
    }

    suspend fun getCashOnHand(): Int {
        val docRef = groupDB
        val cash = docRef.get().await()
        return ((cash.data?.get("cash") ?: 0) as Long).toInt()
    }

    suspend fun updateCashOnHand(cash: Int) {
        val docRef = groupDB
        docRef.update("cash", cash).await()
    }

    suspend fun addCashOnHand(cash: Int) {
        val docRef = groupDB
        docRef.update("cash", FieldValue.increment(cash.toLong())).await()
    }
}