package ru.netology.neworkapp.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.neworkapp.dao.InstantConverter
import ru.netology.neworkapp.dao.ListConverter
import ru.netology.neworkapp.dao.UserMapConverter
import ru.netology.neworkapp.dto.Attachment
import ru.netology.neworkapp.dto.Event
import ru.netology.neworkapp.dto.EventType
import ru.netology.neworkapp.dto.UserPreview
import java.time.Instant

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    @TypeConverters(InstantConverter::class)
    val published: Instant,
    val eventType: EventType,
    @TypeConverters(ListConverter::class)
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    @TypeConverters(ListConverter::class)
    val speakerIds: List<Int>,
    @TypeConverters(ListConverter::class)
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    @Embedded
    val attachment: Attachment?,
    val link: String?,
    val ownedByMe: Boolean,
    @TypeConverters(UserMapConverter::class)
    val users: Map<Int, UserPreview>,
) {

    fun toDto() = Event(
        id, authorId, author, authorAvatar, authorJob, content,
        datetime, published, eventType, likeOwnerIds, likedByMe, speakerIds,
        participantsIds, participatedByMe, attachment, link, ownedByMe, users
    )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(
                dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.authorJob,
                dto.content, dto.datetime, dto.published,
                dto.eventType, dto.likeOwnerIds, dto.likedByMe,
                dto.speakerIds, dto.participantsIds,
                dto.participatedByMe, dto.attachment,
                dto.link, dto.ownedByMe, dto.users
            )
    }
}

fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)