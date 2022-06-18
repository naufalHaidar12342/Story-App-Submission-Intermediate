package xyz.heydarrn.storyappdicoding.model.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import xyz.heydarrn.storyappdicoding.R
import xyz.heydarrn.storyappdicoding.databinding.DicodingFriendStoryBinding
import xyz.heydarrn.storyappdicoding.model.api.response.ListStoryItem

class StoryListAdapter : ListAdapter<ListStoryItem,StoryListAdapter.StoryViewHolder>(StoryDiffUtil()) {
    var whenStorySelected:OpenThisStory? = null
    fun setThisStoryForDetailView(thisStory:OpenThisStory) { this.whenStorySelected=thisStory  }

    inner class StoryViewHolder(private val bindingStory:DicodingFriendStoryBinding) : RecyclerView.ViewHolder(bindingStory.root) {
        fun bindStory(listOfStory: ListStoryItem) {
            bindingStory.apply {
                Glide.with(itemView)
                    .load(listOfStory.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .into(storyImage)

                usernameIcon.setImageResource(R.drawable.ic_baseline_people_24)
                storyUsername.text = listOfStory.name

                root.setOnClickListener {
                    whenStorySelected?.chooseThisStory(
                        id = listOfStory.id,
                        username = listOfStory.name,
                        image = listOfStory.photoUrl,
                        imageDesc = listOfStory.description,
                        postedDate = listOfStory.createdAt,
                        latitude = listOfStory.lat,
                        longitude = listOfStory.lon
                    )
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view=DicodingFriendStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bindStory(getItem(position))
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