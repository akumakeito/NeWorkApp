package ru.netology.neworkapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.model.FeedModelState
import ru.netology.neworkapp.repository.PostRepository
import ru.netology.neworkapp.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import ru.netology.neworkapp.dto.*
import java.io.File
import java.time.Instant
import javax.inject.Inject

private val emptyPost = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = null,
    authorJob = null,
    content = "",
    published = Instant.now(),
    link = null,
    likeOwnerIds = emptyList(),
    mentionIds = emptyList(),
    mentionedMe = false,
    likedByMe = false,
    attachment = null,
    ownedByMe = false,
    users = emptyMap()
)

private val mentions = mutableListOf<User>()

private val noMedia = MediaModel()

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
) : ViewModel() {

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _editedPost = MutableLiveData<Post>()
    val editedPost: LiveData<Post>
        get() = _editedPost

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    val postsUsersData: LiveData<List<UserPreview>> = repository.postUserData
    val usersList: MutableLiveData<List<User>> = MutableLiveData()
    val mentionsData: MutableLiveData<MutableList<User>> = MutableLiveData()

    val edited = MutableLiveData(emptyPost)

    val data: Flow<PagingData<Post>> = appAuth.authStateFlow.flatMapLatest { (myId, _) ->
        val cached = repository.data.cachedIn(viewModelScope)
        cached.map { pagingData ->
            pagingData.map {
                it.copy(ownedByMe = it.authorId == myId)
            }
        }
    }

    fun removeMedia() {
        _editedPost.value = _editedPost.value?.copy(attachment = null)
    }

    fun changeMedia(uri: Uri?, file: File?, type: AttachmentType?) {
        _media.value = MediaModel(uri, file, type)
    }

    fun getLikedAndMentionedUserList(post: Post) {
        viewModelScope.launch {
            try {
                repository.getLikedMentionedUsersList(post)
                _dataState.value = FeedModelState(loading = false)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removePostById(id: Int) {
        try {
            viewModelScope.launch {
                repository.removePostById(id)
                _dataState.value = FeedModelState()
            }
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likePostById(id: Int) {
        viewModelScope.launch {
            try {
                _editedPost.postValue(repository.likeById(id))
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun unlikePostById(id: Int) {
        viewModelScope.launch {
            try {
                _editedPost.postValue(repository.unlikeById(id))
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    private fun clearEditedPost() {
        _editedPost.value = emptyPost
        _media.value = noMedia
        mentions.clear()
        mentionsData.postValue(mentions)
    }

    fun editPost(post: Post) {
        _editedPost.value = post
    }

    fun changeContent(content: String) {
        var text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
    }

    fun savePost(content: String) {
        _editedPost.value = _editedPost.value?.copy(content = content)
        val post = _editedPost.value!!
        viewModelScope.launch {
            try {
                repository.savePost(post)
                _dataState.value = FeedModelState(loading = false)
                clearEditedPost()
                _postCreated.value = Unit
                FeedModelState()
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

//    fun savePost(post: Post) {
//        viewModelScope.launch {
//            try {
//                _dataState.value = FeedModelState(loading = true)
//                when (_media.value) {
//                    noMedia -> repository.savePost(post)
//                    else -> {
//                        when (_media.value?.type) {
//                            AttachmentType.IMAGE -> _media.value?.file?.let { file ->
//                                repository.savePostWithAttachment(
//                                    post,
//                                    MediaUpload(file),
//                                    AttachmentType.IMAGE
//                                )
//                            }
//                            AttachmentType.VIDEO -> {
//                                _media.value?.file?.let { file ->
//                                    repository.savePostWithAttachment(
//                                        post,
//                                        MediaUpload(file),
//                                        AttachmentType.VIDEO
//                                    )
//                                }
//                            }
//                            AttachmentType.AUDIO -> {
//                                _media.value?.file?.let {file ->
//                                    repository.savePostWithAttachment(
//                                        post,
//                                        MediaUpload(file),
//                                        AttachmentType.AUDIO
//                                    )
//                                }
//                            }
//                            null -> repository.savePost(post)
//                        }
//                    }
//                }
//                _dataState.value = FeedModelState()
//            } catch (e : Exception) {
//                _dataState.value = FeedModelState(error = true)
//            } finally {
//                clearEditedPost()
//            }
//        }
//    }

    fun updateMentionsIds() {
        mentionsData.postValue(mentions)
        val listChecked = mutableListOf<Int>()
        val mentionsUserList = mutableListOf<User>()
        usersList.value?.forEach { user ->
            if (user.isChecked) {
                listChecked.add(user.id)
                mentionsUserList.add(user)
            }
        }

        mentionsData.postValue(mentionsUserList)
        _editedPost.value = editedPost.value?.copy(mentionIds = listChecked)
    }

    fun checkUser (id : Int) {
        usersList.value?.forEach {
            if (it.id == id) {
                it.isChecked = true
            }
        }
    }

    fun uncheckUser (id : Int) {
        usersList.value?.forEach {
            if (it.id == id) {
                it.isChecked = false
            }
        }
    }

    fun addLink(link : String) {
        if (link.isNullOrBlank()) {
            _editedPost.value = _editedPost.value?.copy(link = null)
        } else {
            _editedPost.value = _editedPost.value?.copy(link = link)
        }
    }

    fun getPostById(id : Int) {
        mentionsData.postValue(mentions)
        _dataState.value = FeedModelState(loading = false)
        viewModelScope.launch {
            try {
                _editedPost.value = repository.getPostById(id)
                _editedPost.value?.mentionIds?.forEach {
                    mentionsData.value!!.add(repository.getUserById(it))
                }
                _dataState.value = FeedModelState()
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }

    }

    fun getUsers() {
        mentionsData.postValue(mentions)
        _dataState.value = FeedModelState(loading = true)
        viewModelScope.launch {
            try {
                usersList.value = repository.getUsers()
                usersList.value?.forEach { user ->
                    _editedPost.value?.mentionIds?.forEach {
                        if (user.id == it) {
                            user.isChecked = true
                        }
                    }
                }

                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun addMediaToPost(
        type: AttachmentType,
        file: MultipartBody.Part,
    ) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = false)
                val attachment = repository.uploadMedia(type, file)
                _editedPost.value = _editedPost.value?.copy(attachment = attachment)
                _dataState.value = FeedModelState()
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }




}


