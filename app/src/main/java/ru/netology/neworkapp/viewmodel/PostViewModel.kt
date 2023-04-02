package ru.netology.neworkapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.dto.*
import ru.netology.neworkapp.model.FeedModelState
import ru.netology.neworkapp.repository.PostRepository
import ru.netology.neworkapp.util.SingleLiveEvent
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

private val noMedia = MediaModel()

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth
) : ViewModel() {


    val data: Flow<PagingData<Post>> = appAuth.authStateFlow.flatMapLatest { (myId, _) ->
        val cached = repository.data.cachedIn(viewModelScope)
        cached.map { pagingData ->
            pagingData.map {
                it.copy(ownedByMe = it.authorId == myId)
            }
        }
    }


    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

   val edited = MutableLiveData(emptyPost)


    init {
        loadPosts()
    }


    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }



    fun changeMedia(uri: Uri?, file: File?, type: AttachmentType?) {
        _media.value = MediaModel(uri, file, type)
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
        edited.value?.let {
            _postCreated.value = Unit

            viewModelScope.launch {
                try {
                    repository.likeById(id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = emptyPost
    }

    fun unlikePostById(id: Int) {
        edited.value?.let {
            _postCreated.value = Unit

            viewModelScope.launch {
                try {
                    repository.unlikeById(id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = emptyPost
    }

    private fun clearEditedPost() {
        _postCreated.value = Unit
        _media.value = noMedia

    }

    fun editPost(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        var text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
    }


    fun savePost() {
        viewModelScope.launch {
            val post = requireNotNull(edited.value)
            try {
                _dataState.value = FeedModelState(loading = true)
                when (_media.value) {

                    noMedia -> {
                        repository.savePost(post)
                    }
                    else -> {
                        when (_media.value?.type) {

                            AttachmentType.IMAGE -> _media.value?.file?.let { file ->

                                repository.savePostWithAttachment(
                                    post,
                                    MediaUpload(file),
                                    AttachmentType.IMAGE
                                )
                            }
                            AttachmentType.VIDEO -> {
                                _media.value?.file?.let { file ->
                                    repository.savePostWithAttachment(
                                        post,
                                        MediaUpload(file),
                                        AttachmentType.VIDEO
                                    )
                                }
                            }
                            AttachmentType.AUDIO -> {
                                _media.value?.file?.let {file ->
                                    repository.savePostWithAttachment(
                                        post,
                                        MediaUpload(file),
                                        AttachmentType.AUDIO
                                    )
                                }
                            }
                            null -> repository.savePost(post)
                        }
                    }
                }
                _dataState.value = FeedModelState()
            } catch (e : Exception) {
                _dataState.value = FeedModelState(error = true)
            } finally {
                clearEditedPost()
            }
        }
    }


    fun addLink(link : String) {
        if (link.isNullOrBlank()) {
            edited.value = edited.value?.copy(link = null)
        } else {
            edited.value = edited.value?.copy(link = link)
        }
    }

    fun getPostById(id : Int) {
        _dataState.value = FeedModelState(loading = false)
        viewModelScope.launch {
            try {
                edited.value = repository.getPostById(id)
                _dataState.value = FeedModelState()
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }

    }
}


