package ru.netology.neworkapp.auth

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    private val prefs: SharedPreferences,
) {
    companion object {
        const val idKey = "id"
        const val tokenKey = "token"
        const val avatar = "avatar"
        const val name = "name"
    }


    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val id = prefs.getInt(idKey, 0)
        val token = prefs.getString(tokenKey, null)
        val avatar = prefs.getString(avatar, null)
        val name = prefs.getString(name, null)

        if (id == 0 || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token, avatar, name))
        }
    }

    val authStateFlow: StateFlow<AuthState> = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Int, token: String, avatar: String?, name: String?) {
        _authStateFlow.value = AuthState(id, token, avatar, name)
        with(prefs.edit()) {
            putInt(idKey, id)
            putString(tokenKey, token)
            putString(avatar, avatar)
            putString(name, name)
            apply()
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        with(prefs.edit()) {
            clear()
            commit()
        }
    }

}

data class AuthState(
    val id: Int = 0, val token: String? = null,
    val avatar: String? = null, val name: String? = null,
) {
    val authenticated = id != 0
}