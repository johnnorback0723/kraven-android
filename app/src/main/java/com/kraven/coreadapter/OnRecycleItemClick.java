package com.kraven.coreadapter;

import android.view.View;

/**
 * Created by hlink21 on 26/5/16.
 */
public interface OnRecycleItemClick<T> {

    void onClick(T t, View view);
}
