package ru.netology.neworkapp.repository


import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.*

interface PostRepository {
    val data : Flow<PagingData<Post>>
    val postUserData : MutableLiveData<List<UserPreview>>

    suspend fun getPosts()
    suspend fun savePost(post : Post)
    suspend fun removePostById(postId : Int)

    suspend fun savePostWithAttachment(post: Post, media: MediaUpload, type : AttachmentType)


    suspend fun getLikedMentionedUsersList(post : Post)

    suspend fun likeById(id: Int) : Post
    suspend fun unlikeById(id: Int) : Post

    suspend fun getUsers() : List<User>

    suspend fun getPostById(id: Int) : Post
    suspend fun getUserById(id: Int) : User
    suspend fun uploadMedia(upload: MediaUpload): MediaResponse

}