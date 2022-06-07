package com.kraven.coreadapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable divider;
    private int marginLeft = 0;
    private int marginRight = 0;
    private boolean showLast = false;

    /**
     * Default divider will be used
     */
    public DividerItemDecoration(Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        divider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
    }

    /**
     * Custom divider will be used
     */
    public DividerItemDecoration(Context context, int resId, @DimenRes int marginLeft, @DimenRes int marginRight) {
        this.divider = ContextCompat.getDrawable(context, resId);
        this.marginLeft = (int) context.getResources().getDimension(marginLeft);
        this.marginRight = (int) context.getResources().getDimension(marginRight);
    }

    public DividerItemDecoration(Context context, int resId) {
        this.divider = ContextCompat.getDrawable(context, resId);
        this.marginLeft = 0;
        this.marginRight = 0;
    }

    public DividerItemDecoration(Context context, int resId, boolean showLast) {
        this.divider = ContextCompat.getDrawable(context, resId);
        this.marginLeft = 0;
        this.marginRight = 0;
        this.showLast = showLast;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + marginLeft;
        int right = parent.getWidth() - parent.getPaddingRight() - marginRight;

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }

        if (showLast) {
            View child = parent.getChildAt(parent.getChildCount() - 1);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}