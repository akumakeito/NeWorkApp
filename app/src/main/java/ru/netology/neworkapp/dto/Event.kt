package ru.netology.neworkapp.dto

data class Event(
    val id : Int,
    val authorId : Int,
    val author : String,
    val dateTime : String,
    val content : String,
    val published : String,
    val type : EventType,
    val likedByMe : Boolean = false,
    val participatedByMe : Boolean,
    val attachment : Attachment? = null,
    val ownedByMe : Boolean = false,
)

enum class EventType {
    OFFLINE,
    ONLINE
}
