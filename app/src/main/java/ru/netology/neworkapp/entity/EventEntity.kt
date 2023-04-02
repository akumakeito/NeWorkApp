package ru.netology.neworkapp.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.neworkapp.dao.CoordinatesConverter
import ru.netology.neworkapp.dao.InstantConverter
import ru.netology.neworkapp.dao.ListConverter
import ru.netology.neworkapp.dto.*
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
    @TypeConverters(CoordinatesConverter::class)
    val coords: Coordinates?,
    val type: EventType,
    @TypeConverters(ListConverter::class)
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    @TypeConverters(ListConverter::class)
    val speakerIds: List<Int>,
    @TypeConverters(ListConverter::class)
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    @Embedded
    val attachment: AttachmentEmbedded?,
    val link: String?,
    val ownedByMe: Boolean,


    ) {

    fun toDto() = Event(id, authorId, author, authorAvatar, authorJob, content,
        datetime, published, coords,type, likeOwnerIds,likedByMe, speakerIds,
        participantsIds, participatedByMe, attachment?.toDto(), link, ownedByMe)

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.authorJob,
                dto.content, dto.datetime, dto.published, dto.coords,
                dto.type, dto.likeOwnerIds, dto.likedByMe,
                dto.speakerIds, dto.participantsIds,
                dto.participatedByMe, AttachmentEmbedded.fromDto(dto.attachment),
                dto.link, dto.ownedByMe)

        fun fromDtoFlow(dto: Event) =
            EventEntity(dto.id, dto.authorId, dto.author, dto.authorAvatar, dto.authorJob,
                dto.content, dto.datetime, dto.published, dto.coords,
                dto.type, dto.likeOwnerIds, dto.likedByMe,
                dto.speakerIds, dto.participantsIds,
                dto.participatedByMe, AttachmentEmbedded.fromDto(dto.attachment),
                dto.link, dto.ownedByMe)
    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)
fun List<Event>.toEntityFlow(): List<EventEntity> = map(EventEntity::fromDtoFlow)