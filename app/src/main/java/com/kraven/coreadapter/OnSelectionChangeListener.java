package com.kraven.coreadapter;


import androidx.annotation.UiThread;

/**
 * Created by hlink21 on 10/6/16.
 */
public interface OnSelectionChangeListener<T> {

    @UiThread
    void onSelectionChange(T t, boolean isSelected);
}
