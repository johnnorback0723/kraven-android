package com.kraven.coreadapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class GridDividerItemDecoration2 extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private int mOrientation;

    private Drawable divider;
    private int marginLeft = 0;
    private int marginRight = 0;
    private int gridCount = 2;

    /**
     * Default divider will be used
     */
    public GridDividerItemDecoration2(Context context, int orientation) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        this.divider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    /**
     * Custom divider will be used
     */
    public GridDividerItemDecoration2(Context context, @DrawableRes int resId, int gridCount,
                                      @DimenRes int marginLeft, @DimenRes int marginRight) {
        this.divider = ContextCompat.getDrawable(context, resId);
        this.gridCount = gridCount;
        this.marginLeft = (int) context.getResources().getDimension(marginLeft);
        this.marginRight = (int) context.getResources().getDimension(marginRight);
    }

    public GridDividerItemDecoration2(Context context, int orientation, @DrawableRes int resId) {
        this.divider = ContextCompat.getDrawable(context, resId);
        this.marginLeft = 0;
        this.marginRight = 0;
    }

    public GridDividerItemDecoration2(int gridCount, Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        this.divider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
        this.gridCount = gridCount;
        this.marginLeft = 0;
        this.marginRight = 0;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

        /*int left = parent.getPaddingLeft() + marginLeft;
        int right = parent.getWidth() - parent.getPaddingRight() - marginRight;

        int childCount = parent.getChildCount();
        if (childCount > 1) {
            for (int i = 0; i < childCount; i++) {

                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                if (i % gridCount == 0) {

                    int top = child.getBottom() + params.bottomMargin;
                    int bottom = top + divider.getIntrinsicHeight();

                    divider.setBounds(left, top, right, bottom);
                    divider.draw(c);
                } else {

                    int top = child.getBottom() + params.bottomMargin;
                    int bottom = top + divider.getIntrinsicHeight();
                    int left1 = child.getRight() + params.rightMargin;
                    int right1 = left1 + divider.getIntrinsicWidth();

                    divider.setBounds(left1, top, right1, bottom);
                    divider.draw(c);
                }
            }
        }*/
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, divider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, divider.getIntrinsicWidth(), 0);
        }
    }
}