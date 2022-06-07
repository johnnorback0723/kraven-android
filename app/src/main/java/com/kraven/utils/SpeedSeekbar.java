package com.kraven.utils;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import android.util.AttributeSet;

import com.kraven.R;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.res.ResourcesCompat;


public class SpeedSeekbar extends AppCompatSeekBar {


    public SpeedSeekbar(Context context) {
        super(context);
    }

    public SpeedSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public SpeedSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        /*int thumb_x = (int) (( (double)this.getProgress()/this.getMax() ) * (double)this.getWidth());
        float middle = (float) (this.getHeight())/2;*/
      /*  int i = getThumb().getIntrinsicWidth() / 2;
        int thumb_x = (int) (((double) this.getProgress() / this.getMax()) * (double) this.getWidth()) - i;
        int middle = getHeight()-(getHeight()-10);


        String progressText = String.valueOf(getProgress());
      //  Rect bounds = new Rect(10,10,10,10);
      //  mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);
        mTextPaint.setColor(getResources().getColor(R.color.colorHomeLogo));
        */


        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = getThumb().getIntrinsicWidth() * (0f - progressRatio);
        float thumbX = progressRatio * width + leftPadding + thumbOffset;
        float thumbY = getHeight()/5;
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.orange));
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen._8ssp));
        paint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.work_sans_medium));
        String text= this.getProgress()+" Km";
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        c.drawText(""+text,thumbX-(text.length()), thumbY, paint);
    }
}

