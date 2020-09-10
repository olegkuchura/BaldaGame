package com.adlab.balda.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adlab.balda.R
import com.adlab.balda.contracts.GameSettingsContract
import com.google.android.material.chip.Chip

class PlayersAdapter(
        private val presenter: GameSettingsContract.Presenter
): RecyclerView.Adapter<PlayersAdapter.PlayersViewHolder>() {

    var playersCount: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return PlayersViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        presenter.bindPlayer(holder, position)
    }

    override fun getItemCount(): Int = playersCount

    inner class PlayersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PlayerView {
        private val playerNameView: Chip = itemView.findViewById(R.id.ch_player_name)
        init {
            playerNameView.setOnCloseIconClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    presenter.onDeletePlayerClicked(adapterPosition)
            }
        }

        override fun setPlayerName(playerName: String) {
            playerNameView.text = playerName
        }
    }

    interface PlayerView {
        fun setPlayerName(playerName: String)
    }
}