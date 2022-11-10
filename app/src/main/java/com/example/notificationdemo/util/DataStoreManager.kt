package com.example.notificationdemo.util

import android.content.Context
import android.media.RingtoneManager
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager(val context: Context) {

    fun readStringFromDataStore(key: Preferences.Key<String>): Flow<String> {
        return context.myDataStore.data
            .catch { ex ->
                if (ex is IOException) {
                    emit(emptyPreferences())
                } else throw ex
            }
            .map { preferences ->
                val showCompleted = preferences[key] ?: "null"
                showCompleted
            }
    }

    fun readBooleanFromDataStore(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return context.myDataStore.data
            .catch { ex ->
                if (ex is IOException) {
                    emit(emptyPreferences())
                } else throw ex
            }
            .map { preferences ->
                val isChecked = preferences[key] ?: false
                isChecked
            }
    }

    fun readIntegerFromDataStore(key: Preferences.Key<Int>): Flow<Int> {
        return context.myDataStore.data
            .catch { ex ->
                if (ex is IOException) {
                    emit(emptyPreferences())
                } else throw ex
            }
            .map { preferences ->
                val isChecked = preferences[key] ?: -1
                isChecked
            }
    }

    fun readLongFromDataStore(key: Preferences.Key<Long>): Flow<Long> {
        return context.myDataStore.data
            .catch { ex ->
                if (ex is IOException) {
                    emit(emptyPreferences())
                } else throw ex
            }
            .map { preferences ->
                val isChecked = preferences[key] ?: 0L
                isChecked
            }
    }

    fun readFloatFromDataStore(key: Preferences.Key<Float>): Flow<Float> {
        return context.myDataStore.data
            .catch { ex ->
                if (ex is IOException) {
                    emit(emptyPreferences())
                } else throw ex
            }
            .map { preferences ->
                val isChecked = preferences[key] ?: 0f
                isChecked
            }
    }

    suspend fun saveStringToDataStore(key: Preferences.Key<String>, name: String) {
        context.myDataStore.edit { preferences ->
            preferences[key] = name
        }
    }


    suspend fun saveToneUriStringToDataStore(key: Preferences.Key<String>, name: String =  RingtoneManager.getDefaultUri(
        RingtoneManager.TYPE_NOTIFICATION
    ).toString()) {
        context.myDataStore.edit { preferences ->
            preferences[key] = name
        }
    }

    suspend fun saveBooleanToDataStore(key: Preferences.Key<Boolean>, isChecked: Boolean = false) {
        context.myDataStore.edit { preferences ->
            preferences[key] = isChecked
        }
    }

    suspend fun saveIntToDataStore(key: Preferences.Key<Int>, int: Int) {
        context.myDataStore.edit { preferences ->
            preferences[key] = int
        }
    }

    suspend fun saveLongToDataStore(key: Preferences.Key<Long>, long: Long = 0L) {
        context.myDataStore.edit { preferences ->
            preferences[key] = long
        }
    }

    suspend fun saveFloatToDataStore(key: Preferences.Key<Float>, float: Float) {
        context.myDataStore.edit { preferences ->
            preferences[key] = float
        }
    }

    fun isKeyStored(key: Preferences.Key<String>): Flow<Boolean>  =
        context.myDataStore.data.map {
                preference -> preference.contains(key)
        }


}