package com.adlab.balda.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.adlab.balda.database.entity.WordInfo

@Dao
interface WordsDao {

    @Query("SELECT EXISTS(SELECT * FROM WordInfo)")
    suspend fun isAnyWords(): Boolean

    @Query("SELECT EXISTS(SELECT * FROM WordInfo WHERE word = :word)")
    suspend fun isWordExists(word: String): Boolean

    @Query("SELECT * FROM WordInfo")
    suspend fun getAllWords(): List<WordInfo>

    @Query("SELECT * FROM WordInfo WHERE LENGTH(word) = :length ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(length: Int): WordInfo

    @Insert
    suspend fun insertAll(words: List<WordInfo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordInfo)

}