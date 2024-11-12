package com.aramonp.workly

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

//TODO: Posiblemente se borre
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    companion object {
        val USER_UID_KEY = stringPreferencesKey("user_id")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    // Método para guardar el usuario y el estado de autenticación
    suspend fun saveUser(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_UID_KEY] = userId
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    // Método para eliminar la información de usuario en caso de logout
    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences[USER_UID_KEY] = ""
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }

}