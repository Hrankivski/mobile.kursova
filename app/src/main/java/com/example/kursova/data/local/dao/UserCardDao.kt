package com.example.kursova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kursova.data.local.entity.UserCardEntity

@Dao
interface UserCardDao {

    @Query("SELECT * FROM user_cards")
    suspend fun getAll(): List<UserCardEntity>

    @Query("SELECT * FROM user_cards WHERE id = :id")
    suspend fun getById(id: Int): UserCardEntity?

    @Query("SELECT * FROM user_cards WHERE pinCode = :pin")
    suspend fun getByPin(pin: String): UserCardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<UserCardEntity>)
}
