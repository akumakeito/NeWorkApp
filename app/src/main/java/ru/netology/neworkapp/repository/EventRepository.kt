package ru.netology.neworkapp.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import ru.netology.neworkapp.dto.*

interface EventRepository {
    val data: Flow<PagingData<Event>>
    val eventUsersData: MutableLiveData<List<UserPreview>>
    suspend fun getEventUsersList(event: Event)
    suspend fun removeEventById(id: Int)
    suspend fun likeEventById(id: Int): Event
    suspend fun dislikeEventById(id: Int): Event
    suspend fun participateInEvent(id: Int): Event
    suspend fun quitParticipateInEvent(id: Int): Event
    suspend fun getUsers(): List<User>
    suspend fun addMediaToEvent(type: AttachmentType, file: MultipartBody.Part): Attachment
    suspend fun saveEvent(event: EventCreateRequest)
    suspend fun getEventCreateRequest(id: Int): EventCreateRequest
    suspend fun getUserById(id: Int): User
}