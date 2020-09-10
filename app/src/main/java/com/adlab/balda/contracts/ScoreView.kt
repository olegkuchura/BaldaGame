package com.adlab.balda.contracts

interface ScoreView {

    fun showScore(playerName: String, score: Int, isCurrent: Boolean)

}