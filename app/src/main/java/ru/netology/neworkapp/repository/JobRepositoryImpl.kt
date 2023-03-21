package ru.netology.neworkapp.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.neworkapp.apiservice.ApiService
import ru.netology.neworkapp.dao.JobDao
import ru.netology.neworkapp.dto.Job
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.entity.JobEntity
import ru.netology.neworkapp.entity.PostEntity
import ru.netology.neworkapp.entity.toDto
import ru.netology.neworkapp.entity.toEntity
import ru.netology.neworkapp.error.ApiError
import ru.netology.neworkapp.error.NetworkError
import java.io.IOException
import javax.inject.Inject


val emptyJobList = emptyList<Job>()

class JobRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val jobDao: JobDao
): JobRepository {



    override val data: Flow<List<Job>> = jobDao.getAllJobs()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun getUserJobs(id: Int) {
        try {
            val response = apiService.getJobsByUserId(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun saveJob(job: Job) {
        try {
            val response = apiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun removeJobById(id: Int) {
        try {
            val response = apiService.removeJobById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            jobDao.removeJobById(id)
        } catch (e: IOException) {
            throw NetworkError
        }
    }

    override suspend fun getMyJobs() {
        try {
            val response = apiService.getMyJobs()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            jobDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        }
    }
}