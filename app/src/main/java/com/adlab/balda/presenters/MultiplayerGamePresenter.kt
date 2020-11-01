package com.adlab.balda.presenters

import android.os.CountDownTimer
import com.adlab.balda.contracts.CellView
import com.adlab.balda.contracts.MultiplayerGameContract
import com.adlab.balda.contracts.ScoreView
import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType
import com.adlab.balda.enums.GameMessageType
import com.adlab.balda.model.GameLab
import com.adlab.balda.model.MultiplayerGame
import com.adlab.balda.model.Timer
import com.adlab.balda.model.view_field.AbstractViewField
import com.adlab.balda.model.view_field.ClassicViewField
import com.adlab.balda.model.view_field.HexagonViewField
import com.adlab.balda.utils.isCorrectChar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MultiplayerGamePresenter(
        private var mGameView: MultiplayerGameContract.View?
): MultiplayerGameContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    private val game: MultiplayerGame = GameLab.getInstance().multiplayerGame

    private val field: AbstractViewField =
            getViewFieldByType(game.fieldType, game.getCopyOfCells(), game.fieldSize)

    private val timer = Timer(totalValue = (1000 * 60 * 3) + 500)
    private var countdownTimer: CountDownTimer? = null

    private var isMoveChecking = false

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

    override fun cleanup() {
        job.cancel()
    }

    override fun onScreenShown() {
        if (timer.isRun) startCountdownTimer()
    }

    override fun onScreenHidden() {
        countdownTimer?.cancel()
        countdownTimer = null
    }

    override fun onShowUsedWordsClicked() {
        mGameView?.showUsedWords()
    }

    override fun start() {
        mGameView?.let { gameView ->
            gameView.showField(game.fieldSize.intValue(), game.fieldSize.intValue(), game.fieldType, game.fieldSize)
            gameView.showScore(game.playersCount)
            gameView.updateUsedWords(game.usedWords)
            if (field.activeCellNumbers.isNotEmpty()) {
                gameView.activateActionMode()
                gameView.updateActivatedLetterSequence(field.enteredLetterSequence)
            }
            if (field.hasSelectedCell) {
                gameView.updateCell(field.clearSelection())
            }
            gameView.updateTimer(timer.currentValue, true)
            if (game.isFinished) showWinner()
        }
    }

    override fun bindCell(cellView: CellView, cellNumber: Int) {
        if (!timer.isRun && !game.isFinished) {
            cellView.showLetter(' ')
            cellView.hideClearLetterButton()
            cellView.resetState()
            return
        }
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
            val wasActiveModeActivated = field.activeCellNumbers.isNotEmpty()
            val changedItems = field.toggleActiveModeForCell(cellNumber)
            for (item in changedItems) {
                gameView.updateCell(item)
            }
            if (field.activeCellNumbers.isEmpty() && wasActiveModeActivated) {
                gameView.deactivateActionMode()
            } else if (field.activeCellNumbers.isNotEmpty() && !wasActiveModeActivated) {
                gameView.activateActionMode()
                gameView.updateTimer(timer.currentValue)
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
            clearActiveNumbersAndUpdate()
            gameView.updateCell(field.enteredCellNumber)
        }
    }

    override fun confirmWord() {
        if (field.activeCellsContainEnteredLetter()) {
            isMoveChecking = true
            tryToMakeMove()
        } else {
            mGameView?.showMessage(GameMessageType.MUST_CONTAIN_NEW_LETTER)
        }
    }

    private fun tryToMakeMove() = launch {
        val word = field.enteredLetterSequence
        game.makeMove(field.enteredCellNumber, field.enteredLetter,
                field.activeCellNumbersArray, object : MultiplayerGame.MakeMoveCallback {
            override fun onMoveAccepted() {
                isMoveChecking = false
                updateViewAfterSuccessfulMove(word)
                countdownTimer?.cancel()
                countdownTimer = null
                timer.reset()
                startCountdownTimer()
            }

            override fun onWordIsNotExist() {
                isMoveChecking = false
                mGameView?.showMessage(GameMessageType.NO_SUCH_WORD)
                if (countdownTimer == null) onTimerOver()
            }

            override fun onWordIsAlreadyUsed() {
                isMoveChecking = false
                mGameView?.showMessage(GameMessageType.WORD_ALREADY_USED)
                if (countdownTimer == null) onTimerOver()
            }

            override fun onGameFinished() {
                isMoveChecking = false
                updateViewAfterSuccessfulMove(word)
                countdownTimer?.cancel()
                countdownTimer = null
                timer.isRun = false
                showWinner()
            }
        })
    }

    override fun exitGame() {
        mGameView?.showGameExit()
    }

    override fun finishGame() {
        countdownTimer?.cancel()
        countdownTimer = null
        timer.isRun = false
        game.fillInEntireField()
        for (index in field.items.indices) {
            if (field.items[index] == ' ') field.items[index] = 'А'
        }
        mGameView?.updateField()
        showWinner()
    }

    override fun bindScore(view: ScoreView, position: Int) {
        view.showScore(game.getPlayerName(position), game.getPlayerScore(position),
                game.curPlayerIndex == position)
    }
    override fun pauseGameClicked() {
        if (!timer.isRun) return
        countdownTimer?.cancel()
        countdownTimer = null
        timer.isRun = false
        mGameView?.let { gameView ->
            if(field.hasSelectedCell) gameView.hideKeyboard()
            gameView.updateField()
            gameView.updateTimer(timer.currentValue, true)
            gameView.showPause()
        }
    }

    override fun resumeGameClicked() {
        startCountdownTimer()
        mGameView?.updateField()
    }

    private fun startCountdownTimer() {
        if (countdownTimer != null) { return }
        countdownTimer = RoundCountDownTimer(timer.currentValue, 500).start()
        timer.isRun = true
    }

    private fun onTimerOver() {
        if (!isMoveChecking) {
            game.goToNextPlayer()
            timer.reset()
            startCountdownTimer()
            mGameView?.let { gameView ->
                gameView.updateCell(field.clearEnteredLetter())
                gameView.updateCell(field.clearSelection())
                gameView.hideKeyboard()
                clearActiveNumbersAndUpdate()
                gameView.deactivateActionMode()
                gameView.updatePlayersAndScore()
                gameView.hideUsedWords()
                gameView.showMessage(GameMessageType.TIME_OVER)
            }
        } else {
            timer.currentValue = 0
            mGameView?.updateTimer(timer.currentValue , true)
        }
    }

    private fun  clearActiveNumbersAndUpdate() {
        mGameView?.let { gameView ->
            val activeCells = field.activeCellNumbersArray
            field.activeCellNumbers.clear()
            activeCells.forEach { gameView.updateCell(it) }
        }
    }

    private fun updateViewAfterSuccessfulMove(enteredWord: String) {
        mGameView?.let { gameView ->
            field.items = game.getCopyOfCells()
            field.clearEnteredLetter()
            clearActiveNumbersAndUpdate()
            gameView.deactivateActionMode()
            gameView.updatePlayersAndScore()
            //todo score animation
            //gameView.showScoreAnimation(player!!.score -p oldScore)
            gameView.showSuccessfulMoveMessage(enteredWord)
            gameView.updateUsedWords(game.usedWords)
        }
    }

    private fun showWinner() {
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

    private fun getViewFieldByType(fieldType: FieldType, items: CharArray, fieldSize: FieldSizeType): AbstractViewField {
        return if (fieldType === FieldType.SQUARE) {
            ClassicViewField(items, fieldSize)
        } else {
            HexagonViewField(items, fieldSize)
        }
    }


    private inner class RoundCountDownTimer(startValue: Long, interval: Long): CountDownTimer(startValue, interval) {
        override fun onTick(time: Long) {
            timer.currentValue = time
            mGameView?.updateTimer(timer.currentValue)
        }
        override fun onFinish() {
            countdownTimer = null
            onTimerOver()
        }
    }

}