package ru.netology.neworkapp.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import ru.netology.neworkapp.dto.*

interface EventRepository {
    val data: Flow<PagingData<Event>>
    suspend fun removeEventById(id: Int)
    suspend fun likeEventById(id: Int): Event
    suspend fun unlikeEventById(id: Int): Event

    suspend fun getEvents()

    suspend fun addPictureToTheEvent(
        attachmentType: AttachmentType,
        image: MultipartBody.Part
    ): Attachment

    suspend fun saveEventWithAttachment(event: Event, mediaUpload: MediaUpload, type: AttachmentType)
    suspend fun getEvent(id: Int): EventRequest
    suspend fun saveEvent(event: Event)

    suspend fun addEvent(event: EventRequest)

    suspend fun uploadMedia(type: AttachmentType, upload: MediaUpload): Attachment
}