package com.adlab.balda.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adlab.balda.R
import com.adlab.balda.contracts.MultiplayerGameContract
import com.adlab.balda.contracts.ScoreView
import com.google.android.material.chip.Chip

class ScoreAdapter(
        private val presenter: MultiplayerGameContract.Presenter,
        private val playersCount: Int
): RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    override fun getItemCount(): Int = playersCount

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score_chip, parent, false)

        return ScoreViewHolder(view as Chip)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        presenter.bindScore(holder, position)
    }

    class ScoreViewHolder(itemView: Chip): RecyclerView.ViewHolder(itemView), ScoreView {

        override fun showScore(playerName: String, score: Int, isCurrent: Boolean) {
            with(itemView as Chip) {
                text = itemView.resources.getString(R.string.score, playerName, score)
                isSelected = isCurrent
            }
        }

    }
}