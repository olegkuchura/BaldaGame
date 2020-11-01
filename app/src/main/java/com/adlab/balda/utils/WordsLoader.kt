package com.adlab.balda.utils

import android.content.Context
import com.adlab.balda.database.AppRepository
import com.adlab.balda.database.entity.WordInfo
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.NumberFormatException

class WordsLoader(val context: Context) {

    companion object {
        private const val FILE_PATH = "words_rus.sql"
    }

    suspend fun loadFromAssets(repo: AppRepository): List<WordInfo> = ArrayList<WordInfo>().apply {
        try {
            BufferedReader(InputStreamReader(context.assets.open(FILE_PATH))).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    if (line!!.isEmpty() || line!![line!!.length - 1] != ';') continue
                    val list = line!!.split(',')
                    val id: Long = list[0].trim().substring(1).toLong()
                    val word: String = list[1].trim().trim('\'')
                    if (word == "ящурка") log("ящурка")
                    val code: Long? = try { list[2].trim().toLong()} catch (e: NumberFormatException) { null }
                    val codeParent: Long? = try { list[3].trim().toLong()} catch (e: NumberFormatException) { null }
                    val gender: String = list[4].trim()
                    val wcase: String = list[5].trim()
                    val soul: Int? = try { list[6].substring(0, list[6].length-2).trim().toInt() } catch (e: NumberFormatException) { null }
                    val wordInfo = WordInfo(id, word, code, codeParent, gender, wcase, soul)
                    this.add(wordInfo)
                }
            }
        } catch (e: IOException) {
            log("loadFromAssets -> $e")
            e.printStackTrace()
        }
        log("insertAll start")
        repo.insertAll(this)
        log("insertAll end")
    }


}