package xyz.heydarrn.storyappdicoding.viewmodel

import androidx.lifecycle.ViewModel
import xyz.heydarrn.storyappdicoding.model.UserConfig

class RegisterPageViewModel(private val userConfig: UserConfig) : ViewModel() {
    fun registerThisUser(
        name:String,
        email:String,
        pass:String
    ) = userConfig.registerUser(name,email,pass)
}