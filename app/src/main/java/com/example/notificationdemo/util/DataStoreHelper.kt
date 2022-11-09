package com.example.notificationdemo.util

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore


const val USER_PREFERENCES_NAME = "data_store_preferences"

//extension for data store
val Context.myDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

object PreferenceKeys {

    val NOTIFICATION_TONE_URI_KEY = stringPreferencesKey("NOTIFICATION_TONE_URI_KEY")

}