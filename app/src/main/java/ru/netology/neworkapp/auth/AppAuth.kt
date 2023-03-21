package ru.netology.neworkapp.auth

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    private val prefs: SharedPreferences) {

    companion object {
        const val idKey = "id"
        const val tokenKey = "token"
    }

    private val _authStateFlow : MutableStateFlow<AuthState>

    init {
        val id = prefs.getInt(idKey, 0)
        val token = prefs.getString(tokenKey, null)



        if (id == 0 || token == null) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }

        Log.d("auth", "id ${_authStateFlow.value.id} token  ${_authStateFlow.value.token} ")


    }

    val authStateFlow = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Int, token: String) {
        _authStateFlow.value = AuthState(id, token)
        with(prefs.edit()) {
            putInt(idKey, id)
            putString(tokenKey, token)
            apply()
        }

        println("authproblem AppAuth after setAuth id ${authStateFlow.value.id} token  ${authStateFlow.value.token} ")
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