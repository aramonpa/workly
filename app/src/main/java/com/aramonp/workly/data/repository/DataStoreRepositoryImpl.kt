package com.aramonp.workly.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.aramonp.workly.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {
    override suspend fun savePreference(key: String, value: Boolean) {
        val preferencesKey = booleanPreferencesKey(key)
        dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getPreference(key: String): Boolean? {
        val preferencesKey = booleanPreferencesKey(key)
        val preferences = dataStore.data.firstOrNull() ?: return null
        return preferences[preferencesKey]
    }
}