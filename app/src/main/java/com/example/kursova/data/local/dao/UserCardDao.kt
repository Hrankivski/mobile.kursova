package com.example.kursova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kursova.data.local.entity.UserCardEntity

@Dao
interface UserCardDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserCardEntity): Long

    @Query("SELECT * FROM user_cards WHERE login = :login LIMIT 1")
    suspend fun getByLogin(login: String): UserCardEntity?

    @Query("SELECT * FROM user_cards WHERE pinCode = :pin LIMIT 1")
    suspend fun getByPin(pin: String): UserCardEntity?

    @Query("SELECT COUNT(*) > 0 FROM user_cards WHERE login = :login")
    suspend fun isLoginTaken(login: String): Boolean

    @Query("SELECT * FROM user_cards")
    suspend fun getAll(): List<UserCardEntity>
}
