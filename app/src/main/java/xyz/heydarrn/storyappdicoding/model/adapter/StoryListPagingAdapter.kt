package xyz.heydarrn.storyappdicoding.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import xyz.heydarrn.storyappdicoding.R
import xyz.heydarrn.storyappdicoding.databinding.DicodingFriendStoryBinding
import xyz.heydarrn.storyappdicoding.model.api.response.ListStoryItem

class StoryListPagingAdapter : PagingDataAdapter<ListStoryItem, StoryListPagingAdapter.StoryPagingViewHolder>(StoryDiffUtil()) {
    var whenStorySelected: OpenThisStory? = null
    fun setThisStoryForDetailView(thisStory: OpenThisStory) { this.whenStorySelected=thisStory  }

    inner class StoryPagingViewHolder(private val bindingStory:DicodingFriendStoryBinding) : RecyclerView.ViewHolder(bindingStory.root) {
        fun bindStoryWithPaging(story:ListStoryItem) {
            bindingStory.apply {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(storyImage)

                storyUsername.text = story.name
                root.setOnClickListener {
                    whenStorySelected?.chooseThisStory(
                        id = story.id,
                        username = story.name,
                        image = story.photoUrl,
                        imageDesc = story.description,
                        postedDate = story.createdAt,
                        latitude = story.lat,
                        longitude = story.lon
                    )
                }
            }
        }
    }

    override fun onBindViewHolder(holder: StoryPagingViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindStoryWithPaging(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryPagingViewHolder {
        val view = DicodingFriendStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryPagingViewHolder(view)
    }

    interface OpenThisStory {
        fun chooseThisStory(
            id:String,
            username:String,
            postedDate:String,
            image:String,
            imageDesc:String,
            longitude:String?,
            latitude:String?
        )
    }
}