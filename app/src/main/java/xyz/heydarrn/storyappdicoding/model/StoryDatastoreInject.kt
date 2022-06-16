package xyz.heydarrn.storyappdicoding.model

import xyz.heydarrn.storyappdicoding.model.api.ApiConfig

object StoryDatastoreInject {
    fun buildStoryConfig() : StoryConfig = StoryConfig.getStoryInstance(ApiConfig.getApiService())
}