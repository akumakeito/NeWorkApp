package ru.netology.neworkapp.model

import ru.netology.neworkapp.dto.Post

data class PostFeedModel(
    val posts : List<Post> = emptyList(),
    val isEmpty : Boolean = false
)
