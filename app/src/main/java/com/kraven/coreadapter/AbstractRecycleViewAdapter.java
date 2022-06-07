package com.kraven.coreadapter;


import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by hyperlink on 27/7/15.
 */

public abstract class AbstractRecycleViewAdapter<S, T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private List<S> mList;

    public AbstractRecycleViewAdapter(List<S> mList) {
        this.mList = mList;
    }


    public void setmList(List<S> mList) {
        this.mList = mList;
    }

    public List<S> getList() {
        return mList;
    }

    public abstract T onCreateViewHolder(ViewGroup viewGroup, int i);

    public abstract void onBindViewHolder(T t, int i);

    public abstract void onAdded();

    public abstract void onRemoved();

    public void addItem(S mData) {
        mList.add(mData);
        notifyItemInserted(mList.size() - 1);
        onAdded();
    }

    public void updateItem(S mData){
        int index = mList.indexOf(mData);
        mList.set(index,mData);
        notifyItemChanged(mList.indexOf(mData));
    }

    public void addList(S items) {
        mList.add(0, items);
        notifyItemRangeInserted(0,mList.size());
        notifyItemChanged(mList.size());//Only  To update the last top item cause in my case it was the date header so i need to notify it
    }

    public void addUpdateItem(S mData) {
        if (mList.contains(mData)) {
            int index = mList.indexOf(mData);
            mList.remove(index);
            mList.add(index, mData);
            notifyItemChanged(mList.indexOf(mData));
        } else {
            mList.add(mData);
            notifyItemInserted(mList.size() - 1);
            onAdded();
        }
    }

    public void addItem(S mData, int position) {
        mList.add(position, mData);
        notifyItemInserted(position);
        notifyDataSetChanged();
        onAdded();
    }

    public void removeItem(int position) {
        if (position >= mList.size()) return;
        mList.remove(position);
        notifyDataSetChanged();
        onRemoved();
    }

    public void removeItem(S mData) {
        int position = mList.indexOf(mData);
        mList.remove(mData);
        notifyItemRemoved(position);
        onRemoved();
    }


    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }


}
