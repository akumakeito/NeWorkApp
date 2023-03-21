package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.auth.AuthState
import ru.netology.neworkapp.dto.MediaModel
import javax.inject.Inject

private val noAvatar = MediaModel()

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appAuth: AppAuth,
) : ViewModel() {
    val authState: LiveData<AuthState> = appAuth.authStateFlow.asLiveData(Dispatchers.Default)

    val authenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0
}