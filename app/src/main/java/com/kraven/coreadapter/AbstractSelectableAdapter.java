package com.kraven.coreadapter;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import androidx.annotation.IntDef;

/**
 * Created by hlink21 on 10/6/16.
 */
public abstract class AbstractSelectableAdapter<H extends SelectableViewHolder, E extends AbstractSelectableAdapter.Selectable>
        extends AdvanceRecycleViewAdapter<H, E> implements SelectionActionPerformer<E> {

    public static final int SINGLE = 0;
    public static final int MULTI = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SINGLE, MULTI})
    public @interface SelectionMode {
    }

    private E previousSelectedItem = null;
    private int previousSelectedItemIndex = -1;
    @SelectionMode
    private int selectionModes = SINGLE;

    private OnSelectionChangeListener<E> onSelectionChangeListener;

    public AbstractSelectableAdapter(@SelectionMode int selectionModes) {
        this.selectionModes = selectionModes;
    }

    public AbstractSelectableAdapter(ArrayList<E> items, @SelectionMode int selectionModes) {
        super(items);
        this.selectionModes = selectionModes;
    }

    public void addSelectionChangeListener(OnSelectionChangeListener<E> onSelectionChangeListener) {
        this.onSelectionChangeListener = onSelectionChangeListener;
    }

    @SelectionMode
    public int getSelectionModes() {
        return selectionModes;
    }

    @Override
    public void select(int index) {

        if (index < 0)
            index = 0;

        E item = getItem(index);

        if (selectionModes == SINGLE) {
            if (previousSelectedItem != null) {
                previousSelectedItem.setSelected(false);
                notifyItemChanged(previousSelectedItemIndex);
            }
        }

        item.setSelected(true);

        if (onSelectionChangeListener != null)
            onSelectionChangeListener.onSelectionChange(item, true);

        notifyDataSetChanged();

        previousSelectedItem = item;
        previousSelectedItemIndex = index;
    }

    public void select(E e) {
        int i = items.indexOf(e);
        select(i);
    }

    @Override
    public void deSelect(int index) {
        E item = getItem(index);
        item.setSelected(false);
        if (onSelectionChangeListener != null)
            onSelectionChangeListener.onSelectionChange(item, false);
        notifyDataSetChanged();
    }

    public E getPreviousSelectedItem() {
        return previousSelectedItem;
    }

    public int getPreviousSelectedItemIndex() {
        return previousSelectedItemIndex;
    }

    @Override
    public void onBindDataHolder(H holder, int position, E item) {
        onBindSelectedViewHolder(holder, position, item);
        if (previousSelectedItem == null && item.isSelected()) {
            previousSelectedItem = item;
            previousSelectedItemIndex = position;
        }
    }

    public abstract void onBindSelectedViewHolder(H h, int position, E item);

    public interface Selectable {

        void setSelected(boolean selected);

        boolean isSelected();
    }
}
