package com.example.poketgc_api.Data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Gestiona la persistencia de configuraciones de usuario utilizando DataStore.
 * Actualmente se encarga de guardar y recuperar la preferencia del modo oscuro.
 */
class ManagerAjustes(private val context: Context) {
    private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

    /**
     * Un Flow que emite el estado actual del modo oscuro.
     * Si no hay valor guardado, por defecto es `false`.
     */
    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false
    }

    /**
     * Guarda la preferencia del modo oscuro en el DataStore.
     * @param enabled `true` para activar el modo oscuro, `false` para desactivarlo.
     */
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = enabled
        }
    }
}
