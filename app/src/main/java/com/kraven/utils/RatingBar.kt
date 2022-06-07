package com.kraven.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout

import com.kraven.R

import androidx.annotation.DrawableRes


/**
 * Created by JD on 10/16/15.<br></br>
 */

class RatingBar : FrameLayout, View.OnClickListener {

    /**
     * Max count of star
     */
    private var mMaxCount = MAX_COUNT
    /**
     * Count of star
     */
    private var mCount = 0
    /**
     * Fill drawable of star
     */
    private var mFillDrawable: Drawable? = null
    /**
     * Empty drawable of star
     */
    private var mEmptyDrawable: Drawable? = null
    private var mOnRatingChangeListener: OnRatingChangeListener? = null
    /**
     * Space between stars
     */
    private var mSpace = 0

    private var isTouchRating = true
    private var isClickRating = true

    private var mRootLayout: LinearLayout? = null
    private var mImageViews: Array<ImageView?>? = null
    private var mContext: Context? = null
    private var mOldX = 0f
    private var mOldY = 0f
    private var mOldStarCount = 0

    /**
     *
     * Set space between stars
     *
     * @param space space between stars in pixel unit
     */
    var space: Int
        get() = mSpace
        set(space) {
            var space = space
            space = Math.max(0, space)
            if (mSpace == space) {
                return
            }

            mSpace = space
            updateStarViews()
        }

    private  var fillDrawable: Drawable?
        get() = mFillDrawable
        set(fillDrawable) {
            if (mFillDrawable === fillDrawable) {
                return
            }
            mFillDrawable = fillDrawable
            updateStarViews()
        }

    private  var emptyDrawable: Drawable?
        get() = mEmptyDrawable
        set(emptyDrawable) {
            mEmptyDrawable = emptyDrawable
            updateStarViews()
        }

    /**
     *
     * Get max rating count
     *
     * @return the max rating count
     */
    /**
     *
     * Set max rating count which will lead to RatingBar refreshing immediately
     *
     * @param maxCount
     */
    private  var maxCount: Int
        get() = mMaxCount
        set(maxCount) {
            var maxCount = maxCount
            maxCount = Math.max(0, maxCount)
            if (maxCount == mMaxCount) {
                return
            }
            mMaxCount = maxCount
            createStarViews(maxCount)

            if (maxCount < mCount) {
                count = maxCount
            }
        }

