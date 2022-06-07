package com.kraven.coreadapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class LoadingHolder extends BaseHolder {


    public LoadingHolder(View itemView) {
        super(itemView);
        RelativeLayout relativeLayout = (RelativeLayout) itemView;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 50, 50, 50);
        relativeLayout.setLayoutParams(layoutParams);
        relativeLayout.setGravity(Gravity.CENTER);
        ProgressBar progressBar = new ProgressBar(itemView.getContext());
        relativeLayout.addView(progressBar);
    }
}