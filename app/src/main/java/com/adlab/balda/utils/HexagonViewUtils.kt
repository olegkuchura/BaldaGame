package com.adlab.balda.utils

import kotlin.math.sqrt

fun innerRadius(height: Int): Int {
    return (sqrt(3.0) * height.toDouble() / 2.0).toInt()
}