    /**
     *
     * Get rating count
     *
     * @return the rating count.
     */
    /**
     *
     * Set rating count, this will update rating bar immediately.
     *
     * @param count the new rating count. If count small than 0 it will be set to 0, or if count
     * large than max count it will be set to the max count.
     */
    var count: Int
        get() = mCount
        set(count) {
            var count = count
            count = Math.max(0, Math.min(mMaxCount, count))
            if (count == mCount) {
                return
            }

            val oldCount = mCount
            mCount = count
            updateStarViews()
            if (mOnRatingChangeListener != null) {
                mOnRatingChangeListener!!.onChange(this, oldCount, mCount)
            }
        }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    /**
     *
     * Set the fill star drawable resource
     *
     * @param res drawable resource
     */
    fun setFillDrawableRes(@DrawableRes res: Int) {
        fillDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContext!!.getDrawable(res)
        } else {
            mContext!!.resources.getDrawable(res)
        }
    }

    /**
     *
     * Set the empty star drawable resource
     *
     * @param res drawable resource
     */
    fun setEmptyDrawableRes(@DrawableRes res: Int) {
        emptyDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mContext!!.getDrawable(res)
        } else {
            mContext!!.resources.getDrawable(res)
        }
    }

    fun setOnRatingChangeListener(listener: OnRatingChangeListener?) {
        mOnRatingChangeListener = listener
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mContext = context

        // Retrieve attributes
        if (attrs == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFillDrawable = context.getDrawable(R.drawable.ic_rating)
                mEmptyDrawable = context.getDrawable(R.drawable.ic_raitng_empty)
            } else {
                mFillDrawable = context.getDrawable(R.drawable.ic_rating)
                mEmptyDrawable = context.getDrawable(R.drawable.ic_raitng_empty)
            }
        } else {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBar)
            mMaxCount = typedArray.getInteger(R.styleable.RatingBar_rb_max_count, MAX_COUNT)
            mCount = typedArray.getInteger(R.styleable.RatingBar_rb_count, 0)
            mFillDrawable = typedArray.getDrawable(R.styleable.RatingBar_rb_fill)
            mEmptyDrawable = typedArray.getDrawable(R.styleable.RatingBar_rb_empty)
            mSpace = typedArray.getDimensionPixelSize(R.styleable.RatingBar_rb_space, 0)
            isClickRating = typedArray.getBoolean(R.styleable.RatingBar_rb_click_rating, true)
            isTouchRating = typedArray.getBoolean(R.styleable.RatingBar_rb_touch_rating, true)
            typedArray.recycle()

            if (mFillDrawable == null) {
                mFillDrawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.getDrawable(R.drawable.ic_rating)
                } else {
                    context.resources.getDrawable(R.drawable.ic_rating)
                }
            }

            mMaxCount = Math.max(0, mMaxCount)
            mCount = Math.max(0, Math.min(mCount, mMaxCount))
        }

        // Create root layout (LinearLayout) used to contain stars
        mRootLayout = LinearLayout(context)
        addView(mRootLayout, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ))

        createStarViews(mMaxCount)
    }

    // create star views
    private fun createStarViews(count: Int) {
        // remove previous child views
        if (mRootLayout!!.childCount > 0) {
            mRootLayout!!.removeAllViews()
        }

        // create new image views
        mImageViews = arrayOfNulls(count)
        for (i in mImageViews!!.indices) {
            // Use FrameLayout to wrap the star view
            val frameLayout = FrameLayout(context)
            val llp = LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1f
            )
            mRootLayout!!.addView(frameLayout, llp)

            mImageViews!![i] = ImageView(context)
            val imageView = mImageViews!![i]
            imageView?.setOnClickListener(this)
            imageView?.scaleType = ImageView.ScaleType.CENTER_INSIDE
            imageView?.tag = i
            val flp = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER
            )
            frameLayout.addView(imageView, flp)
        }

        updateStarViews()
    }

    /**
     *
     * Update the rating bar with current rating count.
     */
    private fun updateStarViews() {
        var imgView: ImageView?
        // update drawable
        for (i in mImageViews!!.indices) {
            imgView = mImageViews!![i]
            imgView?.setImageDrawable(if (i < mCount) mFillDrawable else mEmptyDrawable)

            // update margin between the stars whose drawable is not null
            val parent = imgView?.parent as ViewGroup
            val mlp = parent.layoutParams as ViewGroup.MarginLayoutParams
            if (imgView.drawable != null
                    && i - 1 >= 0
                    && mImageViews!![i - 1]?.drawable != null) {
                mlp.setMargins(mSpace, 0, 0, 0)
            } else {
                mlp.setMargins(0, 0, 0, 0)
            }
            parent.layoutParams = mlp
        }
    }

    override fun onClick(v: View) {
        val index = v.tag as Int
        count = index + 1
    }

    override fun setClickable(clickable: Boolean) {}

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mOldX = event.x
                mOldY = event.y
                mOldStarCount = getTouchStarCount(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTouchRating) {
                    val deltaX = event.x - mOldX
                    val deltaY = event.y - mOldY
                    val distance = Math.round(Math.sqrt(Math.pow(deltaX.toDouble(), 2.0) + Math.pow(deltaY.toDouble(), 2.0))).toInt()
                    if (distance >= ViewConfiguration.getTouchSlop()) {
                        count = getTouchStarCount(event)
                    }
                }
                mOldX = event.x
                mOldY = event.y
            }
            MotionEvent.ACTION_UP -> if (isClickRating) {
                val starCount = getTouchStarCount(event)
                // if touch down and touch up hit the same view think it is a click event
                if (starCount == mOldStarCount) {
                    count = starCount
                }
            }
            else -> {
            }
        }
        return false
    }

    private fun getStarView(index: Int): View {
        return mRootLayout!!.getChildAt(index)
    }

    /**
     *
     * Get the star count on specified touch position
     * @param event touch event
     * @return selected star count
     */
    private fun getTouchStarCount(event: MotionEvent): Int {
        var count = 1

        val rawX = event.rawX
        for (i in 0 until maxCount) {
            val rect = Rect()
            val view = getStarView(i)
            view.getGlobalVisibleRect(rect)
            val mlp = view.layoutParams as ViewGroup.MarginLayoutParams
            if (rawX > rect.right + mlp.rightMargin) {
                count += 1
            }
        }

        return count
    }

    interface OnRatingChangeListener {
        /**
         *
         * This method will be execute after every change of rating bar
         *
         * @param preCount previous rating count
         * @param curCount current rating count
         */
        fun onChange(ratingBar: RatingBar, preCount: Int, curCount: Int)
    }

    companion object {
        private const val MAX_COUNT = 5
    }

}

