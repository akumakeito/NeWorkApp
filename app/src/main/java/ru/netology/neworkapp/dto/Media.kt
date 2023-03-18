package ru.netology.neworkapp.dto

import android.net.Uri
import retrofit2.http.Url
import java.io.File

data class Media(val uri : String)
data class MediaUpload(var file: File)

data class MediaModel(
    val uri: Uri? = null,
    val file: File? = null,
    val type: AttachmentType? = null,
)