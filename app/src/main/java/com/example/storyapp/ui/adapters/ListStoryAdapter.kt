package com.example.storyapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.databinding.ItemRowListStoryBinding

class ListStoryAdapter(private val onItemClicked: (ListStoryItem) -> Unit) : ListAdapter<ListStoryItem, ListStoryAdapter.ListStoryViewHolder>(
    DIFF_CALLBACK
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ListStoryViewHolder(private val binding: ItemRowListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(story: ListStoryItem, onItemClicked: (ListStoryItem) -> Unit) {
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
        holder.bind(story, onItemClicked)
    }
}