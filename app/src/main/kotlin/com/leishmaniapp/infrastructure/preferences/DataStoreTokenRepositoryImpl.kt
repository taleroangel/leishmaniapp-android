package com.leishmaniapp.infrastructure.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.leishmaniapp.domain.repository.ITokenRepository
import com.leishmaniapp.domain.types.AccessToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Create a [DataStore] to persist authentication
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DataStoreTokenRepositoryImpl.TAG)

/**
 * [DataStore] implementation for the [ITokenRepository]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreTokenRepositoryImpl @Inject constructor(

    /**
     * Android context to access [Context.dataStore]
     */
    @ApplicationContext context: Context,

    ) : ITokenRepository {

    companion object {
        /**
         * TAG for using with [DataStore]
         */
        const val TAG: String = "TokenDataStore"
    }

    /**
     * Definition for the [DataStore] keys
     */
    private object PreferencesKeys {

        /**
         * Key for accessing a [AccessToken] stored in [DataStore]
         */
        val accessTokenKey = stringPreferencesKey("$TAG:accessToken")
    }

    /**
     * Grab a reference to the context's [DataStore]
     */
    private val dataStore = context.dataStore

    override val accessToken: Flow<AccessToken?>
        get() = dataStore.data.mapLatest { preferences ->
            preferences[PreferencesKeys.accessTokenKey]
        }

    override val isTokenStored: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences.contains(PreferencesKeys.accessTokenKey)
        }

    override suspend fun storeAuthenticationToken(token: AccessToken): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                dataStore.edit { preferences ->
                    preferences[PreferencesKeys.accessTokenKey] = token
                }
                Log.d(TAG, "Stored new authentication token")
                return@withContext Result.success(Unit)
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to store an authentication token", e)
                return@withContext Result.failure(e)
            }
        }

    override suspend fun forgetAuthenticationToken(): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                dataStore.edit { preferences ->
                    preferences.remove(PreferencesKeys.accessTokenKey)
                }
                Log.d(TAG, "Deleted authentication token")
                return@withContext Result.success(Unit)
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to delete current authentication token", e)
                return@withContext Result.failure(e)
            }
        }
}