package com.adlab.balda.adapters

import android.view.View

interface BaseFieldAdapter {

    fun setOnItemClickListener(listener: OnFieldItemClickListener)

    interface OnFieldItemClickListener {
        fun onItemClick(itemView: View, position: Int)
        fun onItemLongClick(itemView: View, position: Int)
        fun onClearLetterClick()
    }

}
