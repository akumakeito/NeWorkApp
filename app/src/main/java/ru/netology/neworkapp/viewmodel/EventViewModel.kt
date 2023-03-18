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
import okhttp3.MultipartBody
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.dto.*
import ru.netology.neworkapp.model.FeedModelState
import ru.netology.neworkapp.repository.EventRepository
import ru.netology.neworkapp.util.SingleLiveEvent
import java.io.File
import java.time.Instant
import javax.inject.Inject


val editedEvent = EventCreateRequest(
    id = 0,
    content = "",
    datetime = null,
    type = EventType.OFFLINE,
    attachment = null,
    link = null,
    speakerIds = listOf()
)
val speakers = mutableListOf<User>()

private val noMedia = MediaModel()

@ExperimentalCoroutinesApi
@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    appAuth: AppAuth,
) : ViewModel() {

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _eventResponse = MutableLiveData<Event>()
    val eventResponse: LiveData<Event>
        get() = _eventResponse

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _media = MutableLiveData(noMedia)
    val media: LiveData<MediaModel>
        get() = _media

    val eventUsersData: LiveData<List<UserPreview>> = repository.eventUsersData

    var isEventIntent = false

    val newEvent: MutableLiveData<EventCreateRequest> = MutableLiveData(editedEvent)
    val usersList: MutableLiveData<List<User>> = MutableLiveData()
    val speakersData: MutableLiveData<MutableList<User>> = MutableLiveData()

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

    fun getEventUsersList(event: Event) {
        viewModelScope.launch {
            try {
                repository.getEventUsersList(event)
                _dataState.value = FeedModelState(loading = false)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(loading = true)
            }
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

    fun likeEventById(id: Int) {
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repository.likeEventById(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun dislikeEventById(id: Int) {
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repository.dislikeEventById(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun participateInEvent(id: Int) {
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repository.participateInEvent(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun quitParticipateInEvent(id: Int) {
        viewModelScope.launch {
            try {
                _eventResponse.postValue(repository.quitParticipateInEvent(id))
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun getEventCreateRequest(id: Int) {
        speakersData.postValue(speakers)
        viewModelScope.launch {
            try {
                newEvent.value = repository.getEventCreateRequest(id)
                newEvent.value?.speakerIds?.forEach {
                    speakersData.value!!.add(repository.getUserById(it))
                }
                _dataState.value = FeedModelState(error = false)
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun getUsers() {
        speakersData.postValue(speakers)
        viewModelScope.launch {
            try {
                usersList.value = repository.getUsers()
                usersList.value?.forEach { user ->
                    newEvent.value?.speakerIds?.forEach {
                        if (user.id == it) {
                            user.isChecked = true
                        }
                    }
                }
                _dataState.value = FeedModelState(error = false)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun addSpeakerIds() {
        speakersData.postValue(speakers)
        val listChecked = mutableListOf<Int>()
        val speakersUserList = mutableListOf<User>()
        usersList.value?.forEach { user ->
            if (user.isChecked) {
                listChecked.add(user.id)
                speakersUserList.add(user)
            }
        }
        speakersData.postValue(speakersUserList)
        newEvent.value = newEvent.value?.copy(speakerIds = listChecked)
    }

    fun check(id: Int) {
        usersList.value?.forEach {
            if (it.id == id) {
                it.isChecked = true
            }
        }
    }

    fun unCheck(id: Int) {
        usersList.value?.forEach {
            if (it.id == id) {
                it.isChecked = false
            }
        }
    }

    fun addLink(link: String) {
        if (link != "") {
            newEvent.value = newEvent.value?.copy(link = link)
        } else {
            newEvent.value = newEvent.value?.copy(link = null)
        }
    }

    fun changeMedia(uri: Uri?, file: File?, type: AttachmentType?) {
        _media.value = MediaModel(uri, file, type)
    }

    fun addMediaToEvent(
        type: AttachmentType,
        file: MultipartBody.Part,
    ) {
        viewModelScope.launch {
            try {
                val attachment = repository.addMediaToEvent(type, file)
                newEvent.value = newEvent.value?.copy(attachment = attachment)
                _dataState.value = FeedModelState(error = false)
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun addDateAndTime(dateAndTime: String) {
        newEvent.value = newEvent.value?.copy(datetime = dateAndTime)
    }

    fun addEventType() {
        val type = when (newEvent.value?.type) {
            EventType.OFFLINE -> EventType.ONLINE
            else -> EventType.OFFLINE
        }
        newEvent.value = newEvent.value?.copy(type = type)
    }

    fun saveEvent(event: EventCreateRequest) {
        viewModelScope.launch {
            try {
                repository.saveEvent(event)
                _dataState.value = FeedModelState(error = false)
                deleteEditEvent()
                _eventCreated.value = Unit
            } catch (e: RuntimeException) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun deleteEditEvent() {
        newEvent.postValue(editedEvent)
        speakers.clear()
        speakersData.postValue(speakers)
    }
}