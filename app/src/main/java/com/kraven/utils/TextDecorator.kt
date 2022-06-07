package com.kraven.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BlurMaskFilter
import android.graphics.EmbossMaskFilter
import android.graphics.Typeface
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputLayout

class TextDecorator private constructor(private val textView: TextView, private val content: String) {
    private val decoratedContent: SpannableString = SpannableString(content)
    private var flags: Int = 0

    val finalLength: Int
        get() = content.length

    init {
        this.flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    }

    fun setFlags(flags: Int): TextDecorator {
        this.flags = flags

        return this
    }

    fun underline(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(UnderlineSpan(), start, end, flags)

        return this
    }

    fun underline(vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(UnderlineSpan(), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setTextColor(@ColorRes resColorId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(ForegroundColorSpan(ContextCompat.getColor(textView.context, resColorId)), start, end,
                flags)

        return this
    }

    fun setTextColor(@ColorRes resColorId: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(ForegroundColorSpan(ContextCompat.getColor(textView.context, resColorId)), index, index + text.length, flags)
            }
        }
        return this
    }

    fun setBackgroundColor(@ColorRes colorResId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(BackgroundColorSpan(ContextCompat.getColor(textView.context, colorResId)), start, end, 0)

        return this
    }

    fun setBackgroundColor(@ColorRes colorResId: Int, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(BackgroundColorSpan(ContextCompat.getColor(textView.context, colorResId)), index, index + text.length, flags)
            }
        }

        return this
    }

    fun insertBullet(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(BulletSpan(), start, end, flags)

        return this
    }

    /* public TextDecorator makeTextClickable(final OnTextClickListener listener, final int start, final int end, final boolean underlineText) {
        checkIndexOutOfBoundsException(start, end);
        decoratedContent.setSpan(new ClickableSpan() {
                                     @Override
                                     public void onItemClick(View view) {
                                         listener.onItemClick(view, content.substring(start, end));
                                     }

                                     @Override
                                     public void updateDrawState(TextPaint ds) {
                                         super.updateDrawState(ds);
                                         ds.setUnderlineText(underlineText);
                                     }
                                 }

                , start, end, flags);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        return this;
    }


    public TextDecorator makeTextClickable(final OnTextClickListener listener, final boolean underlineText, final String... texts) {
        int index;

        for (final String text : texts) {
            if (content.contains(text)) {
                index = content.indexOf(text);

                decoratedContent.setSpan(new ClickableSpan() {
                    @Override
                    public void onItemClick(View view) {
                        listener.onItemClick(view, text);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(underlineText);
                    }
                }, index, index + text.length(), flags);
            }
        }

        textView.setMovementMethod(LinkMovementMethod.getInstance());

        return this;
    }

    public TextDecorator makeTextClickable(final ClickableSpan clickableSpan, final int start, final int end) {
        checkIndexOutOfBoundsException(start, end);
        decoratedContent.setSpan(clickableSpan, start, end, flags);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        return this;
    }

    public TextDecorator makeTextClickable(final OnTextClickListener listener, final int color, final boolean underlineText, final Map<String, String> texts) {
        int index;
        int lastIndex = 0;

        for (final Map.Entry<String, String> text : texts.entrySet()) {
            if (content.substring(lastIndex).contains(text.getValue())) {
                index = content.indexOf(text.getValue(), lastIndex);

                decoratedContent.setSpan(new ClickableSpan() {
                    @Override
                    public void onItemClick(View view) {
                        listener.onItemClick(view, text.getKey());
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(underlineText);
                        ds.setColor(color);
                    }
                }, index, index + text.getValue().length(), flags);

                lastIndex = index + text.getValue().length();
            }
        }

        textView.setMovementMethod(LinkMovementMethod.getInstance());

        return this;
    }

    public TextDecorator makeTextClickable(final OnTextClickListener listener, final boolean underlineText, final List<String> texts) {
        int index;

        for (final String text : texts) {
            if (content.contains(text)) {
                index = content.indexOf(text);

                decoratedContent.setSpan(new ClickableSpan() {
                    @Override
                    public void onItemClick(View view) {
                        listener.onItemClick(view, text);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(underlineText);
                    }
                }, index, index + text.length(), flags);
            }
        }
        return this;
    }*/

    fun insertBullet(gapWidth: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(BulletSpan(gapWidth), start, end, flags)

        return this
    }

    fun insertBullet(gapWidth: Int, @ColorRes colorResId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(BulletSpan(gapWidth, ContextCompat.getColor(textView.context, colorResId)), start, end,
                flags)

        return this
    }

    fun makeTextClickable(clickableSpan: ClickableSpan, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(clickableSpan, index, index + text.length, flags)
            }
        }

        textView.movementMethod = LinkMovementMethod.getInstance()

        return this
    }

    fun insertImage(@DrawableRes resId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(ImageSpan(textView.context, resId), start, end, flags)

        return this
    }

    fun quote(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(QuoteSpan(), start, end, flags)

        return this
    }

    fun quote(vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(QuoteSpan(), index, index + text.length, flags)
            }
        }

        return this
    }

    fun quote(@ColorRes colorResId: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(QuoteSpan(ContextCompat.getColor(textView.context, colorResId)), start, end,
                flags)

        return this
    }

    fun quote(@ColorRes colorResId: Int, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(QuoteSpan(ContextCompat.getColor(textView.context, colorResId)), index, index + text.length, flags)
            }
        }

        return this
    }

    fun strikethrough(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(StrikethroughSpan(), start, end, flags)

        return this
    }

    fun strikethrough(vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(StrikethroughSpan(), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setTextStyle(style: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(StyleSpan(style), start, end, flags)

        return this
    }

    fun setTextStyle(style: Int, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(StyleSpan(style), index, index + text.length, flags)
            }
        }

        return this
    }

    fun alignText(alignment: Layout.Alignment, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(AlignmentSpan.Standard(alignment), start, end, flags)

        return this
    }

    fun alignText(alignment: Layout.Alignment, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(AlignmentSpan.Standard(alignment), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setSubscript(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(SubscriptSpan(), start, end, flags)

        return this
    }

    fun setSubscript(vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(SubscriptSpan(), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setSuperscript(start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(SuperscriptSpan(), start, end, flags)

        return this
    }

    fun setSuperscript(vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(SuperscriptSpan(), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setTypeface(@FontRes family: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(CustomTypefaceSpan(ResourcesCompat.getFont(textView.context, family)!!), start, end, flags)
        return this
    }

    fun setTypeface(@FontRes family: Int, vararg texts: String): TextDecorator {
        var index: Int
        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(CustomTypefaceSpan(ResourcesCompat.getFont(textView.context, family)!!), index, index + text.length, flags)
            }
        }
        return this
    }

    fun setTextAppearance(appearance: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(TextAppearanceSpan(textView.context, appearance), start, end,
                flags)

        return this
    }

    fun setTextAppearance(appearance: Int, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(TextAppearanceSpan(textView.context, appearance), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setTextAppearance(appearance: Int, colorList: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(TextAppearanceSpan(textView.context, appearance, colorList), start, end,
                flags)

        return this
    }

    fun setTextAppearance(appearance: Int, colorList: Int, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(TextAppearanceSpan(textView.context, appearance, colorList), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setTextAppearance(family: String, style: Int, size: Int, color: ColorStateList, linkColor: ColorStateList, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(TextAppearanceSpan(family, style, size, color, linkColor), start, end,
                flags)

        return this
    }

    fun setTextAppearance(family: String, style: Int, size: Int, color: ColorStateList, linkColor: ColorStateList, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(TextAppearanceSpan(family, style, size, color, linkColor), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setAbsoluteSize(size: Int, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(AbsoluteSizeSpan(size), start, end, flags)

        return this
    }

    fun setAbsoluteSize(size: Int, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(AbsoluteSizeSpan(size), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setAbsoluteSize(size: Int, dip: Boolean, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(AbsoluteSizeSpan(size, dip), start, end, flags)

        return this
    }

    fun setAbsoluteSize(size: Int, dip: Boolean, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(AbsoluteSizeSpan(size, dip), index, index + text.length, flags)
            }
        }

        return this
    }

    fun setRelativeSize(proportion: Float, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(RelativeSizeSpan(proportion), start, end, flags)

        return this
    }

    fun setRelativeSize(proportion: Float, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(RelativeSizeSpan(proportion), index, index + text.length, flags)
            }
        }

        return this
    }

    fun scaleX(proportion: Float, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(ScaleXSpan(proportion), start, end, flags)

        return this
    }

    fun scaleX(proportion: Float, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(ScaleXSpan(proportion), index, index + text.length, flags)
            }
        }

        return this
    }

    fun blur(radius: Float, style: BlurMaskFilter.Blur, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(MaskFilterSpan(BlurMaskFilter(radius, style)), start, end, flags)

        return this
    }

    fun blur(radius: Float, style: BlurMaskFilter.Blur, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(MaskFilterSpan(BlurMaskFilter(radius, style)), index, index + text.length, flags)
            }
        }

        return this
    }

    fun emboss(direction: FloatArray, ambient: Float, specular: Float, blurRadius: Float, start: Int, end: Int): TextDecorator {
        checkIndexOutOfBoundsException(start, end)
        decoratedContent.setSpan(MaskFilterSpan(EmbossMaskFilter(direction, ambient, specular, blurRadius)), start, end,
                flags)

        return this
    }

    fun emboss(direction: FloatArray, ambient: Float, specular: Float, blurRadius: Float, vararg texts: String): TextDecorator {
        var index: Int

        for (text in texts) {
            if (content.contains(text)) {
                index = content.indexOf(text)
                decoratedContent.setSpan(MaskFilterSpan(EmbossMaskFilter(direction, ambient, specular, blurRadius)), index, index + text.length, flags)
            }
        }

        return this
    }

    fun build() {
        textView.text = decoratedContent
    }

    private fun checkIndexOutOfBoundsException(start: Int, end: Int) {
        if (start < 0) {
            throw IndexOutOfBoundsException("start is less than 0")
        } else if (end > content.length) {
            throw IndexOutOfBoundsException("end is greater than content length " + content.length)
        } else if (start > end) {
            throw IndexOutOfBoundsException("start is greater than end")
        }
    }

    companion object {

        fun decorate(textView: TextView, content: String): TextDecorator {
            return TextDecorator(textView, content)
        }

        //Set fonts for textinputlayout
        fun setTypefaceForTextInputLayout(context: Context, @FontRes font: Int, textInputLayout: TextInputLayout) {
            var face: Typeface? = null
            face = ResourcesCompat.getFont(context, font)
            textInputLayout.typeface = face
        }

        //Set fonts for textinputlayout
        fun setTypefaceForTextInputLayout(context: Context, @FontRes font: Int, vararg textInputLayout: TextInputLayout) {
            for (layout in textInputLayout)
                setTypefaceForTextInputLayout(context, font, layout)
        }

        fun getColoredSpannable(startText: String, endText: String, startColor: Int, endColor: Int): Spannable {
            val start = startText.length
            val length = start + endText.length
            val spannable = SpannableString(startText + endText)
            spannable.setSpan(ForegroundColorSpan(startColor),
                    0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(endColor),
                    start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannable
        }

        fun getColoredSpannable(spannable: Spannable, startText: String, endText: String, startColor: Int, endColor: Int): Spannable {
            val start = startText.length
            val length = start + endText.length
            spannable.setSpan(ForegroundColorSpan(startColor),
                    0, start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(endColor),
                    start, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannable
        }
    }
    /*
    public static Spannable getFontSpannable(Context context, String start, String end) {

        Typeface opensansRegular = ResourcesCompat.getFont(context
                , R.font.montserrat_light);
        Typeface opensansLight = ResourcesCompat.getFont(context
                , R.font.montserrat_bold);


        int slength = start.length();
        int elength = slength + end.length();

        String combine = start + end;
        Spannable spannable = new SpannableString(start + end);
        spannable.setSpan(new CustomTypefaceSpan("bold", opensansLight)
                , 0
                , slength
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan("bold", opensansRegular)
                , slength
                , combine.lengh()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static Spannable getFontSpannable(Context context
            , String start
            , String end
            , @FontRes int fontOne
            , @FontRes int fontTwo) {

        Typeface opensansRegular = ResourcesCompat.getFont(context
                , fontOne);
        Typeface opensansLight = ResourcesCompat.getFont(context
                , fontTwo);


        int slength = start.length();
        //int elength = slength + end.length();

        String combine = start + end;
        Spannable spannable = new SpannableString(start + end);
        spannable.setSpan(new CustomTypefaceSpan("bold", opensansRegular)
                , 0
                , slength
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan("bold", opensansLight)
                , slength
                , combine.length()
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    public static Spannable getFontSpannable(Context context, String s1, String s2, String s3, String s4) {
        Typeface opensansRegular = ResourcesCompat.getFont(context
                , R.font.montserrat_regular);
        Typeface opensansLight = ResourcesCompat.getFont(context
                , R.font.montserrat_bold);

        int l1 = s1.length();
        int l2 = l1 + s2.length();
        Spannable spannable = new SpannableString(s1 + s2 + s3 + s4);
        spannable.setSpan(new CustomTypefaceSpan("bold", opensansLight)
                , 0
                , l1
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan("bold", opensansRegular)
                , l1
                , l2
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        int l3 = l2 + s3.length();
        int l4 = l3 + s4.length();
        spannable.setSpan(new CustomTypefaceSpan("bold", opensansLight)
                , l2
                , l3
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new CustomTypefaceSpan("bold", opensansRegular)
                , l3
                , l4
                , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        return spannable;
    }

    //Set fonts for textinputlayout
    public static void setTypefaceForTextInputLayout(Context context, @StringRes int font, TextInputLayout textInputLayout) {
        Typeface face = null;
        face = Typeface.createFromAsset(context.getAssets(),
                context.getString(font));
        textInputLayout.setTypeface(face);
    }

    //Set fonts for textinputlayout
    public static void setTypefaceForTextInputLayout(Context context, @StringRes int font, TextInputLayout... textInputLayout) {
        for (TextInputLayout layout : textInputLayout)
            setTypefaceForTextInputLayout(context, font, layout);
    }

    public static void setOnlineOffline(TextView textViewStatus, String online, String offline, int onlineCount) {
        if (onlineCount == 0) {
            textViewStatus.setText(offline);
            textViewStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_offline, 0);
        } else {
            textViewStatus.setText(String.format(Locale.ENGLISH, "%d %s", onlineCount, online));
            textViewStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_online, 0);
        }
    }

    public static void setOnlineOffline(ImageView imageView, boolean isOnline) {
        imageView.setImageResource(isOnline ? R.drawable.ic_online : R.drawable.ic_offline);
    }

    public static void setOnlineOfflineBig(ImageView imageView, boolean isOnline) {
        //imageView.setImageResource(isOnline ? R.drawable.ic_online_big : R.drawable.ic_offline_big);
        imageView.setImageResource(isOnline ? R.drawable.ic_online : R.drawable.ic_offline);
    }

    public static void setOnlineOfflineSmall(Context context, TextView textView, String mode) {
        Drawable drawable[] = new Drawable[0];
        Drawable drawableStart = null;
        try {
            drawable = textView.getCompoundDrawablesRelative();
            drawableStart = ContextCompat.getDrawable(context
                    , mode.equalsIgnoreCase(context.getString(R.string.publics))
                            ? R.drawable.ic_earth : R.drawable.ic_private_grey);
            if (mode.equalsIgnoreCase(context.getString(R.string.privates))) {
                drawableStart.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.grey), PorterDuff.Mode.SRC_ATOP));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(
                drawableStart
                , drawable[1]
                , drawable[2]
                , drawable[3]);
    }*/
}
