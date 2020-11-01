package com.adlab.balda.contracts

import com.adlab.balda.BasePresenter
import com.adlab.balda.BaseView
import com.adlab.balda.enums.GameType

interface MainScreenContract {

    interface View: BaseView<Presenter> {
        fun showInitiation()
        fun showMainButtons()
        fun navigateToGameSettings(gameType: GameType)
    }

    interface Presenter: BasePresenter {
        fun setView(view: View)
        fun resetView()
        fun cleanup()
        fun startOneManGameClicked()
        fun startMultiplayerGameClicked()
    }

}