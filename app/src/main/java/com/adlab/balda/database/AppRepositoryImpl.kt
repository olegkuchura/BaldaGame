package com.adlab.balda.database

import com.adlab.balda.database.entity.WordInfo
import kotlinx.coroutines.*

class AppRepositoryImpl(private val appDatabase: AppDatabase): AppRepository {

    override suspend fun isAnyWords(): Boolean = appDatabase.wordsDao().isAnyWords()

    override suspend fun isWordExist(word: String): Boolean = appDatabase.wordsDao().isWordExists(word)

    override suspend fun getAllWords(): List<WordInfo> = appDatabase.wordsDao().getAllWords()

    override suspend fun getRandomWord(length: Int): WordInfo = appDatabase.wordsDao().getRandomWord(length)

    override suspend fun insertAll(words: List<WordInfo>) = appDatabase.wordsDao().insertAll(words)

    override suspend fun insertWord(word: WordInfo) = appDatabase.wordsDao().insertWord(word)

}