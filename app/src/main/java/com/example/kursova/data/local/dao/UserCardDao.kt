package com.example.kursova.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kursova.data.local.entity.UserCardEntity

@Dao
interface UserCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UserCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<UserCardEntity>)

    @Query("SELECT * FROM user_cards")
    suspend fun getAll(): List<UserCardEntity>

    @Query("SELECT * FROM user_cards WHERE login = :login LIMIT 1")
    suspend fun getByLogin(login: String): UserCardEntity?

    @Query("SELECT * FROM user_cards WHERE pinCode = :pinCode LIMIT 1")
    suspend fun getByPin(pinCode: String): UserCardEntity?

    @Query("SELECT * FROM user_cards WHERE isSynced = 0")
    suspend fun getNotSynced(): List<UserCardEntity>

    @Query("DELETE FROM user_cards")
    suspend fun clearAll()
}
