package com.adlab.balda.contracts

interface CellView {

    fun showLetter(letter: Char)
    fun showEnteredLetter(letter: Char)
    fun select()
    fun activate(number: Int)
    fun showClearLetterButton()
    fun hideClearLetterButton()
    fun resetState()

}