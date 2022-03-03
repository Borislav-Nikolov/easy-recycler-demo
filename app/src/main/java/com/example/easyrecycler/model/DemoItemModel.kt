package com.example.easyrecycler.model

import androidx.annotation.DrawableRes
import com.example.easyrecycler.R
import com.example.easyrecycler.EasyRecyclerAdapter

data class DemoItemModel(
  @DrawableRes val imageResource: Int,
  val title: String,
  val type: ItemType
) : EasyRecyclerAdapter.AbstractHolderModel(R.layout.layout_demo_item)
