package com.kraven.extensions

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.kraven.R
import com.kraven.data.URLFactory
import com.kraven.ui.home.model.Restaurants
import com.kraven.ui.home.model.Timing
import com.kraven.utils.ConstantApp
import com.kraven.utils.Formatter
import com.kraven.utils.GlideApp
import com.kraven.utils.LocationManager
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


/**
 * Clear notification
 */


fun Fragment.transactionId(userId: String): String {
    val currentDate = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyyMMdd_HHmmss", true).toString()
    return "TRN_".plus(userId).plus("_").plus(if (URLFactory.IS_LOCAL) getString(R.string.MERCHANT_ID_TESTING) else getString(R.string.MERCHANT_ID_LIVE)).plus(currentDate)
}

fun Fragment.transactionIdWallet(userId: String): String {
    val currentDate = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, "yyyyMMdd_HHmmss", true).toString()
    return "WALLET_".plus(userId).plus("_").plus(if (URLFactory.IS_LOCAL) getString(R.string.MERCHANT_ID_TESTING) else getString(R.string.MERCHANT_ID_LIVE)).plus(currentDate)
    //return "WALLET_".plus(if(URLFactory.IS_LOCAL)getString(R.string.MERCHANT_ID_TESTING) else getString(R.string.MERCHANT_ID_LIVE)).plus(currentDate).plus("_").plus(userId)
}

fun getCurrentTimezoneOffset(callBack: (String, String) -> Unit) {
    val tz = TimeZone.getDefault()
    val cal = GregorianCalendar.getInstance(tz)
    val offsetInMillis = tz.getOffset(cal.timeInMillis)
    var offset = String.format(Locale.US, "%02d:%02d", abs(offsetInMillis / 3600000), Math.abs(offsetInMillis / 60000 % 60))
    offset = "GMT" + (if (offsetInMillis >= 0) "+" else "-") + offset
    callBack(tz.id, offset)
}

fun clearNotification(activity: FragmentActivity) {
    val notifyManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notifyManager.cancelAll()
}

/**
 * Set restaurant time status like Available,Currently Unavailable,Closed
 */

fun setRestaurantTimeStatus(date: String, startTime: String, endTime: String, timingStatus: String, timing: Timing): String {

    val currentTime = Formatter.format(date, Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.HH_mm_ss, false)

    val endDate = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(endTime)

    val beforeDate = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(startTime)

    val currentDate = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(currentTime)

    return if (currentDate.after(beforeDate) && currentDate.before(endDate) && timingStatus == ConstantApp.RestaurantStatus.OPEN) {
        timing.status = ConstantApp.RestaurantStatus.AVAILABLE
        ConstantApp.RestaurantStatus.AVAILABLE
    } else if (currentDate.after(beforeDate) && currentDate.before(endDate)) {
        timing.status = ConstantApp.RestaurantStatus.CURRENTLY_UNAVAILABLE
        ConstantApp.RestaurantStatus.CURRENTLY_UNAVAILABLE
    } else {
        timing.status = ConstantApp.RestaurantStatus.CLOSED
        ConstantApp.RestaurantStatus.CLOSED
    }
}


fun getRestaurantList(responseBody: List<Restaurants>): MutableList<Restaurants>? {
    val list = responseBody as MutableList<Restaurants>

    list.forEach { restaurantDetails ->

        restaurantDetails.timing?.filter { timing ->
            setRestaurantTimeStatus(Date().toString(), timing.startTime, timing.endTime, timing.status, timing) == ConstantApp.RestaurantStatus.AVAILABLE
        }
    }
    return list
}

fun setRestaurantTimeSelectedDateTime(orderDatetime: String): Boolean {

    val currentTime = Formatter.format(Date().toString(),
            Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.YYYY_MM_DD_hh_mm_aa, false)

    val inBeforeDate = SimpleDateFormat(Formatter.YYYY_MM_DD_hh_mm_aa, Locale.ENGLISH).parse(currentTime)

    val beforeDate = SimpleDateFormat(Formatter.YYYY_MM_DD_hh_mm_aa, Locale.ENGLISH).parse(orderDatetime)

    return inBeforeDate.before(beforeDate)
}


/**
 * Set Two digit string
 */


fun specialOfferPrice(itemPrice: Float, specialPrice: Float): String {
    return try {
        Formatter.round(itemPrice.toDouble().minus((itemPrice.toDouble() * specialPrice.toDouble()).div(100)))
    } catch (e: NumberFormatException) {
        "0.00"
    }

}

