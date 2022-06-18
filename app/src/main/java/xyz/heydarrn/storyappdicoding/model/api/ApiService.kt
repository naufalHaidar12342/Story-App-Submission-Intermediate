package xyz.heydarrn.storyappdicoding.model.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import xyz.heydarrn.storyappdicoding.model.api.response.GetStoriesResponse
import xyz.heydarrn.storyappdicoding.model.api.response.StoryLoginResponse
import xyz.heydarrn.storyappdicoding.model.api.response.StoryRegisterResponse
import xyz.heydarrn.storyappdicoding.model.api.response.UploadStoryResponse

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun registerNewUser(
        @Field("name") newName: String,
        @Field("email") newEmail: String,
        @Field("password") newPassword: String
    ): StoryRegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginExistingUser(
        @Field("email") registeredEmail: String,
        @Field("password") registeredPassword: String
    ): StoryLoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
    ): GetStoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): UploadStoryResponse

    @GET("stories")
    suspend fun getStoriesForPaging(
        @Header("Authorization") token: String,
        @Query("size") sizeStory : Int,
        @Query("page") pageStory : Int,
    ) : GetStoriesResponse

    @GET("stories")
    fun getStoriesForMaps(
        @Header("Authorization") token: String,
        @Query("location") locationEnabled : Int,
    ) : Call<GetStoriesResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStoryWithLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") latitudeUpload: RequestBody? = null,
        @Part("lon") longitudeUpload: RequestBody? = null
    ) : UploadStoryResponse
}