package ru.netology.neworkapp.repository

import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.neworkapp.apiservice.ApiService
import ru.netology.neworkapp.auth.AuthState
import ru.netology.neworkapp.dto.MediaUpload
import ru.netology.neworkapp.error.ApiError
import ru.netology.neworkapp.error.NetworkError
import java.io.IOException
import javax.inject.Inject

interface AuthRepository {
    suspend fun signIn(
        login: String,
        pass: String,
    ): AuthState

    suspend fun registerNewUser(
        login: String,
        pass: String,
        name: String,
    ): AuthState

    suspend fun registerWithPhoto(
        login: String,
        pass: String,
        name: String,
        media: MediaUpload,
    ): AuthState

}

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    ) : AuthRepository {

    override suspend fun signIn(login: String, pass: String): AuthState {
        try {
            val response = apiService.authenticateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw NetworkError
        }
    }

    override suspend fun registerNewUser(login: String, pass: String, name: String): AuthState {

        try {

            val response = apiService.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e : CancellationException) {
            throw CancellationException()
        }
        catch (e: Exception) {
            throw NetworkError
        }
    }

    override suspend fun registerWithPhoto(
        login: String,
        pass: String,
        name: String,
        media: MediaUpload
    ): AuthState {
        try {
            val file = MultipartBody.Part.createFormData(
                "file", media.file.name, media.file.asRequestBody()
            )
            val response = apiService.registerWithPhoto(
                login.toRequestBody("text/plain".toMediaType()),
                pass.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                file
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw NetworkError
        }
    }
}

