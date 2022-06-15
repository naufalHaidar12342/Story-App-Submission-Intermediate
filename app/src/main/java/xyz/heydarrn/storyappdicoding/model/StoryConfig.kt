package xyz.heydarrn.storyappdicoding.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import xyz.heydarrn.storyappdicoding.model.api.ApiService
import xyz.heydarrn.storyappdicoding.model.api.response.GetStoriesResponse
import xyz.heydarrn.storyappdicoding.model.api.response.UploadStoryResponse

class StoryConfig(private val apiService: ApiService) {
    fun getStories(personalToken: String): LiveData<ApiResponseConfig<GetStoriesResponse>> = liveData {
        emit(ApiResponseConfig.ResultIsLoading)

        try {
            val client=apiService.getAllStories(token = "Bearer $personalToken")
            emit(ApiResponseConfig.ResponseSuccess(client))

        }catch (exceptionStory:Exception){
            Log.d("STORYCONFIG", "getStories: ${exceptionStory.message}")
            emit(ApiResponseConfig.ResponseFail(exceptionStory.message.toString()))
        }
    }

    fun uploadNewStory(
        personalToken: String,
        image: MultipartBody.Part,
        imageDescription: RequestBody
    ) :LiveData<ApiResponseConfig<UploadStoryResponse>> = liveData {
        emit(ApiResponseConfig.ResultIsLoading)
        try {
            val client=apiService.uploadStory(token = "Bearer $personalToken", file = image, description = imageDescription)
            emit(ApiResponseConfig.ResponseSuccess(client))

        }catch (exceptionUpload:Exception){
            exceptionUpload.printStackTrace()
            Log.d("STORYCONFIG", "uploadNewStory: ${exceptionUpload.message} ")
            emit(ApiResponseConfig.ResponseFail(exceptionUpload.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var storyConfigInstance : StoryConfig? = null
        fun getStoryInstance(apiService: ApiService): StoryConfig =
            storyConfigInstance ?: synchronized(this) {
                storyConfigInstance ?: StoryConfig(apiService)
            }.also {
                storyConfigInstance=it
            }
    }
}