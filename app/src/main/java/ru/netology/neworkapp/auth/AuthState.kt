package ru.netology.neworkapp.auth

import com.google.gson.annotations.SerializedName

data class AuthState(
    @SerializedName("id")
    val id : Int = 0,
    @SerializedName("token")
    val token : String? = null,
)
