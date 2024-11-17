package com.aramonp.workly.domain.repository

interface DataStoreRepository {
    suspend fun savePreference(key: String, value: Boolean)
    suspend fun getPreference(key: String): Boolean?
}