package ru.netology.neworkapp.dao

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.neworkapp.dto.UserPreview
import java.time.Instant

class InstantConverter {

    @TypeConverter
    fun fromInstantToMillis(instant: Instant): Long =
        instant.toEpochMilli()

    @TypeConverter
    fun fromMillisToInstant(milis: Long): Instant =
        Instant.ofEpochMilli(milis)

}
class UserMapConverter {
    @TypeConverter
    fun fromUsers(map: Map<Int, UserPreview>): String {
        return Gson().toJson(map)
    }

    @TypeConverter
    fun toUsers(string: String): Map<Int, UserPreview> {
        val maptype = object : TypeToken<Map<Int, UserPreview>>() {}.type
        return Gson().fromJson(string, maptype)
    }


}

class ListConverter {
    @TypeConverter
    fun fromListDto(list: List<Int>?): String? {
        if (list == null) return ""
        return list.toString()
    }

    @TypeConverter
    fun toListDto(data: String?): List<Int>? {
        if (data == "[]") return emptyList<Int>()
        else {
            val substr = data?.substring(1, data.length - 1)
            return substr?.split(", ")?.map { it.toInt() }
        }
    }
}
