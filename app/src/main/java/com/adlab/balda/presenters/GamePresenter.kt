package com.adlab.balda.presenters

import com.adlab.balda.contracts.CellView
import com.adlab.balda.contracts.GameContract
import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType
import com.adlab.balda.enums.GameMessageType
import com.adlab.balda.model.GameLab
import com.adlab.balda.model.OneManGame
import com.adlab.balda.model.move_search.Move
import com.adlab.balda.model.move_search.MoveFinderManager
import com.adlab.balda.model.view_field.AbstractViewField
import com.adlab.balda.model.view_field.ClassicViewField
import com.adlab.balda.model.view_field.HexagonViewField
import com.adlab.balda.utils.isCorrectChar
import com.adlab.balda.utils.log
import kotlinx.coroutines.*

class GamePresenter(
        private var mGameView: GameContract.View?
): GameContract.Presenter {

    private val game: OneManGame = GameLab.getInstance().game

    private var field: AbstractViewField =
            getViewFieldByType(game.fieldType, game.copyOfCells, game.fieldSize)

    private var hintMove: Move? = null

    init {
        mGameView?.setPresenter(this)
    }

    override fun setView(view: GameContract.View) {
        mGameView = view
        view.setPresenter(this)
    }

    override fun resetView() {
        mGameView = null
    }

    override fun onShowUsedWordsClicked() {
        mGameView?.showUsedWords()
    }


    override fun start() {
        mGameView?.let { gameView ->
            gameView.showField(game.fieldSize.intValue(), game.fieldSize.intValue(), game.fieldType, game.fieldSize)
            gameView.updateScore(game.player.score)
            gameView.updateUsedWords(game.usedWords)
            if (field.activeCellNumbers.isNotEmpty()) {
                gameView.activateActionMode()
                gameView.updateActivatedLetterSequence(field.enteredLetterSequence)
            }
            if (field.hasSelectedCell) {
                gameView.updateCell(field.clearSelection())
            }
            hintMove?.let { gameView.showHintBanner(it.word) }
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

        hintMove?.let { move ->
            if (cellNumber == move.addedCharPos) {
                cellView.showEnteredLetter(move.addedChar)
            }
            val movePos = move.wordCharsPos.indexOf(cellNumber)
            if (movePos != -1) {
                cellView.moveSelect(movePos + 1)
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
            val wasActiveModeActivated = field.activeCellNumbers.isNotEmpty()
            val changedItems = field.toggleActiveModeForCell(cellNumber)
            for (item in changedItems) {
                gameView.updateCell(item)
            }
            if (field.activeCellNumbers.isEmpty() && wasActiveModeActivated) {
                gameView.deactivateActionMode()
            } else if (field.activeCellNumbers.isNotEmpty() && !wasActiveModeActivated) {
                gameView.activateActionMode()
                gameView.updateActivatedLetterSequence(field.enteredLetterSequence)
            }
        }
    }

    override fun onKeyboardOpen() {
        if (!field.hasSelectedCell) {
            mGameView?.hideKeyboard()
        } else {
            mGameView?.scrollFieldToCell(field.selectedCellNumber)
        }
    }

    override fun onKeyboardHidden() {
        mGameView?.updateCell(field.clearSelection())
    }

    override fun enterLetter(letter: Char) {
        mGameView?.let { gameView ->
            gameView.hideHintBanner()
            hideHint()
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
            val oldScore: Int = game.player.score
            game.makeMove(field.enteredCellNumber, field.enteredLetter,
                    field.activeCellNumbersArray, object : OneManGame.MakeMoveCallback {
                override fun makeNextMove() {
                    updateViewAfterSuccessfulMove(oldScore)
                }

                override fun onWordIsNotExist() {
                    mGameView?.showMessage(GameMessageType.NO_SUCH_WORD)
                }

                override fun onWordIsAlreadyUsed() {
                    mGameView?.showMessage(GameMessageType.WORD_ALREADY_USED)
                }

                override fun onGameFinished() {
                    updateViewAfterSuccessfulMove(oldScore)
                    mGameView?.showGameResult(game.player.score)
                }
            })
        } else {
            mGameView?.showMessage(GameMessageType.MUST_CONTAIN_NEW_LETTER)
        }
    }

    override fun hintClicked() {
        mGameView?.let { gameView ->
            clearEnteredLetter()
            hideHint()
            if (!MoveFinderManager.inited) return
            hintMove = MoveFinderManager.findRandomMove(game.field, game.usedWords)
            gameView.showHintBanner(hintMove!!.word)
            for (i in hintMove!!.wordCharsPos) {
                gameView.updateCell(i)
            }
        }
    }

    override fun hideHintClicked() {
        mGameView?.hideHintBanner()
        hideHint()
    }

    override fun applyHintClicked() {
        hintMove?.let { hintMove ->
            field.enteredCellNumber = hintMove.addedCharPos
            field.enteredLetter = hintMove.addedChar
            hintMove.wordCharsPos.forEach {
                field.activeCellNumbers.add(it)
            }
        }

        hintMove = null

        log(field.activeCellNumbersArray.toList().map { it.toString() }, ", ")
        confirmWord()

        mGameView?.hideHintBanner()
    }

    override fun finishGame() {
        mGameView?.showGameExit()
    }

    private fun updateViewAfterSuccessfulMove(oldScore: Int) {
        mGameView?.let { gameView ->
            field.items = game.copyOfCells
            field.clearEnteredLetter()
            val activeCellNumbers = field.activeCellNumbersArray
            field.activeCellNumbers.clear()
            for (cellNumber in activeCellNumbers) {
                gameView.updateCell(cellNumber)
            }
            gameView.deactivateActionMode()
            gameView.updateScore(game.player.score)
            //mGameView.showScoreAnimation(player.getScore() - oldScore);
            gameView.updateUsedWords(game.usedWords)
        }
    }

    private fun getViewFieldByType(fieldType: FieldType, items: CharArray, fieldSize: FieldSizeType)
            : AbstractViewField {
        return if (fieldType === FieldType.SQUARE) {
            ClassicViewField(items, fieldSize)
        } else {
            HexagonViewField(items, fieldSize)
        }
    }

    private fun hideHint() {
        hintMove?.let { move ->
           hintMove = null
            move.wordCharsPos.forEach {
                mGameView?.updateCell(it)
            }
        }
    }

}