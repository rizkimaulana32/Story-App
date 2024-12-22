package com.example.storyapp

import com.example.storyapp.data.local.Story

object DataDummy {

    fun generatedStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..20) {
            val story = Story(
                id = i.toString(),
                name = "User $i",
                description = "This is a description for story $i",
                photoUrl = "https://via.placeholder.com/150?text=Story+$i",
                createdAt = "2024-11-${(i % 30) + 1}T10:00:00Z",
                lat = (-90..90).random() + Math.random(),
                lon = (-180..180).random() + Math.random()
            )
            items.add(story)
        }
        return items
    }
}