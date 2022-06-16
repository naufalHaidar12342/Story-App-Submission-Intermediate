package xyz.heydarrn.storyappdicoding.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import xyz.heydarrn.storyappdicoding.model.StoryConfig
import xyz.heydarrn.storyappdicoding.model.StoryDatastoreInject
import xyz.heydarrn.storyappdicoding.model.UserConfig
import xyz.heydarrn.storyappdicoding.model.UserDatastoreInject

@Suppress("UNCHECKED_CAST")
class StoriesDicodingModelFactory private constructor(
    private val userConfig: UserConfig,
    private val storyConfig: StoryConfig
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StoriesDicodingViewModel::class.java) -> {
                StoriesDicodingViewModel(userConfig, storyConfig) as T
            }

            modelClass.isAssignableFrom(UploadOurStoryViewModel::class.java) -> {
                UploadOurStoryViewModel(userConfig, storyConfig) as T
            }

            else -> {
                throw IllegalArgumentException("ViewModel is not found. Troubled viewmodel : ${modelClass.name} ")

            }
        }
    }

    companion object {
        @Volatile
        private var storyModelFactoryInstance : StoriesDicodingModelFactory ? = null

        fun getStoryModelFactoryInstance(
            context: Context
        ) : StoriesDicodingModelFactory = storyModelFactoryInstance ?: synchronized(this) {
            storyModelFactoryInstance ?: StoriesDicodingModelFactory(
                UserDatastoreInject.buildUserConfig(context),
                StoryDatastoreInject.buildStoryConfig()
            )
        }.also {
            storyModelFactoryInstance=it
        }
    }
}