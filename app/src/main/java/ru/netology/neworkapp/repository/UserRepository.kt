package ru.netology.neworkapp.repository

import androidx.lifecycle.MutableLiveData
import ru.netology.neworkapp.dto.User

interface UserRepository {
    val data: MutableLiveData<List<User>>
    val userData: MutableLiveData<User>
    suspend fun getAllUsers()
    suspend fun getUserById(id: Int)
}