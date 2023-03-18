package ru.netology.neworkapp.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.neworkapp.R

fun ImageView.loadAvatar(url : String, vararg transforms: BitmapTransformation = emptyArray()) =

        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.avatar_placeholder)
            .error(R.drawable.error_ic)
            .timeout(10_000)
            .transform(*transforms)
            .into(this)

    fun ImageView.loadImage(url: String, vararg transforms : BitmapTransformation = emptyArray()) =
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.image_placeholder_ic)
            .timeout(10_000)
            .transform(*transforms)
            .error(R.drawable.error_ic)
            .into(this)

    fun ImageView.loadCircleCrop(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
        loadAvatar(url, CircleCrop(), *transforms)

