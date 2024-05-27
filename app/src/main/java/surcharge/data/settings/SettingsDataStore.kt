package surcharge.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class SettingsDataStore(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val themeKey = intPreferencesKey("theme")
    private val artistKey = stringPreferencesKey("artist")

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
}