package ru.netology.neworkapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.neworkapp.dao.*
import ru.netology.neworkapp.dto.Event
import ru.netology.neworkapp.entity.*

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class, UserEntity::class, JobEntity::class, EventEntity::class, EventRemoteKeyEntity::class], version = 3, exportSchema = false)
@TypeConverters(InstantConverter::class, ListConverter::class, UserMapConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao() : PostDao
    abstract fun postRemoteKeyDao() : PostRemoteKeyDao
    abstract fun userDao() : UserDao
    abstract fun jobDao() : JobDao

    abstract fun eventDao() : EventDao

    abstract fun eventRemoteKeyDao() : EventRemoteKeyDao
}