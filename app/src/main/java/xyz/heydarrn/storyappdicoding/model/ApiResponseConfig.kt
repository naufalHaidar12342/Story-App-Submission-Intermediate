package xyz.heydarrn.storyappdicoding.model

sealed class ApiResponseConfig <out A> private constructor() {
    data class ResponseSuccess<out T>(val data: T) :ApiResponseConfig<T>()
    data class ResponseFail(val errorMessage: String) : ApiResponseConfig<Nothing>()
    object ResultIsLoading : ApiResponseConfig<Nothing>()
}
