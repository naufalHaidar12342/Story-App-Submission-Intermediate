package xyz.heydarrn.storyappdicoding.model.api.response

import com.google.gson.annotations.SerializedName

data class StoryRegisterResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
