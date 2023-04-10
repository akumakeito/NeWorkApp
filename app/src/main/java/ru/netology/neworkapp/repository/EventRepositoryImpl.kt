package ru.netology.neworkapp.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.neworkapp.apiservice.ApiService
import ru.netology.neworkapp.dao.EventDao
import ru.netology.neworkapp.dto.*
import ru.netology.neworkapp.entity.EventEntity
import ru.netology.neworkapp.entity.toEntity
import ru.netology.neworkapp.error.ApiError
import ru.netology.neworkapp.error.AppError
import ru.netology.neworkapp.error.NetworkError
import ru.netology.neworkapp.error.UnknownAppError
import java.io.IOException
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
class EventRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    mediator: EventRemoteMediator,
    private val dao: EventDao,
) : EventRepository {

    override val data: Flow<PagingData<Event>> =
        Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = dao::pagingSource,
            remoteMediator = mediator
        ).flow.map {
            it.map(EventEntity::toDto)
        }


    override suspend fun removeEventById(id: Int) {
        try {
            val response = apiService.removeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeEventById(id)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun likeEventById(id: Int): Event {
        try {
            val response = apiService.likeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
            return body
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun unlikeEventById(id: Int): Event {
        try {
            val response = apiService.dislikeEventById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
            return body
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun saveEventWithAttachment(
        event: Event,
        media: MediaUpload,
        type: AttachmentType
    ) {

        try {
            val upload = uploadMedia(type, media)
            val postWithAttach = event.copy(attachment = Attachment(upload.url, type))

            saveEvent(postWithAttach)

        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun getEvents() {
        try {

            val response = apiService.getEvents()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            if (dao.isEmpty()) {
                dao.insert(body.toEntity())
            }

            if (body.size > dao.getSize()) {
                val notInRoomEvents = body.takeLast(body.size - dao.getSize())
                dao.insert(notInRoomEvents.toEntity())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: java.lang.Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun addPictureToTheEvent(
        attachmentType: AttachmentType,
        image: MultipartBody.Part
    ): Media {
        try {
            val response = apiService.uploadMedia(image)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun getEvent(id: Int): EventRequest {
        try {
            val response = apiService.getEvent(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                return EventRequest(
                    id = body.id,
                    content = body.content,
                    datetime = body.datetime,
                    coords = body.coords,
                    type = body.type,
                    attachment = body.attachment,
                    link = body.link,
                    speakerIds = body.speakerIds
                )
            }
        } catch (e: IOException) {
            throw NetworkError
        }
    }


    override suspend fun saveEvent(event: Event) {
        try {
            val response = apiService.saveEvent(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                dao.insert(EventEntity.fromDto(body))
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownAppError
        }
    }

    override suspend fun addEvent(event: EventRequest) {
        try {
            val response = apiService.addEvent(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            } else {
                val body = response.body() ?: throw ApiError(response.code(), response.message())
                dao.insert(EventEntity.fromDto(body))
            }
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun uploadMedia(type: AttachmentType, upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            val response = apiService.uploadMedia(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        }
    }
}
