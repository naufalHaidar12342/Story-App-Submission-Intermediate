package xyz.heydarrn.storyappdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import xyz.heydarrn.storyappdicoding.model.StoryConfig
import xyz.heydarrn.storyappdicoding.model.UserConfig
import xyz.heydarrn.storyappdicoding.model.api.response.ListStoryItem

class StoriesDicodingViewModel (
    private val userConfig: UserConfig,
    private val storyConfig: StoryConfig
    ) : ViewModel() {

    fun getTokenForStory(): LiveData<String> = userConfig.getUserToken().asLiveData()

    fun isUserLoggedInStory() : LiveData<Boolean> = userConfig.doesUserLoggedIn().asLiveData()

    fun grabStories(token:String) = storyConfig.getStories(token)

    fun logoutFromStoryPage() {
        viewModelScope.launch {
            userConfig.loggingOutUser()
        }
    }

    fun grabStoriesWithLocation(token: String):LiveData<PagingData<ListStoryItem>> {
        return storyConfig.getStoriesForPaging(token).cachedIn(viewModelScope)
    }

}