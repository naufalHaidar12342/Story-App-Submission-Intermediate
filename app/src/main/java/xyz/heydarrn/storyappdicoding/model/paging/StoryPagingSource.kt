package xyz.heydarrn.storyappdicoding.model.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import xyz.heydarrn.storyappdicoding.model.api.ApiService
import xyz.heydarrn.storyappdicoding.model.api.response.ListStoryItem

class StoryPagingSource(private val apiService: ApiService, private val token:String) : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INDEX_OF_FIRST_PAGE
            val responseData = apiService.getStoriesForPaging(token = token, sizeStory = params.loadSize, pageStory = position)
            val response = responseData.listStory

            LoadResult.Page(
                data = response,
                nextKey = if (responseData.listStory.isEmpty()) null else position+1,
                prevKey = if (position == INDEX_OF_FIRST_PAGE) null else position-1
            )
        }catch(loadPageSourceException: Exception) {
            return LoadResult.Error(loadPageSourceException)
        }
    }

    companion object {
        const val INDEX_OF_FIRST_PAGE =1
    }
}