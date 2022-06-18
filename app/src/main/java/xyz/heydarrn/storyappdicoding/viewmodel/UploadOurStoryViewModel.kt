package xyz.heydarrn.storyappdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import xyz.heydarrn.storyappdicoding.model.ApiResponseConfig
import xyz.heydarrn.storyappdicoding.model.StoryConfig
import xyz.heydarrn.storyappdicoding.model.UserConfig
import xyz.heydarrn.storyappdicoding.model.api.response.UploadStoryResponse

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

    fun uploadStoryAndLocation(
        token: String,
        image: MultipartBody.Part,
        description: RequestBody,
        latitude: RequestBody?,
        longitude: RequestBody?
    ): LiveData<ApiResponseConfig<UploadStoryResponse>> {
        return storyConfig.uploadNewStoryFromPaging(token, image, description, latitude, longitude)
    }
}