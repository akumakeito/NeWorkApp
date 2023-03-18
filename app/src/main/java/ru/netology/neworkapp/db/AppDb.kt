package ru.netology.neworkapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.neworkapp.dao.*
import ru.netology.neworkapp.entity.JobEntity
import ru.netology.neworkapp.entity.PostEntity
import ru.netology.neworkapp.entity.PostRemoteKeyEntity
import ru.netology.neworkapp.entity.UserEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class, UserEntity::class, JobEntity::class], version = 2, exportSchema = false)
@TypeConverters(InstantConverter::class, ListConverter::class, UserMapConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao() : PostDao
    abstract fun postRemoteKeyDao() : PostRemoteKeyDao
    abstract fun userDao() : UserDao
    abstract fun jobDao() : JobDao
}