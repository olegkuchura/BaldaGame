package com.adlab.balda.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.adlab.balda.R


class FieldCellView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {

    companion object {
        private val STATE_MOVE = intArrayOf(R.attr.state_move)
    }

    var isShowingMove: Boolean = false
        set(value) {
            if (value != field) {
                field = value
                refreshDrawableState()
            }
        }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isShowingMove) {
            mergeDrawableStates(drawableState, STATE_MOVE)
        }
        return drawableState
    }
}