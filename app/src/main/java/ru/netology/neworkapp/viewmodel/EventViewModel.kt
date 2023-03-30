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
import ru.netology.neworkapp.repository.EventRepository
import ru.netology.neworkapp.util.SingleLiveEvent
import java.io.File
import java.time.Instant
import javax.inject.Inject


val emptyEvent = Event(
    0,
    0,
    "",
    null,
    null,
    "",
    "",
    Instant.now(),
    null,
    EventType.OFFLINE,
    emptyList(),
    false,
    emptyList(),
    emptyList(),
    false,
    null,
    null,
    false
)

private val noMedia = MediaModel()

@ExperimentalCoroutinesApi
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    appAuth: AppAuth,
) : ViewModel() {

    val data: Flow<PagingData<Event>> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
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


    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    private val edited = MutableLiveData(emptyEvent)

    init {
        loadEvents()
    }

    fun loadEvents() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }



    fun removeEventById(id: Int) {
        viewModelScope.launch {
            try {
                repository.removeEventById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun removeMedia() {
        _media.value = noMedia
    }


    fun likeEventById(id: Int) {
        edited.value?.let {
            _eventCreated.value = Unit

            viewModelScope.launch {
                try {
                    repository.likeEventById(id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = emptyEvent
    }

    fun dislikeEventById(id: Int) {
        edited.value?.let {
            _eventCreated.value = Unit

            viewModelScope.launch {
                try {
                    repository.unlikeEventById(id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = emptyEvent
    }


    fun addLink(link : String) {
        if (link.isNullOrBlank()) {
            edited.value = edited.value?.copy(link = null)
        } else {
            edited.value = edited.value?.copy(link = link)
        }
    }

    fun changeMedia(uri: Uri?, file: File?, type: AttachmentType?) {
        _media.value = MediaModel(uri, file, type)
    }

      fun addDateAndTime(dateAndTime: String) {
          edited.value = edited.value?.copy(datetime = dateAndTime)
    }

    fun addEventType() {
        val type =  EventType.ONLINE
        edited.value = edited.value?.copy(type = type)
    }

    private fun clearEditedPost() {
        _eventCreated.value = Unit
        _media.value = noMedia

    }

    fun editEvent(event: Event) {

        edited.value = event
    }

    fun changeContent(content: String) {
        var text = content.trim()
        if (edited.value?.content == text) return
        edited.value = edited.value?.copy(content = text)
    }


    fun saveEvent() {
        viewModelScope.launch {
            val edit = requireNotNull(edited.value)
            try {
                _dataState.value = FeedModelState(loading = true)
                when (_media.value) {

                    noMedia -> {
                        repository.saveEvent(edit)
                    }
                    else -> {
                        when (_media.value?.type) {

                            AttachmentType.IMAGE -> _media.value?.file?.let { file ->

                                repository.saveEventWithAttachment(
                                    edit,
                                    MediaUpload(file),
                                    AttachmentType.IMAGE
                                )
                            }
                            AttachmentType.VIDEO -> {
                                _media.value?.file?.let { file ->
                                    repository.saveEventWithAttachment(
                                        edit,
                                        MediaUpload(file),
                                        AttachmentType.VIDEO
                                    )
                                }
                            }
                            AttachmentType.AUDIO -> {
                                _media.value?.file?.let {file ->
                                    repository.saveEventWithAttachment(
                                        edit,
                                        MediaUpload(file),
                                        AttachmentType.AUDIO
                                    )
                                }
                            }
                            null -> repository.saveEvent(edit)
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
}