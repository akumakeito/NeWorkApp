package ru.netology.neworkapp.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.neworkapp.dto.AttachmentType
import ru.netology.neworkapp.dto.MediaModel
import ru.netology.neworkapp.dto.MediaUpload
import ru.netology.neworkapp.repository.AuthRepository
import java.io.File
import javax.inject.Inject

private val noAvatar = MediaModel()

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
    private val repository: AuthRepository
) : ViewModel() {
    val authState: LiveData<AuthState> = appAuth.authStateFlow.asLiveData(Dispatchers.Default)

    val authenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0
}