package com.example.easyrecycler

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easyrecycler.databinding.ActivityDemoBinding
import com.example.easyrecycler.mock.MockImage
import com.example.easyrecycler.mock.MockService
import com.example.easyrecycler.model.DemoItemListener
import com.example.easyrecycler.model.DemoItemModel
import com.example.easyrecycler.model.DemoSectionModel
import com.example.easyrecycler.model.ItemType

class DemoActivity : AppCompatActivity() {

  private lateinit var views: ActivityDemoBinding
  private val listener: EasyRecyclerAdapter.ViewHolderListener by lazy {
    DemoItemListener { type: ItemType ->
      Toast.makeText(this@DemoActivity, type.toString(), Toast.LENGTH_SHORT).show()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    views = ActivityDemoBinding.inflate(layoutInflater)
    setContentView(views.root)

    views.demoRecyclerView.buildSet {
      add(
        DemoSectionModel("Base Section")
      )
      add(
        DemoItemModel(R.drawable.ic_android, "Android stuff", ItemType.GOOD),
        DemoItemModel(R.drawable.ic_baseline_arrow_circle_up, "Up and away", ItemType.WORSE),
        DemoItemModel(R.drawable.ic_baseline_category, "Let's categorize", ItemType.BEST),
        listener = listener
      )
      add(
        DemoSectionModel("Only categories")
      )
      add(
        DemoItemModel(R.drawable.ic_baseline_category, "Good stuff", ItemType.GOOD),
        DemoItemModel(R.drawable.ic_baseline_category, "Worse stuff", ItemType.WORSE),
        DemoItemModel(R.drawable.ic_baseline_category, "Best stuff", ItemType.BEST),
        DemoItemModel(R.drawable.ic_baseline_category, "Bad stuff", ItemType.BAD),
        listener = listener
      )
      add(
        DemoSectionModel("Transformed from service")
      )
      addFrom(MockService().getMockResponse(), listener) { serviceModel ->
        DemoItemModel(
          imageResource = serviceModel.mockImage.toDrawable(),
          title = serviceModel.title,
          type = ItemType.valueOf(serviceModel.type)
        )
      }
    }
  }

  private fun MockImage.toDrawable() = when(this) {
    MockImage.ANDROID -> R.drawable.ic_android
    MockImage.ARROW -> R.drawable.ic_baseline_arrow_circle_up
    MockImage.CATEGORY -> R.drawable.ic_baseline_category
  }
}
