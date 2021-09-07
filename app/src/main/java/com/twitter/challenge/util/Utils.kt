package com.twitter.challenge.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import kotlin.math.round

@BindingAdapter("srcUrl")
fun ImageView.setSrcUrl(url: String) {
    Glide.with(this).load(url).into(this)
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}