package com.kraven.coreadapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hlink21 on 20/7/16.
 */
public abstract class AdvanceRecycleViewAdapter<H extends BaseHolder, E>
        extends RecyclerView.Adapter<BaseHolder> implements HasItem<E> {

    protected ArrayList<E> items;
    protected boolean isNoData = false;
    protected boolean isLoading = true;
    protected String errorMessage = "";
    protected static int MAX_ITEM_PER_PAGE = 10;

    public AdvanceRecycleViewAdapter() {
    }

    public AdvanceRecycleViewAdapter(ArrayList<E> items) {
        this.items = items;
    }

    public void setItems(List<E> items) {
        if (this.items == null)
            this.items = new ArrayList<>();
        else
            this.items.clear();
        this.items.addAll(items);
        isLoading = false;
        notifyDataSetChanged();
    }

    public void setItems(List<E> items, int page) {
        isLoading = true;
        if (page == 1) {
            if (this.items == null)
                this.items = new ArrayList<>();
            else
                this.items.clear();

            this.items.addAll(items);

            if (this.items.size() < MAX_ITEM_PER_PAGE)
                isLoading = false;
        } else {
            if (this.items != null) {
                if (items != null && items.size() > 0) {
                    this.items.addAll(items);
                    if (items.size() < MAX_ITEM_PER_PAGE)
                        isLoading = false;
                } else {
                    isLoading = false;
                }
            } else {
                this.items = new ArrayList<>();
            }
        }
        notifyDataSetChanged();
    }

    public void removeItem(E e) {
        int i = items.indexOf(e);
        items.remove(e);
        notifyItemRemoved(i);
        notifyItemRangeChanged(i, items.size());
    }


    public void addItem(E e) {
        if (items == null) {
            items = new ArrayList<>();
            isLoading = false;
        }
        items.add(e);
        notifyItemInserted(items.size());
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(E e, int position) {
        if (items == null) {
            items = new ArrayList<>();
            isLoading = false;
            items.add(e);
            notifyDataSetChanged();
        } else {
            items.add(position, e);
            notifyItemInserted(position);
        }
    }

    public void updateItem(E e, int position) {
        if (items != null) {
            // int index = items.indexOf(e);

            // DebugLog.d("Hlink","index = "+position);
            if (position > -1) {
                //DebugLog.d("Hlink","if = "+position);
                items.set(position, e);
                notifyDataSetChanged();
            } else {
                Log.d("Hlink", "else = " + position);
            }
        }
    }

    public void clearAllItem() {
        if (items != null) {
            items.clear();
            isLoading = true;
            notifyDataSetChanged();
        }
    }

    public void stopLoader() {
        isLoading = false;
        notifyDataSetChanged();
    }

    public List<E> getItems() {
        if (this.items == null)
            return new ArrayList<>();
        else
            return items;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        isLoading = false;
        isNoData = true;
        notifyDataSetChanged();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        BaseHolder baseHolder;

        if (viewType == 333)
            baseHolder = createLoadingHolder(parent, viewType);
        else if (viewType == 111)
            baseHolder = createNoDataHolder(parent, viewType);
        else
            baseHolder = createDataHolder(parent, viewType);

        baseHolder.setHasItem(this);

        return baseHolder;
    }

    public NoDataHolder createNoDataHolder(ViewGroup parent, int viewType) {
        return new NoDataHolder(new RelativeLayout(parent.getContext()));
    }

    public LoadingHolder createLoadingHolder(ViewGroup parent, int viewType) {
        return new LoadingHolder(new RelativeLayout(parent.getContext()));
    }

    public abstract H createDataHolder(ViewGroup parent, int viewType);

    @Override
    public int getItemViewType(int position) {

        if (isNoData && isLoading)
            return 333;
        else if (!isLoading && isNoData)
            return 111;
        else if (isLoading && items.size() == position)
            return 333;

        return getViewType(position);
    }

    public int getViewType(int position) {
        return 222;
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType != 111 && itemViewType != 333) {
            E item = getItem(position);
            // actual view
            onBindDataHolder((H) holder, position, item);
        }
        if (itemViewType == 111 && holder instanceof NoDataHolder) {
            ((NoDataHolder) holder).setErrorText(errorMessage);
        }
    }

    public abstract void onBindDataHolder(H holder, int position, E item);

    @Override
    public final E getItem(int position) {
        if (isLoading && items.size() == position) {
            int i = position - 1;
            i = Math.max(i, 0);
            return items.get(i);
        }
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        isNoData = items == null || items.isEmpty();
        if (isNoData) {
            return 1;
        } else if (isLoading) {
            return items.size() + 1;
        } else {
            return items.size();
        }
    }


    protected View makeView(ViewGroup parent, @LayoutRes int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }
}