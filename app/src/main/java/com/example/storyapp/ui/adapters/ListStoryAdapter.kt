package com.example.storyapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.local.Story
import com.example.storyapp.databinding.ItemRowListStoryBinding

class ListStoryAdapter(private val onItemClicked: (Story) -> Unit) : PagingDataAdapter<Story, ListStoryAdapter.ListStoryViewHolder>(
    DIFF_CALLBACK
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(
                oldItem: Story,
                newItem: Story
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Story,
                newItem: Story
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    class ListStoryViewHolder(private val binding: ItemRowListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(story: Story, onItemClicked: (Story) -> Unit) {
                Glide.with(binding.imageView.context)
                    .load(story.photoUrl)
                    .into(binding.imageView)
                binding.nameTextView.text = story.name
                binding.descriptionTextView.text = story.description

                itemView.setOnClickListener {
                    onItemClicked(story)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding = ItemRowListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story, onItemClicked)
        }
    }
}