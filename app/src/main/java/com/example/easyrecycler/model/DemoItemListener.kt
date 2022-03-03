package com.example.easyrecycler.model

import com.example.easyrecycler.EasyRecyclerAdapter

fun interface DemoItemListener : EasyRecyclerAdapter.ViewHolderListener {

  fun toastTypeInfo(type: ItemType)
}
