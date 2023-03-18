package ru.netology.neworkapp.error

import java.io.IOException
import java.sql.SQLException

sealed class AppError(var code : String) : RuntimeException() {
    companion object {
        fun from(e :Throwable) : AppError = when (e) {
            is IOException -> NetworkError
            is SQLException -> DBError
            is AppError -> e
            else -> UnknownAppError
        }
    }
}


class ApiError(code: Int, message: String) : AppError(code.toString())
object DBError : AppError("DB_error")
object NetworkError : AppError("network_error")
object UnknownAppError : AppError("unknown_app_error")