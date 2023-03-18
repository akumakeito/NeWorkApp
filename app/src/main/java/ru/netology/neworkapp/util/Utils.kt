package ru.netology.neworkapp.util

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

    fun showKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }


    fun formatMillisToDateTimeString(millis: Long?): String {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
            .format(millis)
    }

    fun formatMillisToDateString(millis: Long?): String? {
        return if (millis == null) null
        else SimpleDateFormat.getDateInstance(DateFormat.DEFAULT)
            .format(millis)
    }

    fun formatDateToDateTimeString(date: Date): String {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
            .format(date)
    }

    fun formatDateToDateString(date: Date): String {
        return SimpleDateFormat.getDateInstance(DateFormat.DEFAULT)
            .format(date)
    }

    fun formatDateTimeStringToMillis(dateString: String): Long {
        val sdf = SimpleDateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT)
        return sdf.parse(dateString)!!.run {
            time
        }
    }

    fun formatDateStringToMillis(dateString: String): Long {
        val sdf = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT)
        return sdf.parse(dateString)!!.run {
            time
        }
    }

    fun convertDate(date: String): String {
        return if (date == "") {
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
            cursor?.getString(columnIndex!!)
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
            cursor?.getString(columnIndex!!)
        } finally {
            cursor?.close()
        }
    }
}