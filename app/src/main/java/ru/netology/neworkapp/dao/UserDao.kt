package ru.netology.neworkapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.neworkapp.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity ORDER BY id DESC")
    fun getAllUsers(): LiveData<List<UserEntity>>

    @Insert
    suspend fun insert(list: List<UserEntity>)
}