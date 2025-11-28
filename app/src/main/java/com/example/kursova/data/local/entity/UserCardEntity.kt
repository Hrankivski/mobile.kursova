package com.example.kursova.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_cards")
data class UserCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val login: String,
    val name: String,
    val cardNumberMasked: String,
    val pinCode: String,
    val isAdmin: Boolean = false,
    val isSynced: Boolean = false
)
