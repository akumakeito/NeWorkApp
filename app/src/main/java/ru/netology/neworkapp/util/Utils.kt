package ru.netology.neworkapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



    fun formatMillisToDateTimeString(millis: Long?): String {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
            .format(millis)
    }

    fun convertDateAndTime(dateAndTime: String): String {
        return if (dateAndTime.isBlank()) {
            ""
        } else {
            val parsedDate = LocalDateTime.parse(dateAndTime, DateTimeFormatter.ISO_DATE_TIME)
            return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
        }
    }

    fun convertDate(date: String): String {
        return if (date.isBlank()) {
            ""
        } else {
            val parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
            return parsedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        }
    }

    fun selectDateDialog(editText: EditText?, context: Context) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year, month, dayOfMonth)
            val result = GregorianCalendar(year, month, dayOfMonth).time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.uuu'Z'", Locale.getDefault())
            editText?.setText(dateFormat.format(result))
        }, startYear, startMonth, startDay).show()
    }

    fun getVideoPathFromUri(uri: Uri, activity: Activity): String? {
        var cursor: Cursor? = null
        return try {
            val projection = arrayOf(MediaStore.Video.Media.DATA)
            cursor = activity.contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor?.moveToFirst()
            cursor?.getString(requireNotNull(columnIndex))
        } finally {
            cursor?.close()
        }
    }

    fun getAudioPathFromUri(uri: Uri, activity: Activity): String? {
        var cursor: Cursor? = null
        return try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA)
            cursor = activity.contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            cursor?.moveToFirst()
            cursor?.getString(requireNotNull(columnIndex))
        } finally {
            cursor?.close()
        }
    }
}

object DataConverter {

    @SuppressLint("SimpleDateFormat")
    fun convertDateToLocalDate(date: List<Int>): String {
        val x = date[0]
        val y = date[1] + 1
        val z = date[2]
        val h = date[3]
        val m = date[4]
        val day = when (x) {
            in 1..9 -> "0$x"
            else -> {
                "$x"
            }
        }
        val month = when (y) {
            in 1..9 -> "0$y"
            else -> {
                "$y"
            }
        }
        val year = "$z"
        val hour = when (h) {
            in 1..9 -> "0$h"
            else -> "$h"
        }
        val minute = when (m) {
            in 1..9 -> "0$m"
            else -> "$m"
        }
        val newDate = "$day.$month.$year $hour:$minute"
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val date = formatter.parse(newDate)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'")
        return sdf.format(requireNotNull(date))
    }
}