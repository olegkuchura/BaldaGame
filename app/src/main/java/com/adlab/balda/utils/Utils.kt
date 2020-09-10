package com.adlab.balda.utils

import android.util.Log

fun Char.isCorrectChar(): Boolean {
    val gameLanguage = "ru"
    return when (gameLanguage) {
        "ru" -> this.toInt() in 0x0410..0x044F || this.toInt() == 0x0451 || this.toInt() == 0x0401
        else -> false
    }
}

fun log(message: String) {
    Log.d("AndroidRuntime", message)
}