package com.example.easyrecycler.util

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter

@BindingAdapter("imageResource")
fun ImageView.setImageFromResource(@DrawableRes resource: Int) {
  setImageResource(resource)
}
