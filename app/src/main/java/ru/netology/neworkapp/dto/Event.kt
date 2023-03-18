package ru.netology.neworkapp.dto

import java.time.Instant

data class Event(
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: Instant = Instant.now(),
    val eventType: EventType,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    val attachment: Attachment?,
    val link: String?,
    val ownedByMe: Boolean,
    val users: Map<Int, UserPreview>,
)

data class EventCreateRequest(
    val id: Int = 0,
    val content: String = "",
    val datetime: String? = null,
    val type: EventType? = EventType.OFFLINE,
    val attachment: Attachment? = null,
    val link: String? = null,
    val speakerIds: List<Int>? = null,
)


enum class EventType {
    OFFLINE,
    ONLINE
}


