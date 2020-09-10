package com.adlab.balda.model

import com.adlab.balda.database.WordsDataSource
import com.adlab.balda.database.WordsDataSource.CheckWordCallback
import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType
import com.adlab.balda.model.field.AbstractField
import com.adlab.balda.model.field.ClassicField
import com.adlab.balda.model.field.HexagonField
import java.util.*
import kotlin.collections.ArrayList

class MultiplePlayersGame(
        private val field: AbstractField,
        private val players: List<GamePlayer>,
        private val wordsRepository: WordsDataSource
) {
    var curPlayerIndex: Int = 0
        private set

    val fieldType
        get() = field.fieldType

    val fieldSize
        get() = field.fieldSize

    val playersCount
        get() = players.size

    val winnerIndex
        get() = players.indexOf(players.maxBy { it.score })

    fun getPlayerName(index: Int) = players[index].nickname

    fun getPlayerScore(index: Int) = players[index].score

    fun getCopyOfCells(): CharArray {
        return field.getCopyOfCells()
    }

    val usedWords
        get() = with(ArrayList<String>()) {
            add(field.initWord)
            players.forEach {
                addAll(it.enteredWords)
            }
            this.also { it.sort() }
        }


    fun makeMove(enteredCellNumber: Int, enteredLetter: Char, cellsWithWord: IntArray, callback: MakeMoveCallback) {
        val enteredWord: String = field.getEnteredWord(enteredCellNumber, enteredLetter, cellsWithWord)
        if (field.initWord == enteredWord) {
            callback.onWordIsAlreadyUsed()
            return
        }
        players.forEach {
            for (word in it.enteredWords) {
                if (word == enteredWord) {
                    callback.onWordIsAlreadyUsed()
                    return
                }
            }
        }

        wordsRepository.isWordExist(enteredWord) { isWordExist ->
            if (isWordExist) {
                val isGameFinished = applyMove(enteredCellNumber, enteredLetter, enteredWord)
                if (isGameFinished) {
                    callback.onGameFinished()
                } else {
                    callback.makeNextMove()
                }
            } else {
                callback.onWordIsNotExist()
            }
        }
    }

    // return true if game is finished and false otherwise
    private fun applyMove(enteredCellNumber: Int, enteredLetter: Char, enteredWord: String): Boolean {
        players[curPlayerIndex].addEnteredWord(enteredWord)
        moveIndexToNextPlayer()
        field.setLetter(enteredCellNumber, enteredLetter)
        return isGameFinish()
    }

    private fun moveIndexToNextPlayer() {
        if (curPlayerIndex == players.size - 1)
            curPlayerIndex = 0
        else curPlayerIndex++
    }

    private fun isGameFinish() = field.isFieldFull


    interface MakeMoveCallback {
        fun makeNextMove()
        fun onWordIsNotExist()
        fun onWordIsAlreadyUsed()
        fun onGameFinished()
    }


}