package com.adlab.balda.presenters

import com.adlab.balda.DEFAULT_FIELD_SIZE
import com.adlab.balda.DEFAULT_FIELD_TYPE
import com.adlab.balda.contracts.GameSettingsContract
import com.adlab.balda.database.WordsDataSource.GetWordCallback
import com.adlab.balda.database.WordsRepository
import com.adlab.balda.enums.FieldSizeType
import com.adlab.balda.enums.FieldType

class GameSettingsPresenter(
        private val repository: WordsRepository,
        private var viewState: GameSettingsContract.View?
) : GameSettingsContract.Presenter {

    init {
        viewState?.setPresenter(this)
    }

    private var mCurrentWord: String = ""
    private var mIsCurrentWordExist = false
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

    override fun start() {
        if (isFirstStart) {
            isFirstStart = false
            generateRandomWord()
        }
        viewState?.updateFiledSize(mFieldSize, false, true)
    }

    override fun initWordChanged(newWord: String) {
        mCurrentWord = newWord
        if (mCurrentWord.isEmpty()) {
            mIsCurrentWordExist = false
            viewState?.showEmptyWordError()
            viewState?.showNonAppropriateWordLength(mWordLength)
            viewState?.setStartGameEnabled(false)
            return
        }
        if (mCurrentWord.length == mWordLength) {
            viewState?.hideNonAppropriateWordLength()
        } else {
            viewState?.showNonAppropriateWordLength(mWordLength)
        }
        repository.isWordExist(mCurrentWord) { isWordExist ->
            mIsCurrentWordExist = isWordExist
            if (mIsCurrentWordExist) {
                viewState?.hideInitWordError()
                viewState?.setStartGameEnabled(mCurrentWord.length == mWordLength )
            } else {
                viewState?.showNonExistentWordError()
                viewState?.setStartGameEnabled(false)
            }
        }
    }

    override fun generateRandomWord() {
        repository.getRandomWord(mWordLength, object : GetWordCallback {
            override fun onWordLoaded(word: String) {
                mCurrentWord = word
                mIsCurrentWordExist = true
                viewState?.setInitWord(mCurrentWord)
                viewState?.hideInitWordError()
                viewState?.setStartGameEnabled(true)
            }

            override fun onDataNotAvailable() {}
        })
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

    override fun startGame() {
        viewState?.navigateToGameScreen(mCurrentWord, mFieldSize, mFieldType)
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