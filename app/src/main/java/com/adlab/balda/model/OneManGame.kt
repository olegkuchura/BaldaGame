package com.adlab.balda.model

import com.adlab.balda.database.AppRepository
import com.adlab.balda.model.field.AbstractField
import java.util.*

class OneManGame(
        val field: AbstractField,
        val player: GamePlayer,
        private val repository: AppRepository
) {
    val copyOfCells
            get() = field.getCopyOfCells()

    val usedWords
        get() = ArrayList<String>().apply {
            this.add(field.initWord)
            this.addAll(player.enteredWords)
        }

    suspend fun makeMove(enteredCellNumber: Int, enteredLetter: Char, cellsWithWord: IntArray,
                         callback: MakeMoveCallback) {
        val enteredWord = field.getEnteredWord(enteredCellNumber, enteredLetter, cellsWithWord)

        if (field.initWord == enteredWord) {
            callback.onWordIsAlreadyUsed()
            return
        }
        player.enteredWords.forEach { word ->
            if (word == enteredWord) {
                callback.onWordIsAlreadyUsed()
                return
            }
        }
        if (repository.isWordExist(enteredWord)) {
            val isGameFinished = applyMove(enteredCellNumber, enteredLetter, enteredWord)
            if (isGameFinished) {
                callback.onGameFinished()
            } else {
                callback.makeNextMove()
            }
        } else {
            callback.onWordIsNotExist();
        }
    }

    // return true if game is finished and false otherwise
    private fun applyMove(enteredCellNumber: Int, enteredLetter: Char, enteredWord: String): Boolean {
        player.addEnteredWord(enteredWord)
        field.setLetter(enteredCellNumber, enteredLetter)
        return isGameFinished
    }

    private val isGameFinished
        get() = field.isFieldFull

    val fieldType
        get() = field.fieldType

    val fieldSize
        get() = field.fieldSize

    interface MakeMoveCallback {
        fun makeNextMove()
        fun onWordIsNotExist()
        fun onWordIsAlreadyUsed()
        fun onGameFinished()
    }
}