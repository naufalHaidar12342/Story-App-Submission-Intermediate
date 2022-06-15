package xyz.heydarrn.storyappdicoding.model

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xyz.heydarrn.storyappdicoding.model.api.ApiService
import xyz.heydarrn.storyappdicoding.model.api.response.StoryLoginResponse
import xyz.heydarrn.storyappdicoding.model.api.response.StoryRegisterResponse

class UserConfig private constructor(
    private val dataStoreUser: DataStore<Preferences>,
    private val apiService: ApiService
){
    fun registerUser(
        name: String,
        email: String,
        password:String
    ): LiveData<ApiResponseConfig<StoryRegisterResponse>> = liveData {
        emit(ApiResponseConfig.ResultIsLoading)

        try {
            val registerClient=apiService.registerNewUser(newName = name, newEmail = email, newPassword = password)
            emit(ApiResponseConfig.ResponseSuccess(registerClient))
            
        }catch (registerException:Exception) {
            Log.d("USERCONFIG", "registerUser: ${registerException.message}")
            registerException.printStackTrace()
            emit(ApiResponseConfig.ResponseFail(registerException.message.toString()))
        }
    }

    fun loggingInUser(
        email: String,
        password: String
    ): LiveData<ApiResponseConfig<StoryLoginResponse>> = liveData {
        emit(ApiResponseConfig.ResultIsLoading)

        try {
            val loginClient=apiService.loginExistingUser(registeredEmail = email, registeredPassword = password)
            emit(ApiResponseConfig.ResponseSuccess(loginClient))

        }catch (loginException:Exception) {
            Log.d("USERCONFIG", "loggingInUser: ${loginException.message}")
            loginException.printStackTrace()
            emit(ApiResponseConfig.ResponseFail(loginException.message.toString()))
        }
    }

    fun getUserToken(): Flow<String> {
        return dataStoreUser.data.map { tokenPref ->
            tokenPref[USER_TOKEN] ?: ""
        }
    }

    fun doesUserLoggedIn(): Flow<Boolean> {
        return dataStoreUser.data.map { userLoginCondition ->
            userLoginCondition[STATE_OF_USER] ?: false
        }
    }

    suspend fun setTokenForStoring(token:String, isLogin:Boolean) {
        dataStoreUser.edit { setToken ->
            setToken[USER_TOKEN] = token
            setToken[STATE_OF_USER] = isLogin
        }
    }

    suspend fun loggingOutUser(){
        dataStoreUser.edit { logOut ->
            logOut[USER_TOKEN] = ""
            logOut[STATE_OF_USER] = false
        }
    }

    companion object {
        @Volatile
        private var userConfigInstance:UserConfig?=null
        private val USER_TOKEN= stringPreferencesKey("personal_token")
        private val STATE_OF_USER= booleanPreferencesKey("state_key")

        fun getUserConfigInstance(dataStore: DataStore<Preferences>, apiService: ApiService) :UserConfig {
            return userConfigInstance ?: synchronized(this) {
                val instance=UserConfig(dataStore,apiService)
                userConfigInstance=instance
                instance
            }
        }
    }
}