fun convertTwoDigit(setValue: Float): String {
    return try {
        String.format(Locale.US, "$%.2f", setValue)
    } catch (e: NumberFormatException) {
        "0.00"
    }
}

fun convertTwoDigitFlot(setValue: Float): Float {
    return try {
        String.format(Locale.US, "%.2f", setValue).toFloat()
    } catch (e: NumberFormatException) {
        0.00f
    }
}

fun twoDigit(setValue: Float): String {
    return try {
        String.format(Locale.US, "%.2f", setValue, Locale.US)
    } catch (e: NumberFormatException) {
        "0.00"
    }
}

fun testTwoDigit(setValue: Float): String? {
    val d = setValue
    val decimalFormat = DecimalFormat("#.##")
    decimalFormat.setRoundingMode(RoundingMode.FLOOR)
    return decimalFormat.format(d)
    //System.out.println("Result :" + decimalFormat.format(d))
}

fun getLastFourNumber(cardToken: String): String {
    val lastFour = cardToken.substring(cardToken.length - 4, cardToken.length)
    return "xxxxx xxxx xxxx $lastFour"
}


/**
 * Set visibility of the view
 */
fun View.viewShow() {
    visibility = VISIBLE
}


fun AppCompatEditText.clearText() {
    text?.clear()
}

fun View.viewGone() {
    visibility = GONE
}


fun getText(view: TextView): String? {
    return view.text.toString().trim { it <= ' ' }
}

fun isSelectedTrue(view: List<View>) {
    for (i in view.indices) {
        view[i].isSelected = true
    }
}


fun getTextReplace(view: TextView): Float {
    return view.text?.toString()?.replace("$", "")?.toFloat() ?: 0.0f

}

fun getTextReplaceString(view: TextView): String {
    return view.text?.toString()?.replace("$", "") ?: "0.0F"
}

/**
 * Scale bitmap.
 */
fun Bitmap.scale(ratio: Float): Bitmap {
    return Bitmap.createScaledBitmap(this, Math.round(width.times(ratio)), Math.round(height.times(ratio)), true)
}


fun loadCircleImage(imageView: ImageView, url: Any?) {
    GlideApp.with(imageView.context).load(url).circleCrop().apply(RequestOptions().placeholder(R.drawable.profile)).into(imageView)
}


fun AppCompatImageView.loadCornerImage(url: Any, rounded: Int) {
    GlideApp.with(context).load(url).transform(RoundedCorners(rounded)).into(this)
}


fun getRestaurantFutureLists(date: String, responseBody: List<Restaurants>): MutableList<Restaurants>? {

    var list = responseBody as MutableList<Restaurants>

    list.forEach { restaurant ->
        val availableList = availableRestaurantFuture(date, getList(restaurant.timing!!))
        restaurant.timing?.clear()
        restaurant.timing?.addAll(availableList)
    }

    list = list.sortedWith(compareBy { it.timing?.get(0)?.statusCode }) as MutableList<Restaurants>

    return list

}

fun getRestaurantFutureListsClosed(date: String, responseBody: List<Restaurants>): MutableList<Restaurants>? {

    var list = responseBody as MutableList<Restaurants>

    list.forEach { restaurant ->

        val availableList = availableRestaurantFuture(date, getList(restaurant.timing!!))

        restaurant.timing?.clear()
        restaurant.timing?.addAll(availableList)
    }

    list = list.sortedWith(compareBy { it.timing?.get(0)?.statusCode }) as MutableList<Restaurants>
    return list

}


