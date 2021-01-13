package com.adlab.balda.utils

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter

@BindingAdapter("app:imageRes")
fun setImage(view: ImageView, @DrawableRes res: Int) {
    view.setImageResource(res)
}