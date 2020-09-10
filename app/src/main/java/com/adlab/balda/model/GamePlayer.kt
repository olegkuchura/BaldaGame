package com.adlab.balda.model

import java.util.*

data class GamePlayer(val nickname: String = "Player") {
    var score = 0
        private set

    val enteredWords: MutableList<String> = ArrayList()

    fun addEnteredWord(word: String) {
        enteredWords.add(word)
        score += word.length
    }
}