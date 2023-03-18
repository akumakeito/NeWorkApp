package ru.netology.neworkapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.neworkapp.dto.User

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val login: String = "",
    val name: String = "",
    val avatar: String? = null,
    var isChecked: Boolean = false,
) {
    fun toDto() = User(
        id, login, name, avatar, isChecked
    )

    companion
    object {
        fun fromDto(dto: User) =
            UserEntity(dto.id, dto.login, dto.name, dto.avatar, dto.isChecked)
    }
}

fun List<UserEntity>.toDto(): List<User> = map(UserEntity::toDto)
fun List<User>.toEntity(): List<UserEntity> = map(UserEntity::fromDto)
