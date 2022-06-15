package xyz.heydarrn.storyappdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xyz.heydarrn.storyappdicoding.model.UserConfig

class LoginPageViewModel(private val userConfig: UserConfig) : ViewModel() {

    fun setToken(userToken:String,isUserLoggedIn:Boolean){
        viewModelScope.launch {
            userConfig.setTokenForStoring(userToken,isUserLoggedIn)
        }
    }

    fun grabToken():LiveData<String> = userConfig.getUserToken().asLiveData()

    fun loggingIn(
        email:String,
        pass:String
    ) = userConfig.loggingInUser(email,pass)
}