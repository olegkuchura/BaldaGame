package com.adlab.balda.presenters

import com.adlab.balda.contracts.CellView
import com.adlab.balda.contracts.MultiplayerGameContract
import com.adlab.balda.contracts.ScoreView
import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType
import com.adlab.balda.enums.GameMessageType
import com.adlab.balda.model.GameLab
import com.adlab.balda.model.MultiplePlayersGame
import com.adlab.balda.model.view_field.AbstractViewField
import com.adlab.balda.model.view_field.ClassicViewField
import com.adlab.balda.model.view_field.HexagonViewField
import com.adlab.balda.utils.isCorrectChar

class MultiplayerGamePresenter(
        private var mGameView: MultiplayerGameContract.View?
): MultiplayerGameContract.Presenter {

    private val game: MultiplePlayersGame = GameLab.getInstance().multiplayerGame

    private var field: AbstractViewField =
            getViewFieldByType(game.fieldType, game.getCopyOfCells(), game.fieldSize)

    init {
        mGameView?.setPresenter(this)
    }

    override fun setView(view: MultiplayerGameContract.View) {
        mGameView = view
        view.setPresenter(this)
    }

    override fun resetView() {
        mGameView = null
    }

    override fun onShowUsedWordsClicked() {
        mGameView?.showUsedWords()
//        mGameView?.showGameResult("Artem", 68,
//            listOf(Pair("Viktor", 13), Pair("Marina", 45), Pair("Oleg", 26)))
    }

    override fun start() {
        mGameView?.let { gameView ->
            gameView.showField(game.fieldSize.intValue(), game.fieldSize.intValue(), game.fieldType, game.fieldSize)
            mGameView?.showScore(game.playersCount)
            gameView.updateUsedWords(game.usedWords)
            if (!field.activeCellNumbers.isEmpty()) {
                gameView.activateActionMode()
                gameView.updateActivatedLetterSequence(field.enteredLetterSequence)
            }
            if (field.hasSelectedCell) {
                gameView.updateCell(field.clearSelection())
            }
        }
    }

    override fun bindCell(cellView: CellView, cellNumber: Int) {
        if (cellNumber == field.enteredCellNumber) {
            cellView.showEnteredLetter(field.enteredLetter)
        } else {
            cellView.showLetter(field.items[cellNumber])
        }
        if (cellNumber == field.enteredCellNumber && field.activeCellNumbers.isEmpty()) {
            cellView.showClearLetterButton()
        } else {
            cellView.hideClearLetterButton()
        }
        cellView.resetState()
        if (cellNumber == field.selectedCellNumber) {
            cellView.select()
        } else {
            val activeIndex = field.activeCellNumbers.indexOf(cellNumber)
            if (activeIndex != -1) {
                cellView.activate(activeIndex + 1)
            }
        }
    }

    override fun onCellClicked(cellNumber: Int) {
        mGameView?.let { gameView ->
            if (field.activeCellNumbers.isEmpty()) {
                if (cellNumber == field.selectedCellNumber) return
                val oldSelectedCellNumber = field.selectedCellNumber
                val isSelected = field.trySetSelectedCellNumber(cellNumber)
                gameView.updateCell(oldSelectedCellNumber)
                if (isSelected) {
                    gameView.updateCell(cellNumber)
                    gameView.showKeyboard()
                } else {
                    gameView.hideKeyboard()
                }
            } else {
                val changedItems = field.toggleActiveModeForCell(cellNumber)
                for (item in changedItems) {
                    gameView.updateCell(item)
                }
                if (field.activeCellNumbers.isEmpty()) {
                    gameView.deactivateActionMode()
                } else {
                    gameView.updateActivatedLetterSequence(field.enteredLetterSequence)
                }
            }
        }
    }

    override fun onCellLongClicked(cellNumber: Int) {
        mGameView?.let { gameView ->
            // remove selection if it is needed
            if (field.activeCellNumbers.isEmpty() && field.hasSelectedCell) {
                gameView.updateCell(field.clearSelection())
                gameView.hideKeyboard()
            }
            if (!field.hasEnteredLetter) {
                gameView.showMessage(GameMessageType.NEED_ENTER_LETTER)
                return
            }
            gameView.updateCell(field.enteredCellNumber)
            val wasActiveModeActivated = !field.activeCellNumbers.isEmpty()
            val changedItems = field.toggleActiveModeForCell(cellNumber)
            for (item in changedItems) {
                gameView.updateCell(item)
            }
            if (field.activeCellNumbers.isEmpty() && wasActiveModeActivated) {
                gameView.deactivateActionMode()
            } else if (!field.activeCellNumbers.isEmpty() && !wasActiveModeActivated) {
                gameView.activateActionMode()
                gameView.updateActivatedLetterSequence(field.enteredLetterSequence)
            }
        }
    }

    override fun onKeyboardOpen() {
        if (!field.hasSelectedCell) {
            mGameView?.hideKeyboard()
        } else {
            //todo do normal scroll to cell
            mGameView?.scrollFieldToCell(field.selectedCellNumber)
        }
    }

    override fun onKeyboardHidden() {
        mGameView?.updateCell(field.clearSelection())
    }

    override fun enterLetter(letter: Char) {
        mGameView?.let { gameView ->
            if (letter.isCorrectChar()) {
                val oldEnteredCellNumber = field.enteredCellNumber
                field.enteredLetter = letter
                field.enteredCellNumber = field.selectedCellNumber
                gameView.updateCell(field.enteredCellNumber)
                if (oldEnteredCellNumber != field.enteredCellNumber) {
                    gameView.updateCell(oldEnteredCellNumber)
                }
            } else {
                gameView.showMessage(GameMessageType.INCORRECT_SYMBOL)
            }
        }
    }

    override fun clearEnteredLetter() {
        mGameView?.updateCell(field.clearEnteredLetter())
    }

    override fun deactivateActionMode() {
        mGameView?.let { gameView ->
            while (field.activeCellNumbers.size != 0) {
                val removedCellNumber = field.activeCellNumbers.removeLast()
                gameView.updateCell(removedCellNumber)
            }
            gameView.updateCell(field.enteredCellNumber)
        }
    }

    override fun confirmWord() {
        if (field.activeCellsContainEnteredLetter()) {
            val oldScore = game.getPlayerScore(game.curPlayerIndex)
            game.makeMove(field.enteredCellNumber, field.enteredLetter,
                    field.activeCellNumbersArray, object : MultiplePlayersGame.MakeMoveCallback {
                override fun makeNextMove() {
                    updateViewAfterSuccessfulMove(oldScore, game.getPlayerName(game.curPlayerIndex))
                }

                override fun onWordIsNotExist() {
                    mGameView?.showMessage(GameMessageType.NO_SUCH_WORD)
                }

                override fun onWordIsAlreadyUsed() {
                    mGameView?.showMessage(GameMessageType.WORD_ALREADY_USED)
                }

                override fun onGameFinished() {
                    updateViewAfterSuccessfulMove(oldScore, game.getPlayerName(game.curPlayerIndex))
                    val winnerIndex = game.winnerIndex
                    val winnerNickname = game.getPlayerName(winnerIndex)
                    val winnerScore = game.getPlayerScore(winnerIndex)
                    val otherPlayers = mutableListOf<Pair<String, Int>>()
                    for (i in 0 until game.playersCount) {
                        if (i != winnerIndex) {
                            otherPlayers.add(Pair(game.getPlayerName(i), game.getPlayerScore(i)))
                        }
                    }
                    mGameView?.showGameResult(winnerNickname, winnerScore, otherPlayers)
                }
            })
        } else {
            mGameView?.showMessage(GameMessageType.MUST_CONTAIN_NEW_LETTER)
        }
    }

    override fun finishGame() {
        mGameView?.showGameExit()
    }

    override fun bindScore(view: ScoreView, position: Int) {
        view.showScore(game.getPlayerName(position), game.getPlayerScore(position),
                game.curPlayerIndex == position)
    }

    private fun updateViewAfterSuccessfulMove(oldScore: Int, curPlayerNickname: String) {
        mGameView?.let { gameView ->
            field.items = game.getCopyOfCells()
            field.clearEnteredLetter()
            val activeCellNumbers = field.activeCellNumbersArray
            field.activeCellNumbers.clear()
            for (cellNumber in activeCellNumbers) {
                gameView.updateCell(cellNumber)
            }
            gameView.deactivateActionMode()
            gameView.updateScore()
            //todo score animation
            //gameView.showScoreAnimation(player!!.score - oldScore)
            gameView.updateUsedWords(game.usedWords)
        }
    }

    private fun getViewFieldByType(fieldType: FieldType, items: CharArray, fieldSize: FieldSizeType): AbstractViewField {
        return if (fieldType === FieldType.SQUARE) {
            ClassicViewField(items, fieldSize)
        } else {
            HexagonViewField(items, fieldSize)
        }
    }

}