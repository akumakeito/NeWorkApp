package ru.netology.neworkapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.dto.AttachmentType
import ru.netology.neworkapp.dto.MediaModel
import ru.netology.neworkapp.dto.MediaUpload
import ru.netology.neworkapp.error.UnknownAppError
import ru.netology.neworkapp.model.FeedModelState
import ru.netology.neworkapp.repository.AuthRepository
import java.io.File
import javax.inject.Inject


private val noPhoto = MediaModel()

@HiltViewModel
class RegistrationLoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val auth: AppAuth,
) : ViewModel() {


    private val _isSignedIn = MutableLiveData(false)
    val isSignedIn: LiveData<Boolean>
        get() = _isSignedIn

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<MediaModel>
        get() = _photo

    fun invalidateSignedInState() {
        _isSignedIn.value = false
    }

    fun register(login: String, pass: String, name: String) = viewModelScope.launch {
        println("authproblem viewmodel fun register in with param: login ${login}, pass ${pass} name ${name}")

            try {
                println("authproblem viewmodel try1")
                val response =  repository.registerNewUser(login, pass, name)
                println("authproblem viewmodel try2")
                val id = response.id ?: 0
                println("authproblem viewmodel try3")
                val token = response.token ?: "null"
                println("authproblem viewmodel try4")

                println("authproblem viewmodel after repo.registerNewUser: id ${id}, token ${token}")


                auth.setAuth(id, token)

                _isSignedIn.value = true
            } catch (e: Exception) {
                println("authproblem viewmodel catch error")
                UnknownAppError
            }

        }



    fun registerWithPhoto(
        login: String,
        pass: String,
        name: String,
        media: MediaUpload,
    ) = viewModelScope.launch {

        try {
            _dataState.value = FeedModelState(loading = true)
            val response = repository.registerWithPhoto(login, pass, name, media)
            response.token?.let {
                auth.setAuth(response.id, response.token)
            }
            _isSignedIn.value = true
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = MediaModel(uri, file, AttachmentType.IMAGE)
    }

    fun signIn(login: String, password: String) = viewModelScope.launch {

        try {
            _dataState.value = FeedModelState(loading = true)

            val response = repository.signIn(login, password)
            response.token?.let {
                auth.setAuth(response.id, response.token)
            }
            _dataState.value = FeedModelState()
            _isSignedIn.value = true
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }

    }
}