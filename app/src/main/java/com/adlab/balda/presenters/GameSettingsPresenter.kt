package com.adlab.balda.presenters

import com.adlab.balda.DEFAULT_FIELD_SIZE
import com.adlab.balda.DEFAULT_FIELD_TYPE
import com.adlab.balda.TEST_MODE
import com.adlab.balda.adapters.PlayersAdapter
import com.adlab.balda.contracts.GameSettingsContract
import com.adlab.balda.database.AppRepository
import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType
import com.adlab.balda.enums.GameType
import com.adlab.balda.model.GameLab
import com.adlab.balda.model.GamePlayer
import com.adlab.balda.model.move_search.MoveFinderManager
import kotlinx.coroutines.*

class GameSettingsPresenter(
        private var viewState: GameSettingsContract.View?,
        private val repository: AppRepository
) : GameSettingsContract.Presenter, CoroutineScope {

    companion object {
        private const val MAX_PLAYERS_COUNT = 5
        private const val MIN_PLAYERS_COUNT = 2
    }

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    init {
        viewState?.setPresenter(this)
    }
    private lateinit var mGameType: GameType
    private var mCurrentWord: String = ""
    private var mIsCurrentWordExist = false
    private val mPlayers = ArrayList<GamePlayer>()
            .apply { if (TEST_MODE) { add(GamePlayer("Rozmarin")); add(GamePlayer("Roman"))
                    add(GamePlayer("Marina")); add(GamePlayer("Jackson")) }}
    private var mFieldSize = DEFAULT_FIELD_SIZE
    private var mFieldType = DEFAULT_FIELD_TYPE
    private val mWordLength: Int
        get() = mFieldSize.intValue() - if (mFieldType == FieldType.HEXAGON &&
                    (mFieldSize == FieldSizeType.SMALL || mFieldSize == FieldSizeType.LARGE)) 1 else 0

    private var isFirstStart = true

    override fun setView(view: GameSettingsContract.View) {
        viewState = view
        viewState?.setPresenter(this)
    }

    override fun resetView() {
        viewState = null
    }

    override fun cleanup() {
        job.cancel()
    }

    override fun start() {}

    override fun start(gameType: GameType) {
        if (isFirstStart) {
            isFirstStart = false
            generateRandomWord()
        }
        viewState?.updateFiledSize(mFieldSize, false, true)
        mGameType = gameType
        when(mGameType) {
            GameType.SINGLE -> { viewState?.showPlayerNamesBlock(false) }
            GameType.MULTIPLAYER -> { viewState?.showPlayerNamesBlock(true) }
        }
        viewState?.updatePlayers(mPlayers.size)
    }

    override fun initWordChanged(newWord: String) {
        launch {
            mCurrentWord = newWord
            if (mCurrentWord.isEmpty()) {
                mIsCurrentWordExist = false
                viewState?.showEmptyWordError()
                viewState?.showNonAppropriateWordLength(mWordLength)
                viewState?.setStartGameEnabled(false)
                return@launch
            }
            if (mCurrentWord.length == mWordLength) {
                viewState?.hideNonAppropriateWordLength()
            } else {
                viewState?.showNonAppropriateWordLength(mWordLength)
            }
            mIsCurrentWordExist = repository.isWordExist(mCurrentWord)
            if (mIsCurrentWordExist) {
                viewState?.hideInitWordError()
                viewState?.setStartGameEnabled(mCurrentWord.length == mWordLength)
            } else {
                viewState?.showNonExistentWordError()
                viewState?.setStartGameEnabled(false)
            }
        }
    }

    override fun generateRandomWord() {
        loadRandomWord()
    }

    private fun loadRandomWord() = launch {
        mCurrentWord = repository.getRandomWord(mWordLength).word
        mIsCurrentWordExist = true
        viewState?.let { view ->
            view.setInitWord(mCurrentWord)
            view.hideInitWordError()
            view.setStartGameEnabled(true)
        }
    }

    override fun bindPlayer(playerView: PlayersAdapter.PlayerView, position: Int) {
        playerView.setPlayerName(mPlayers[position].nickname)
    }

    override fun onDeletePlayerClicked(position: Int) {
        mPlayers.removeAt(position)
        viewState?.playerDeleted(position)
    }

    override fun onAddPlayerClicked(playerName: String) {
        val name = playerName.trim{ it == ' '}
        if (name.isEmpty()) return
        if (mPlayers.size >= MAX_PLAYERS_COUNT) {
            viewState?.showPlayersCountErrorMax(MAX_PLAYERS_COUNT)
            return
        }
        if (mPlayers.any { it.nickname == playerName}) {
            viewState?.showAlreadyUsedNicknameError()
            return
        }
        if(name.all{ it.isLetterOrDigit() || it == '_' ||  it == ' '}) {
            mPlayers.add(GamePlayer(playerName))
            viewState?.let { viewState ->
                viewState.playerAdded()
                viewState.setPlayerEdit("")
            }
        } else {
            viewState?.showNicknameError()
        }
    }

    override fun increaseFieldSize() {
        mFieldSize = mFieldSize.increase()
        if (mFieldSize == FieldSizeType.maxValue()) {
            viewState?.setIncreaseFieldSizeEnabled(false)
        } else {
            viewState?.setReduceFieldSizeEnabled(true)
        }
        setUpUIAfterChangeFieldSize(true)
    }

    override fun reduceFieldSize() {
        mFieldSize = mFieldSize.reduce()
        if (mFieldSize == FieldSizeType.minValue()) {
            viewState?.setReduceFieldSizeEnabled(false)
        } else {
            viewState?.setIncreaseFieldSizeEnabled(true)
        }
        setUpUIAfterChangeFieldSize(false)
    }

    override fun fieldTypeChanged(fieldType: FieldType) {
        mFieldType = fieldType
        if (mCurrentWord.length == mWordLength && mIsCurrentWordExist) {
            viewState?.setStartGameEnabled(true)
        } else {
            generateRandomWord()
        }
    }

    override fun startGameClicked() {
        when(mGameType) {
            GameType.SINGLE -> { createSingleGame() }
            GameType.MULTIPLAYER -> {
                if (mPlayers.size < MIN_PLAYERS_COUNT) {
                    viewState?.showPlayersCountErrorMin(MIN_PLAYERS_COUNT)
                    return
                }
                createMultiplayerGame()
            }
        }
        viewState?.navigateToGameScreen(mGameType)
    }

    private fun createMultiplayerGame() {
        GameLab.getInstance().createMultiplayerGame(mCurrentWord, mFieldSize, mFieldType, mPlayers)
    }

    private fun createSingleGame() {
        GameLab.getInstance().createGame(mCurrentWord, mFieldSize, mFieldType, GamePlayer())
    }

    private fun setUpUIAfterChangeFieldSize(biggerValue: Boolean) {
        viewState?.updateFiledSize(mFieldSize, true, biggerValue)
        if (mCurrentWord.length == mWordLength && mIsCurrentWordExist) {
            viewState?.setStartGameEnabled(true)
            viewState?.hideNonAppropriateWordLength()
        } else {
            generateRandomWord()
        }
    }

}