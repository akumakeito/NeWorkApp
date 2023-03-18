package ru.netology.neworkapp.model

import android.net.Uri
import java.io.File

data class AttachmentModel(
    val uri : Uri? = null,
    val file : File? = null
)
