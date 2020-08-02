package com.adlab.balda.utils

import android.content.Context
import android.util.TypedValue

fun Context.dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics).toInt()