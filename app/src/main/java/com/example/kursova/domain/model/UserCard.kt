package com.example.kursova.domain.model

data class UserCard(
    val id: Int,
    val login: String,
    val name: String,
    val cardNumberMasked: String,
    val pinCode: String,
    val isAdmin: Boolean
)
