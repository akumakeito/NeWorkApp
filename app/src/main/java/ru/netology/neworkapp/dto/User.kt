package ru.netology.neworkapp.dto

data class User(
    val id : Int = 0,
    val login : String  = "",
    val name : String = "",
    val avatar : String? = null,
    var isChecked : Boolean = false
)
