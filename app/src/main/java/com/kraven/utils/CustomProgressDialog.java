package com.kraven.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;


import com.kraven.R;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.BallBeatIndicator;
import com.wang.avi.indicators.BallClipRotateIndicator;
import com.wang.avi.indicators.BallClipRotateMultipleIndicator;
import com.wang.avi.indicators.BallClipRotatePulseIndicator;
import com.wang.avi.indicators.BallGridBeatIndicator;
import com.wang.avi.indicators.BallGridPulseIndicator;
import com.wang.avi.indicators.BallPulseIndicator;
import com.wang.avi.indicators.BallPulseRiseIndicator;
import com.wang.avi.indicators.BallPulseSyncIndicator;
import com.wang.avi.indicators.BallRotateIndicator;
import com.wang.avi.indicators.BallScaleMultipleIndicator;
import com.wang.avi.indicators.BallScaleRippleIndicator;
import com.wang.avi.indicators.BallScaleRippleMultipleIndicator;
import com.wang.avi.indicators.BallSpinFadeLoaderIndicator;
import com.wang.avi.indicators.BallTrianglePathIndicator;
import com.wang.avi.indicators.BallZigZagDeflectIndicator;
import com.wang.avi.indicators.BallZigZagIndicator;
import com.wang.avi.indicators.CubeTransitionIndicator;
import com.wang.avi.indicators.LineScaleIndicator;
import com.wang.avi.indicators.LineScalePartyIndicator;
import com.wang.avi.indicators.LineScalePulseOutIndicator;
import com.wang.avi.indicators.LineScalePulseOutRapidIndicator;
import com.wang.avi.indicators.LineSpinFadeLoaderIndicator;
import com.wang.avi.indicators.PacmanIndicator;
import com.wang.avi.indicators.SemiCircleSpinIndicator;
import com.wang.avi.indicators.SquareSpinIndicator;
import com.wang.avi.indicators.TriangleSkewSpinIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by JD on 18/09/19.
 */

public class CustomProgressDialog extends DialogFragment {
    private int mBackStackId;
    private Random random = new Random();
    private AVLoadingIndicatorView loadingIndicatorView;
    private List<Class> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.other_dialog_loader_custom, null, false);

        final Dialog dialog = getDialog();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setCancelable(false);

        if (list.size() == 0) {
            list.add(BallBeatIndicator.class);
            list.add(BallClipRotateIndicator.class);
            list.add(BallClipRotateMultipleIndicator.class);
            list.add(BallClipRotatePulseIndicator.class);
            list.add(BallGridBeatIndicator.class);
            list.add(BallGridPulseIndicator.class);
            list.add(BallPulseIndicator.class);
            list.add(BallPulseRiseIndicator.class);

            list.add(BallPulseSyncIndicator.class);
            list.add(BallRotateIndicator.class);
            list.add(BallScaleMultipleIndicator.class);
            list.add(BallScaleRippleIndicator.class);
            list.add(BallScaleRippleMultipleIndicator.class);
            list.add(BallSpinFadeLoaderIndicator.class);
            list.add(BallTrianglePathIndicator.class);
            list.add(BallZigZagDeflectIndicator.class);
            list.add(BallZigZagIndicator.class);
            list.add(CubeTransitionIndicator.class);

            list.add(LineScaleIndicator.class);
            list.add(LineScalePartyIndicator.class);
            list.add(LineScalePulseOutIndicator.class);
            list.add(LineScalePulseOutRapidIndicator.class);
            list.add(LineSpinFadeLoaderIndicator.class);

            list.add(PacmanIndicator.class);
            list.add(SemiCircleSpinIndicator.class);
            list.add(SquareSpinIndicator.class);
            list.add(TriangleSkewSpinIndicator.class);

        }

        loadingIndicatorView = view.findViewById(R.id.loaderView);
        loadingIndicatorView.setIndicator(list.get(random.nextInt(list.size() - 1)).getSimpleName());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.0f;
        window.setAttributes(windowParams);*/
    }

   /*@Override
    public void show(FragmentManager manager, String tag) {
        try {
            if (manager.findFragmentByTag(tag) == null) {
                super.show(manager, tag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void dismiss() {
        super.dismissAllowingStateLoss();
    }

    public int show(FragmentTransaction transaction, String tag) {
        return show(transaction, tag, true);
    }

    public int show(FragmentTransaction transaction, String tag, boolean allowStateLoss) {
        transaction.add(this, tag);
        mBackStackId = allowStateLoss ? transaction.commitAllowingStateLoss() : transaction.commit();
        return mBackStackId;
    }

}
