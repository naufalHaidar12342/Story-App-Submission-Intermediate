package xyz.heydarrn.storyappdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import xyz.heydarrn.storyappdicoding.model.StoryConfig
import xyz.heydarrn.storyappdicoding.model.UserConfig

class UploadOurStoryViewModel(
    private val userConfig: UserConfig,
    private val storyConfig: StoryConfig
) : ViewModel() {
    fun getTokenForUploadStory() : LiveData<String> = userConfig.getUserToken().asLiveData()

    fun uploadStory(
        token:String,
        image:MultipartBody.Part,
        imageDescription:RequestBody
    ) = storyConfig.uploadNewStory(token,image,imageDescription)
}