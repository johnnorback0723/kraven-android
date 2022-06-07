package com.kraven.coreadapter;

/**
 * Created by hlink21 on 10/6/16.
 */
public interface SelectionActionPerformer<E extends AbstractSelectableAdapter.Selectable> {

    E getItem(int index);

    void select(int index);

    void deSelect(int index);
}
