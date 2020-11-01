package com.adlab.balda.database

import com.adlab.balda.database.entity.WordInfo

interface AppRepository {

    suspend fun isAnyWords(): Boolean

    suspend fun isWordExist(word: String): Boolean

    suspend fun getAllWords(): List<WordInfo>

    suspend fun getRandomWord(length: Int): WordInfo

    suspend fun insertAll(words: List<WordInfo>)

    suspend fun insertWord(word: WordInfo)
}