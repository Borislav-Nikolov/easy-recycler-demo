package com.example.easyrecycler

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.RecyclerView

/**
 * A custom implementation of the [RecyclerView].
 * It creates its own [EasyRecyclerAdapter] implementation and works with the passed
 * [ModelWrapper]s to create [EasyRecyclerAdapter.BindingViewHolder]s.
 *
 * The purpose of this view is to remove the need of adding the adapter to the [RecyclerView]
 * and ease the work of setting [EasyRecyclerAdapter.ViewHolderListener] to a specific type
 * of [EasyRecyclerAdapter.BindingViewHolder].
 */
class EasyRecyclerView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, @AttrRes defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

  private var modelWrappers: MutableList<ModelWrapper>? = null
  private val easyAdapter = object : EasyRecyclerAdapter<EasyRecyclerAdapter.AbstractHolderModel>() {
    override fun createHolder(
      parent: ViewGroup,
      layoutResource: Int
    ) = BindingViewHolder(createBinding(parent, layoutResource)) { position -> extractListener(position) }
  }

  init {
    adapter = easyAdapter
  }

  /**
   * Sets the provided [EasyRecyclerAdapter.AbstractHolderModel]s used by the [easyAdapter] through
   * the provided [ModelWrapper]s.
   *
   * @param modelWrappers [ModelWrapper] varargs.
   */
  fun setModelWrappers(vararg modelWrappers: ModelWrapper) {
    setModelWrappers(modelWrappers.toList())
  }

  /**
   * Sets the list of [EasyRecyclerAdapter.AbstractHolderModel]s used by the [easyAdapter] through
   * the provided [ModelWrapper]s.
   *
   * @param modelWrappers [ModelWrapper] list.
   */
  fun setModelWrappers(modelWrappers: List<ModelWrapper>) {
    this.modelWrappers = modelWrappers.toMutableList()
    easyAdapter.setModels(modelWrappers.flatMap { it.models })
  }

  /**
   * Merges the provided [EasyRecyclerAdapter.AbstractHolderModel]s used by the [easyAdapter] with
   * the old list of models through the provided [ModelWrapper]s.
   *
   * @param modelWrappers [ModelWrapper] varargs.
   */
  fun mergeModelWrappers(vararg modelWrappers: ModelWrapper) {
    mergeModelWrappers(modelWrappers.toList())
  }

  /**
   * Merges the list of [EasyRecyclerAdapter.AbstractHolderModel]s used by the [easyAdapter] with the
   * old list through the provided [ModelWrapper]s.
   *
   * @param modelWrappers [ModelWrapper] list.
   */
  fun mergeModelWrappers(modelWrappers: List<ModelWrapper>) {
    this.modelWrappers = modelWrappers.toMutableList()
    easyAdapter.mergeModels(modelWrappers.flatMap { it.models })
  }

  /**
   * Adds a single [EasyRecyclerAdapter.AbstractHolderModel] and a
   * [EasyRecyclerAdapter.ViewHolderListener] attached to it at the end of the item list.
   */
  fun addEnd(model: EasyRecyclerAdapter.AbstractHolderModel, listener: EasyRecyclerAdapter.ViewHolderListener? = null) {
    modelWrappers?.add(ModelWrapper(model, listener = listener))
    easyAdapter.addEnd(model)
  }

  /**
   * Removes the last item in the list.
   */
  fun removeLast() {
    modelWrappers?.removeLast()
    easyAdapter.removeLast()
  }

  /**
   * Creates a [ModelList] and invokes the provided [build] function on it to provide a way to
   * construct the list of [ModelWrapper]s used by this [EasyRecyclerView] and sets the items anew.
   * @see [ModelList.add]
   *
   * @param build is the function to build the model list with.
   */
  fun buildSet(build: ModelList.() -> Unit) {
    val modelList = ModelList()
    modelList.build()
    setModelWrappers(modelList)
  }

  /**
   * Creates a [ModelList] and invokes the provided [build] function on it to provide a way to
   * construct the list of [ModelWrapper]s used by this [EasyRecyclerView] and merges the new items
   * with the old ones.
   * @see [ModelList.add]
   *
   * @param build is the function to build the model list with.
   */
  fun buildMerge(build: ModelList.() -> Unit) {
    val modelList = ModelList()
    modelList.build()
    mergeModelWrappers(modelList)
  }

  private fun extractListener(position: Int): EasyRecyclerAdapter.ViewHolderListener? {
    var positionInModelWrappers = 0
    modelWrappers?.forEach { wrapper ->
      for (model in wrapper.models) {
        if (positionInModelWrappers == position) {
          return wrapper.listener
        }
        positionInModelWrappers++
      }
    }
    return null
  }

  /**
   * A model class used by the [EasyRecyclerView] to bind [EasyRecyclerAdapter.ViewHolderListener]s
   * to the provided [EasyRecyclerAdapter.AbstractHolderModel]s.
   *
   * @param models [List] of [EasyRecyclerAdapter.AbstractHolderModel].
   * @param listener [EasyRecyclerAdapter.ViewHolderListener] that will be added to the data binding of the
   * [RecyclerView] item that uses the [EasyRecyclerAdapter.AbstractHolderModel] s.
   */
  class ModelWrapper(
    val models: List<EasyRecyclerAdapter.AbstractHolderModel>,
    val listener: EasyRecyclerAdapter.ViewHolderListener? = null
  ) {

    /**
     * Secondary constructor, using varargs instead of [List].
     *
     * @param models [EasyRecyclerAdapter.AbstractHolderModel] varargs.
     * @param listener [EasyRecyclerAdapter.ViewHolderListener] that will be added to the data binding
     * of the [RecyclerView] item that uses the [EasyRecyclerAdapter.AbstractHolderModel]s.
     */
    constructor(
      vararg models: EasyRecyclerAdapter.AbstractHolderModel,
      listener: EasyRecyclerAdapter.ViewHolderListener? = null
    ) : this(models.toList(), listener)
  }

  /**
   * An extension of [ArrayList] that can only contain [ModelWrapper]s.
   */
  class ModelList : ArrayList<ModelWrapper>() {

    /**
     * Uses the provided [EasyRecyclerAdapter.AbstractHolderModel]s and
     * [EasyRecyclerAdapter.ViewHolderListener] to create a [ModelWrapper] and add it to the list.
     *
     * @param models are the [EasyRecyclerAdapter.AbstractHolderModel]s to be used for the [ModelWrapper].
     * @param listener is the [EasyRecyclerAdapter.ViewHolderListener] to be used for the [ModelWrapper].
     */
    fun add(
      vararg models: EasyRecyclerAdapter.AbstractHolderModel,
      listener: EasyRecyclerAdapter.ViewHolderListener? = null
    ) {
      add(ModelWrapper(listOf(*models), listener))
    }

    /**
     * Uses the provided [EasyRecyclerAdapter.AbstractHolderModel]s and
     * [EasyRecyclerAdapter.ViewHolderListener] to create a [ModelWrapper] and add it to the list.
     *
     * @param models are the [EasyRecyclerAdapter.AbstractHolderModel]s to be used for the [ModelWrapper].
     * @param listener is the [EasyRecyclerAdapter.ViewHolderListener] to be used for the [ModelWrapper].
     */
    fun add(
      models: List<EasyRecyclerAdapter.AbstractHolderModel>,
      listener: EasyRecyclerAdapter.ViewHolderListener? = null
    ) {
      add(ModelWrapper(models, listener))
    }

    /**
     * Transforms the provided [initialList] into a list of [EasyRecyclerAdapter.AbstractHolderModel]s through
     * the [transform] function and adds the models to this [ModelList].
     *
     * @param initialList is the list of items that will be transformed into [EasyRecyclerAdapter.AbstractHolderModel]s.
     * @param listener is the [EasyRecyclerAdapter.ViewHolderListener] to be added to the [ModelList].
     * @param transform is the function that transforms [T] into a [EasyRecyclerAdapter.AbstractHolderModel].
     */
    fun <T> addFrom(
      initialList: List<T>,
      listener: EasyRecyclerAdapter.ViewHolderListener? = null,
      transform: (T) -> EasyRecyclerAdapter.AbstractHolderModel
    ) {
      val models = mutableListOf<EasyRecyclerAdapter.AbstractHolderModel>()
      initialList.forEach {
        models.add(transform(it))
      }
      add(models, listener)
    }
  }
}
