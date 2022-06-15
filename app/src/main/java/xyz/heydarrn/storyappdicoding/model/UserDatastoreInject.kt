package xyz.heydarrn.storyappdicoding.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import xyz.heydarrn.storyappdicoding.model.api.ApiConfig

val Context.dataStore:DataStore<Preferences> by preferencesDataStore("setting_datastore")
object UserDatastoreInject {
    fun buildUserConfig(context: Context) : UserConfig {
        return UserConfig.getUserConfigInstance(context.dataStore,ApiConfig.getApiService())
    }
}