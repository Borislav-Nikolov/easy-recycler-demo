package com.example.easyrecycler;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A basic recycler view adapter. Uses data binding. Can have multiple layouts provided via its item models which extend
 * {@link AbstractHolderModel}.
 *
 * @param <M> type argument of an implementation of {@link AbstractHolderModel} by which the layout resource for the
 * item is provided.
 */
public abstract class EasyRecyclerAdapter<M extends EasyRecyclerAdapter.AbstractHolderModel>
  extends RecyclerView.Adapter<EasyRecyclerAdapter.BindingViewHolder> {

  private List<M> models = new ArrayList<>();

  /**
   * Sets the recycler view item models and notifies of the change.
   *
   * @param models the list of the item models to be displayed by the recycler view.
   */
  public void setModels(List<M> models) {
    refreshList(models);
    notifyDataSetChanged();
  }

  /**
   * Merges the new list of models with the current one using {@link DiffUtil}.
   *
   * @param models is the new list of models.
   */
  public void mergeModels(List<M> models) {
    EasyDiffCallback<M> callback = new EasyDiffCallback<>(this.models, models);
    DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
    refreshList(models);
    result.dispatchUpdatesTo(this);
  }

  /**
   * Adds a single model to the end of the item list and updates the recycler.
   */
  public void addEnd(M model) {
    models.add(model);
    notifyItemInserted(models.size() - 1);
  }

  /**
   * Removes the last item from the list and updates the recycler.
   */
  public void removeLast() {
    int lastPosition = models.size() - 1;
    models.remove(lastPosition);
    notifyItemRemoved(lastPosition);
  }

  @Override
  public int getItemViewType(int position) {
    return models.get(position).getLayoutRes();
  }

  @NonNull
  @Override
  public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return createHolder(parent, viewType);
  }

  @Override
  public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
    holder.bindModel(models.get(position), position);
  }

  @Override
  public int getItemCount() {
    return models.size();
  }

  /**
   * Creates the {@link BindingViewHolder}.
   *
   * @param parent The ViewGroup into which the new View will be added after it is bound to
   *               an adapter position.
   * @param layoutResource The layout resource of the new View.
   * @return {@link BindingViewHolder}.
   */
  protected abstract BindingViewHolder createHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResource);

  /**
   * Creates the binding provided to the {@link BindingViewHolder}.
   *
   * @param parent The ViewGroup into which the new View will be added after it is bound to
   *               an adapter position.
   * @param layoutResource The layout resource of the new View.
   * @return {@link ViewDataBinding}
   */
  protected ViewDataBinding createBinding(@NonNull ViewGroup parent, @LayoutRes int layoutResource) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    return DataBindingUtil.inflate(inflater, layoutResource, parent, false);
  }

  private void refreshList(List<M> models) {
    this.models.clear();
    this.models.addAll(models);
  }

  /**
   * An implementation of {@link RecyclerView.ViewHolder} used by {@link EasyRecyclerAdapter}.
   * It receives the {@link ViewDataBinding} from the adapter and has methods to bind a model and a click listener to
   * its layout.
   * It is presumed that the data binding fields will be named
   * "model" for the {@link AbstractHolderModel} implementations
   * and "listener" for the {@link ViewHolderListener} implementations.
   */
  public static final class BindingViewHolder extends RecyclerView.ViewHolder {

    private final ViewDataBinding binding;
    private ListenerProvider listenerProvider;

    /**
     * Constructor.
     *
     * @param binding {@link ViewDataBinding}.
     */
    public BindingViewHolder(ViewDataBinding binding) {
      this(binding, null);
    }

    /**
     * Constructor.
     *
     * @param binding {@link ViewDataBinding}.
     * @param listenerProvider {@link ListenerProvider}.
     */
    public BindingViewHolder(ViewDataBinding binding, ListenerProvider listenerProvider) {
      super(binding.getRoot());
      this.binding = binding;
      this.listenerProvider = listenerProvider;
    }

    private void bindModel(AbstractHolderModel model, int position) {
      binding.setVariable(BR.model, model);
      binding.executePendingBindings();
      if (listenerProvider != null) {
        bindListener(listenerProvider.provideListener(position));
      }
    }

    /**
     * Binds a {@link ViewHolderListener} to the {@link ViewDataBinding}.
     *
     * @param listener {@link ViewHolderListener}.
     */
    public void bindListener(ViewHolderListener listener) {
      if (listener != null) {
        binding.setVariable(BR.listener, listener);
        binding.executePendingBindings();
      }
    }

    /**
     * An interface used to provide the {@link BindingViewHolder} with a {@link ViewHolderListener}.
     */
    public interface ListenerProvider {

      /**
       * Provides the {@link ViewHolderListener} that is going to be used by the {@link BindingViewHolder}.
       *
       * @param position is the position of the recycler view item.
       * @return {@link ViewHolderListener}.
       */
      @Nullable
      ViewHolderListener provideListener(int position);
    }
  }

  /**
   * An abstract class to be extended by the [ScdRecyclerAdapter]'s models.
   */
  public abstract static class AbstractHolderModel {

    @LayoutRes
    private final int layoutRes;

    /**
     * Constructor.
     *
     * @param layoutRes the layout resource used to tell the recycler view adapter which layout to use.
     */
    protected AbstractHolderModel(@LayoutRes int layoutRes) {
      this.layoutRes = layoutRes;
    }

    /**
     * Provides the layout resource used by the {@link AbstractHolderModel}.
     *
     * @return the layout resource.
     */
    public int getLayoutRes() {
      return layoutRes;
    }

    /**
     * Provides an identifier object that will be used for optimizing the item list changes through
     * {@link DiffUtil}. Override in derived classes to provide the desired identifier.
     *
     * @implNote The {@link Object#equals(Object)} method is used in {@link EasyDiffCallback}
     * when comparing the identifiers with each other.
     *
     * @return an {@link Object} that will act as an identifier for the {@link EasyDiffCallback}.
     * Returns the {@link AbstractHolderModel} by default.
     */
    public Object identifier() {
      return this;
    }
  }

  private static class EasyDiffCallback<M extends AbstractHolderModel> extends DiffUtil.Callback {

    private final List<M> oldList;
    private final List<M> newList;

    private EasyDiffCallback(List<M> oldList, List<M> newList) {
      this.oldList = oldList;
      this.newList = newList;
    }

    @Override
    public int getOldListSize() {
      return oldList.size();
    }

    @Override
    public int getNewListSize() {
      return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
      return areSame(oldItemPosition, newItemPosition, (oldItem, newItem) -> {
        Object oldItemId = oldItem.identifier();
        Object newItemId = newItem.identifier();
        if (oldItemId == null || newItemId == null) {
          return false;
        }
        return oldItemId.equals(newItemId);
      });
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
      return areSame(oldItemPosition, newItemPosition, Object::equals);
    }

    private boolean areSame(int oldItemPosition, int newItemPosition, ItemDiffComparator<M> comparator) {
      Pair<M, M> itemPair = getItemPair(oldItemPosition, newItemPosition);
      if (itemPair == null || itemPair.first == null || itemPair.second == null) {
        return false;
      }
      return comparator.areSame(itemPair.first, itemPair.second);
    }

    @Nullable
    private Pair<M, M> getItemPair(int oldItemPosition, int newItemPosition) {
      if (oldList == null || newList == null) {
        return null;
      }
      M oldItem = oldList.get(oldItemPosition);
      M newItem = newList.get(newItemPosition);
      if (oldItem == null || newItem == null) {
        return null;
      }
      return new Pair<>(oldItem, newItem);
    }

    private interface ItemDiffComparator<M extends AbstractHolderModel> {

      boolean areSame(M oldItem, M newItem);
    }
  }

  /**
   * A marker interface used for the click listeners provided to the [ScdRecyclerAdapter].
   */
  public interface ViewHolderListener {
  }
}
