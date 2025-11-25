package com.example.kursova.domain.usecase

import com.example.kursova.domain.model.UserCard
import com.example.kursova.domain.repository.UserCardRepository

class AuthenticateUserByPinUseCase(
    private val userCardRepository: UserCardRepository
) {
    suspend operator fun invoke(pin: String): UserCard? =
        userCardRepository.authenticateByPin(pin)
}