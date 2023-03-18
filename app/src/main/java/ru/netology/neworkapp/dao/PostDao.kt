package ru.netology.neworkapp.dao

import androidx.paging.PagingSource
import androidx.room.*
import ru.netology.neworkapp.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAllPosts() : PagingSource<Int, PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postEntity: PostEntity)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id : Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postEntityList: List<PostEntity>)

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty() : Boolean

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun countPosts() : Int

    @Query("DELETE FROM PostEntity")
    suspend fun removeAllPosts()

}

//class Converters {
//
//}