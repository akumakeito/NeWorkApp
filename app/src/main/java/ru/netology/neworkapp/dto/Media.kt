package ru.netology.neworkapp.dto

import android.net.Uri
import java.io.File

data class Media(val url : String)
data class MediaUpload(var file: File)

data class MediaModel(
    val uri: Uri? = null,
    val file: File? = null,
    val type: AttachmentType? = null,
)