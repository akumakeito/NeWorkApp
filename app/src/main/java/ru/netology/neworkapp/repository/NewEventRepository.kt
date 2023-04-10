package ru.netology.neworkapp.repository

import okhttp3.MultipartBody
import ru.netology.neworkapp.apiservice.ApiService
import ru.netology.neworkapp.dao.EventDao
import ru.netology.neworkapp.dto.Attachment
import ru.netology.neworkapp.dto.AttachmentType
import ru.netology.neworkapp.dto.EventRequest
import ru.netology.neworkapp.dto.Media
import ru.netology.neworkapp.entity.EventEntity
import ru.netology.neworkapp.error.ApiError
import ru.netology.neworkapp.error.NetworkError
import java.io.IOException
import javax.inject.Inject

interface NewEventRepository {
    suspend fun addPictureToTheEvent(
        attachmentType: AttachmentType,
        image: MultipartBody.Part
    ): Media

    suspend fun addEvent(event: EventRequest)
    suspend fun getEvent(id: Int): EventRequest
}


class NewEventRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: EventDao
) : NewEventRepository {

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
}
