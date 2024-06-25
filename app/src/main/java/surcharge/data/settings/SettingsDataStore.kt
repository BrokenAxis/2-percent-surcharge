package surcharge.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import surcharge.types.Size
import surcharge.types.emptyPriceMap
import surcharge.utils.retrofit.Location
import surcharge.utils.retrofit.Token
import surcharge.utils.retrofit.generateCsrfToken
import java.util.UUID

class SettingsDataStore(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val uniqueIdKey = stringPreferencesKey("UUID")
    private val themeKey = intPreferencesKey("theme")
    private val squareAccessTokenKey = stringPreferencesKey("squareAccessToken")
    private val artistKey = stringPreferencesKey("artist")
    private val cashKey = intPreferencesKey("cash")
    private val discountKey = booleanPreferencesKey("discount")
    private val defaultPricesKey = stringPreferencesKey("defaultPrices")
    private val intentKey = stringPreferencesKey("intent")
    private val csrfKey = stringPreferencesKey("csrf")
    private val locationKey = stringPreferencesKey("location")
    private val alternateSaleKey = booleanPreferencesKey("alternateSale")

    suspend fun readUniqueId(): String {
        if (context.dataStore.data.first()[uniqueIdKey] == null) {
            context.dataStore.edit { preferences ->
                preferences[uniqueIdKey] = UUID.randomUUID().toString()
            }
        }
        return context.dataStore.data.first()[uniqueIdKey]!!
    }

    suspend fun readTheme(): Int {
        return context.dataStore.data.first()[themeKey] ?: 0
    }

    suspend fun updateTheme(theme: Int) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme
        }
    }

    suspend fun readArtist(): String {
        return context.dataStore.data.first()[artistKey] ?: "User"
    }

    suspend fun updateArtist(artist: String) {
        context.dataStore.edit { preferences ->
            preferences[artistKey] = artist
        }
    }

    suspend fun readSquareAccessToken(): Token {
        val type = object : TypeToken<Token>() {}.type
        val squareAccessToken =
            context.dataStore.data.first()[squareAccessTokenKey] ?: return Token()
        return Gson().fromJson(squareAccessToken, type)
    }

    suspend fun updateSquareAccessToken(accessToken: Token): Boolean {
        context.dataStore.edit { preferences ->
            preferences[squareAccessTokenKey] = Gson().toJson(accessToken)
        }
        return true
    }

    suspend fun readCash(): Int {
        return context.dataStore.data.first()[cashKey] ?: 0
    }

    suspend fun updateCash(cash: Int) {
        context.dataStore.edit { preferences ->
            preferences[cashKey] = cash
        }
    }

    suspend fun readDiscount(): Boolean {
        return context.dataStore.data.first()[discountKey] ?: false
    }

    suspend fun updateDiscount(discount: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[discountKey] = discount
        }
    }

    suspend fun readDefaultPrices(): Map<Size, Int> {
        val type = object : TypeToken<Map<Size, Int>>() {}.type
        val defaultPrices = context.dataStore.data.first()[defaultPricesKey] ?: return emptyPriceMap
        return Gson().fromJson(defaultPrices, type)
    }

    suspend fun updateDefaultPrices(prices: Map<Size, Int>): Boolean {
        if (prices.keys.containsAll(Size.entries)) {
            context.dataStore.edit { preferences ->
                preferences[defaultPricesKey] = Gson().toJson(prices)
            }
            return true
        }
        return false
    }

    suspend fun updateIntent(intent: String) {
        context.dataStore.edit { preferences ->
            preferences[intentKey] = intent
        }
    }

    suspend fun readIntent(): String {
        return context.dataStore.data.first()[intentKey] ?: ""
    }

    suspend fun refreshCsrf() {
        context.dataStore.edit { preferences ->
            preferences[csrfKey] = generateCsrfToken()
        }
    }

    suspend fun readCsrf(): String {
        return context.dataStore.data.first()[csrfKey] ?: ""
    }

    suspend fun readLocation(): Location {
        val type = object : TypeToken<Location>() {}.type
        val location = context.dataStore.data.first()[locationKey]
            ?: return Location(locationName = "Location")
        return Gson().fromJson(location, type)
    }

    suspend fun updateLocation(location: Location) {
        context.dataStore.edit { preferences ->
            preferences[locationKey] = Gson().toJson(location)
        }
    }

    suspend fun readAlternateSale(): Boolean {
        return context.dataStore.data.first()[alternateSaleKey] ?: false
    }

    suspend fun updateAlternateSale(alternateSale: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[alternateSaleKey] = alternateSale
        }
    }
}