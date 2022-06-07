package com.kraven.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;

import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.kraven.R;

public class TextInputLayoutWithError extends TextInputLayout {

    private Context mContext;

    private String mErrorMessage;

    public TextInputLayoutWithError(Context context) {
        super(context);
        mContext = context;
    }

    public TextInputLayoutWithError(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public TextInputLayoutWithError(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs) {
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        final TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.TextInputLayout, defStyleAttr, 0);
        try {
            mErrorMessage = typedArray.getString(R.styleable.TextInputLayout_errorText);
        } finally {
            typedArray.recycle();
        }
    }

    public void setToErrorView() {

        final EditText editText = getEditText();
        if (editText == null) {
            return;
        }
        final int color = ContextCompat.getColor(mContext, R.color.colorAccent);

        //TODO améliorer ce système de MAJ graphique, apparemment c'est un manque du Framework:http://tinyurl.com/z3z62lp

        //colore la ligne de l'éditext
        editText.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        setHintTextAppearance(R.style.ErrorAppearanceTextInputLayout);

        if (getHint() != null && getHint().length() > 0) {
            editText.setHint(getHint());
            setHint("");
            editText.setHintTextColor(color);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        final CharSequence editextHint = editText.getHint();
                        if (editextHint != null && editextHint.length() > 0) {
                            setHint(editextHint);
                            editText.setHint("");
                        }
                    } else {
                        //keeping the animation of textinputlayout
                        postOnAnimationDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (getHint().length() > 0 && editText.getText().length() == 0) {
                                    editText.setHint(getHint());
                                    setHint("");
                                    editText.setHintTextColor(ContextCompat.getColor(mContext, R.color.light_blue));
                                    removeCallbacks(this);
                                }
                            }
                        }, 200);


                    }

                }
            });

        }
    }


    public String getErrorMessage() {
        return mErrorMessage == null ? "" : mErrorMessage;
    }

    public void setToDefaultView() {

        final EditText editText = getEditText();
        if (editText == null) {
            return;
        }
        final int color = ContextCompat.getColor(mContext, R.color.colorPrimaryDark);
        editText.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        //setHintTextAppearance(R.style.AppearanceTextInputLayout);
    }
}