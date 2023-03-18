package ru.netology.neworkapp.dto

data class UserPreview(
    val id: Int = 0,
    val name: String,
    val avatarUrl: String? = null,
)