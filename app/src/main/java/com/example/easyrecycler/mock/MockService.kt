package com.example.easyrecycler.mock

class MockService {

  fun getMockResponse() =
    listOf(
      MockServiceModel("From service 1", MockImage.CATEGORY, "BAD"),
      MockServiceModel("From service 2", MockImage.ANDROID, "WORSE"),
      MockServiceModel("From service 3", MockImage.ARROW, "GOOD"),
      MockServiceModel("From service 4", MockImage.ANDROID, "BAD"),
      MockServiceModel("From service 5", MockImage.CATEGORY, "BEST"),
      MockServiceModel("From service 6", MockImage.ANDROID, "BAD"),
      MockServiceModel("From service 7", MockImage.ARROW, "BAD"),
      MockServiceModel("From service 8", MockImage.CATEGORY, "BEST"),
      MockServiceModel("From service 9", MockImage.ARROW, "BAD"),
      MockServiceModel("From service 10", MockImage.ANDROID, "BEST"),
      MockServiceModel("From service 11", MockImage.CATEGORY, "BAD"),
      MockServiceModel("From service 12", MockImage.ANDROID, "WORSE"),
    )
}

data class MockServiceModel(
  val title: String,
  val mockImage: MockImage,
  val type: String
)

enum class MockImage {
  ANDROID, ARROW, CATEGORY
}
