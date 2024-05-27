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
import surcharge.data.SQUARE_ID
import surcharge.types.Size
import surcharge.types.emptyPriceMap

class SettingsDataStore(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val themeKey = intPreferencesKey("theme")
    private val artistKey = stringPreferencesKey("artist")
    private val squareKey = stringPreferencesKey("square")
    private val cashKey = intPreferencesKey("cash")
    private val discountKey = booleanPreferencesKey("discount")
    private val defaultPricesKey = stringPreferencesKey("defaultPrices")

    suspend fun readTheme(): Int {
        return context.dataStore.data.first()[themeKey] ?: 0
    }

    suspend fun updateTheme(theme: Int) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme
        }
    }

    suspend fun readArtist(): String {
        return context.dataStore.data.first()[artistKey] ?: ""
    }

    suspend fun updateArtist(artist: String) {
        context.dataStore.edit { preferences ->
            preferences[artistKey] = artist
        }
    }

    suspend fun readSquareID(): String {
        return context.dataStore.data.first()[squareKey] ?: SQUARE_ID
    }

    suspend fun updateSquareID(id: String) {
        context.dataStore.edit { preferences ->
            preferences[squareKey] = id
        }
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
}