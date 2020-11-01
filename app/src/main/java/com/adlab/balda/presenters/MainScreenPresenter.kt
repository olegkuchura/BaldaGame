package com.adlab.balda.presenters

import com.adlab.balda.contracts.MainScreenContract
import com.adlab.balda.database.AppRepository
import com.adlab.balda.enums.GameType
import com.adlab.balda.model.move_search.MoveFinderManager
import com.adlab.balda.utils.WordsLoader
import com.adlab.balda.utils.log
import kotlinx.coroutines.*

class MainScreenPresenter(
        private var viewState: MainScreenContract.View?,
        private val repository: AppRepository,
        private val wordsLoader: WordsLoader
): MainScreenContract.Presenter, CoroutineScope {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main

    private var isDatabaseCreating: Boolean = false

    private var isFirstStart = true

    init {
        viewState?.setPresenter(this)
    }

    override fun start() {
        if (isFirstStart) {
            isFirstStart = false
            loadDB()
        } else {
            when(isDatabaseCreating) {
                true -> viewState?.showInitiation()
                false -> viewState?.showMainButtons()
            }
        }
    }

    private fun loadDB() = launch {
        if (!repository.isAnyWords()) {
            isDatabaseCreating = true
            viewState?.showInitiation()
            withContext(Dispatchers.IO) { wordsLoader.loadFromAssets(repository) }
            isDatabaseCreating = false
        }
        viewState?.showMainButtons()
        initMoveFinder()
    }

    private fun initMoveFinder() = launch {
        if (!MoveFinderManager.inited) {
            val listOfWords = repository.getAllWords()
            withContext(Dispatchers.Default) {
                MoveFinderManager.init(listOfWords.map { it.word })
            }
        }
    }


    override fun setView(view: MainScreenContract.View) {
        viewState = view
        viewState?.setPresenter(this)
    }

    override fun resetView() {
        viewState = null
    }

    override fun cleanup() {
        job.cancel()
    }

    override fun startOneManGameClicked() {
        viewState?.navigateToGameSettings(GameType.SINGLE)
    }

    override fun startMultiplayerGameClicked() {
        viewState?.navigateToGameSettings(GameType.MULTIPLAYER)
    }
}