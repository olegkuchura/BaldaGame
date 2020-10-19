package com.adlab.balda.model

class Timer(
        val totalValue: Long,
        var currentValue: Long = totalValue,
        var isRun: Boolean = true
) {
    fun reset() {
        currentValue = totalValue
    }
}