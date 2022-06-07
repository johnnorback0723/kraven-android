package com.kraven.coreadapter;

import android.view.View;

public class SelectableViewHolder<E extends AbstractSelectableAdapter.Selectable> extends BaseHolder<E> {

    SelectionActionPerformer<E> actionPerformer;

    public SelectableViewHolder(View itemView, SelectionActionPerformer<E> actionPerformer) {
        super(itemView);
        this.actionPerformer = actionPerformer;
    }

    public SelectableViewHolder(View itemView, SelectionActionPerformer<E> actionPerformer, OnRecycleItemClick<E> onRecycleItemClick) {
        super(itemView, onRecycleItemClick);
        this.actionPerformer = actionPerformer;
    }

    public E getCurrentItem() {
        return actionPerformer.getItem(getLayoutPosition());
    }

    public void select() {
        actionPerformer.select(getLayoutPosition());
    }

    public void deSelect() {
        actionPerformer.deSelect(getLayoutPosition());
    }
}