fun availableRestaurant(timing: MutableList<Timing>): MutableList<Timing> {
    val list = mutableListOf<Timing>()

    timing.forEach { timing1 ->
        val currentTimeDate = Formatter.format(Date().toString(), Formatter.YYYY_MM_DD_T_HH_MM_SSS_Z, Formatter.HH_mm_ss, false)

        val startTime = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(timing1.startTime)
        //val endTime = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(if (timing1.endTime == "12:00:00" || timing1.endTime == "00:00:00") "23:59:59" else timing1.endTime)
        val endTime = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(timing1.endTime)
        val currentTime = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(currentTimeDate)

        if (timing1.status == ConstantApp.RestaurantStatus.OPEN || timing1.status == ConstantApp.RestaurantStatus.AVAILABLE) {

            if (currentTime.after(startTime) && currentTime.before(endTime)) {
                timing1.statusCode = 0
                timing1.status = ConstantApp.RestaurantStatus.AVAILABLE
                list.clear()
                list.add(timing1)
                return@forEach
            } else {
                timing1.statusCode = 2
                timing1.status = ConstantApp.RestaurantStatus.CLOSED
                list.add(timing1)
            }

        } else {
            if (currentTime.after(startTime) && currentTime.before(endTime)) {
                timing1.statusCode = 1
                timing1.status = ConstantApp.RestaurantStatus.CURRENTLY_UNAVAILABLE
                list.clear()
                list.add(timing1)
                return@forEach
            } else {
                timing1.statusCode = 2
                timing1.status = ConstantApp.RestaurantStatus.CLOSED
                list.add(timing1)
            }

        }
    }

    if (list.size > 1) {
        val time = list[0]
        list.clear()
        list.add(time)
    }

    return list

}

fun availableRestaurantFuture(date: String, timing: List<Timing>): MutableList<Timing> {

    val list = mutableListOf<Timing>()
    val currentTimeDate = Formatter.format(date, "yyyy-MM-dd hh:mm aa", Formatter.HH_mm_ss, false)
    val currentTime = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(currentTimeDate)

    timing.forEach { timing1 ->
        val startTime = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(timing1.startTime)
        val endTime = SimpleDateFormat(Formatter.HH_mm_ss, Locale.ENGLISH).parse(timing1.endTime)



        if (timing1.status == ConstantApp.RestaurantStatus.OPEN || timing1.status == ConstantApp.RestaurantStatus.AVAILABLE) {
            if (currentTime.after(startTime) && currentTime.before(endTime)) {
                timing1.statusCode = 0
                timing1.status = ConstantApp.RestaurantStatus.AVAILABLE
                list.clear()
                list.add(timing1)
                return@forEach
            } else {
                timing1.statusCode = 2
                timing1.status = ConstantApp.RestaurantStatus.CLOSED
                list.add(timing1)
            }

        } else {
            if (currentTime.after(startTime) && currentTime.before(endTime)) {
                timing1.statusCode = 1
                timing1.status = ConstantApp.RestaurantStatus.CURRENTLY_UNAVAILABLE
                list.clear()
                list.add(timing1)
                return@forEach
            } else {
                timing1.statusCode = 2
                timing1.status = ConstantApp.RestaurantStatus.CLOSED
                list.add(timing1)
            }
        }
    }

    if (list.size > 1) {
        val time = list[0]
        list.clear()
        list.add(time)
    }
    return list
}

fun getTimeFormat(time: String): String {
    var getTime = ""
    try {
        val sdf = SimpleDateFormat(ConstantApp.TIME_INPUT_FORMAT, Locale.ENGLISH)
        val dateObj = sdf.parse(time)
        getTime = SimpleDateFormat(ConstantApp.TIME_OUTPUT_FORMAT, Locale.ENGLISH).format(dateObj)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return getTime
}

fun View.clickWithDebounce(debounceTime: Long = 3000, action: () -> Unit) {
    this.setOnClickListener(object : OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}


fun Fragment.getCurrentLatLong(locationManager: LocationManager, callBack: (LatLng) -> Unit) {
    requirePermission(activity!!, "This app need permissions to use this feature.You can grant them in app settings.") {
        locationManager.checkLocationEnableAndStartUpdate(true)
        locationManager.locationUpdateLiveData.observe(this.viewLifecycleOwner, Observer {
            callBack(it)
            locationManager.locationUpdateLiveData.removeObservers(this)
        })
    }
}


fun Fragment.addFireBaseLogs(itemID: String, itemName: String, contentType: String) {
    val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleOf(
            FirebaseAnalytics.Param.ITEM_ID to itemID,
            FirebaseAnalytics.Param.ITEM_NAME to itemName,
            FirebaseAnalytics.Param.CONTENT_TYPE to contentType))
}

fun AppCompatActivity.addFireBaseLogs(itemID: String, itemName: String, contentType: String) {
    val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleOf(
            FirebaseAnalytics.Param.ITEM_ID to itemID,
            FirebaseAnalytics.Param.ITEM_NAME to itemName,
            FirebaseAnalytics.Param.CONTENT_TYPE to contentType))
}