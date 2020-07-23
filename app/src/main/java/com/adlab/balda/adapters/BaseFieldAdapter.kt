package com.adlab.balda.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface BaseFieldAdapter {

    fun setOnItemClickListener(listener: OnFieldItemClickListener)

    interface OnFieldItemClickListener {
        fun onItemClick(itemView: View?, position: Int)
        fun onItemLongClick(itemView: View?, position: Int)
        fun onClearLetterClick()
    }

}
