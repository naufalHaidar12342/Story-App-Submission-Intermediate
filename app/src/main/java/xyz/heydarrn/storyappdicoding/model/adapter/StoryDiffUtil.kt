package xyz.heydarrn.storyappdicoding.model.adapter

import androidx.recyclerview.widget.DiffUtil
import xyz.heydarrn.storyappdicoding.model.api.response.ListStoryItem

class StoryDiffUtil : DiffUtil.ItemCallback<ListStoryItem> () {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }
}