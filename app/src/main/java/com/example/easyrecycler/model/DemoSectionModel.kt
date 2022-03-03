package com.example.easyrecycler.model

import com.example.easyrecycler.R
import com.example.easyrecycler.EasyRecyclerAdapter

class DemoSectionModel(
  val title: String
) : EasyRecyclerAdapter.AbstractHolderModel(R.layout.layout_demo_section)
