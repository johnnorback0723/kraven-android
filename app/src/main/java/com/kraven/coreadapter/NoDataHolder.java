package com.kraven.coreadapter;


import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.kraven.R;

public class NoDataHolder extends BaseHolder {

    private TextView errorTextView;

    public NoDataHolder(View itemView) {
        super(itemView);
        RelativeLayout relativeLayout = (RelativeLayout) itemView;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setGravity(Gravity.CENTER);
        errorTextView = new TextView(itemView.getContext());
        errorTextView.setTypeface(ResourcesCompat.getFont(itemView.getContext(), R.font.robotomedium));
        errorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemView.getContext().getResources().getDimension(R.dimen._14ssp));
        relativeLayout.addView(errorTextView);
    }

    public void setErrorText(String errorText) {
        if (errorTextView != null) {
            errorTextView.setText(errorText);
        }
    }

    public void setTextColor(@ColorRes int color) {
        if (errorTextView != null) {
            errorTextView.setTextColor(ContextCompat.getColor(errorTextView.getContext(), color));
        }
    }
}