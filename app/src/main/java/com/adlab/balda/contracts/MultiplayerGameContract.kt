package com.adlab.balda.contracts

import com.adlab.balda.BasePresenter
import com.adlab.balda.BaseView
import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType
import com.adlab.balda.enums.GameMessageType

interface MultiplayerGameContract {

    interface View: BaseView<Presenter> {

        fun showField(rowCount: Int, colCount: Int, fieldType: FieldType, fieldSize: FieldSizeType)

        fun showUsedWords()

        fun updateCell(cellNumber: Int)

        fun showKeyboard()

        fun scrollFieldToCell(cellNumber: Int)

        fun hideKeyboard()

        fun activateActionMode()

        fun updateActivatedLetterSequence(letterSequence: String)

        fun deactivateActionMode()

        fun showScore(playersCount: Int)

        fun updateScore()

        fun showMessage(message: GameMessageType)

        fun showScoreAnimation(deltaScore: Int)

        fun updateUsedWords(listOfWords: List<String>)

        fun showGameResult(winnerNickname: String, winnerScore: Int, otherPlayers: List<Pair<String, Int>>)

        fun showGameExit()
    }

    interface Presenter: BasePresenter, BaseGamePresenter {

        fun setView(view: View)

        fun resetView()

        fun onShowUsedWordsClicked()

        fun onCellClicked(cellNumber: Int)

        fun onCellLongClicked(cellNumber: Int)

        fun onKeyboardOpen()

        fun onKeyboardHidden()

        fun enterLetter(letter: Char)

        fun clearEnteredLetter()

        fun deactivateActionMode()

        fun confirmWord()

        fun finishGame()

        fun bindScore(view: ScoreView, position: Int)
    }

}
