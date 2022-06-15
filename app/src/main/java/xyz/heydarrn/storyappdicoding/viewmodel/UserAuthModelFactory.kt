package xyz.heydarrn.storyappdicoding.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xyz.heydarrn.storyappdicoding.model.UserConfig
import xyz.heydarrn.storyappdicoding.model.UserDatastoreInject

class UserAuthModelFactory(
    private val userConfig: UserConfig) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterPageViewModel::class.java) -> {
                RegisterPageViewModel(userConfig) as T
            }

            modelClass.isAssignableFrom(LoginPageViewModel::class.java) -> {
                LoginPageViewModel(userConfig) as T
            }
            else ->{
                throw IllegalArgumentException("ViewModel is not known. Troubled viewmodel : ${modelClass.name}")
            }
        }
    }

    companion object {
        @Volatile
        private var userAuthInstance:UserAuthModelFactory? = null

        fun getUserAuthInstance(context: Context) = userAuthInstance ?: synchronized(this) {
            userAuthInstance ?: UserAuthModelFactory(UserDatastoreInject.buildUserConfig(context))
        }.also {
            userAuthInstance=it
        }
    }
}