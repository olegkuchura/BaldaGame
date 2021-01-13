package com.adlab.balda.model

import androidx.annotation.DrawableRes

data class Rule(
        val title: String,
        val description: String,
        @DrawableRes val imageRes: Int
)