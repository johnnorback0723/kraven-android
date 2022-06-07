package com.kraven.utils

import android.content.Context
import android.util.Log


import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.annotation.StringDef

/**
 * A class to format datetime strings
 */
object Formatter {
    const val JAN_2017 = "MMM / yyyy"
    const val MM_YY = "MM/yy"
    const val MMM = "MMM"
    const val MM = "MM"
    const val MMMM = "MMMM"
    const val YYYY = "yyyy"
    const val MMMM_YYYY = "MMMM yyyy"
    const val DD_MM_YYYY = "dd/ MM /yyyy"
    const val DD_MMM_YYYY = "dd MMM yyyy"
    const val DD_MMMM_YYYY_FULL = "dd MMMM, yyyy"
    const val DD_MMMM_YYYY = "dd MMMM yyyy"
    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val YYYY_MM_DD_HH_MM_SS_NEW = "yyyy-MM-dd hh-mm-ss"
    const val YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
    const val YYYY_MM_DD_hh_mm_aa = "yyyy-MM-dd hh:mm aa"
    const val DD_MMMM_YYYY_hh_mm_aa = "dd MMMM yyyy hh:mm aa"
    const val DD_MMM_YYYY_hh_mm_aa = "dd MMM yyyy, hh:mm aa"
    const val CALL_LOG_TIME = "dd MMM, yyyy - hh:mm aa"
    const val YYYY_MM_DD_T_HH_MM_SSS_Z = "EE MMM dd HH:mm:ss z yyyy"
    const val SERVER_TIME_FORMATE = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    // public static final String DD_MM_YYYY = "dd.MM.yyyy";
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val MM_DD_YYYY = "MM/dd/yyyy"
    const val HH_mm = "HH:mm"
    const val HH_mm_ss = "HH:mm:ss"
    const val hh_mm_aa = "hh:mm aa"
    // CREATE DateFormatSymbols WITH ALL SYMBOLS FROM (DEFAULT) Locale
    private val symbols: DateFormatSymbols
    private val TAG = "Formatter"
    private val UTC = "GMT"
    private val SECOND: Long = 1000
    private val MINUTE = SECOND * 60
    private val HOUR = MINUTE * 60
    private val DAY = HOUR * 24
    private val MONTH = DAY * 30
    private val YEAR = MONTH * 12
    private val WEEK = DAY * 7
    internal var suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
            //    10    11    12    13    14    15    16    17    18    19
            "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
            //    20    21    22    23    24    25    26    27    28    29
            "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
            //    30    31
            "th", "st")//    0     1     2     3     4     5     6     7     8     9
    private val inputTimeZone = UTC

    init {
        // OVERRIDE SOME symbols WHILE RETAINING OTHERS
        symbols = DateFormatSymbols(Locale.getDefault())
        symbols.amPmStrings = arrayOf("AM", "PM")
    }

    fun round(value: Double, places: Int): Double {
        var value = value
        if (places < 0) throw IllegalArgumentException()

        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value = value * factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }



    fun round(value: Double): String {
        var value = value
        val factor = Math.pow(10.0, 2.0).toLong()
        value = value * factor
        val tmp = Math.round(value)
        val valueV = tmp.toDouble() / factor
        return String.format(Locale.US, "%.2f", valueV)
    }

    /**
     * Check if two calendar objects has same date,month,year
     *
     * @param first
     * @param second
     * @return
     */
    fun matches(first: Calendar, second: Calendar): Boolean {
        val fDay = first.get(Calendar.DAY_OF_MONTH)
        val fMonth = first.get(Calendar.MONTH)
        val fYear = first.get(Calendar.YEAR)
        val sDay = second.get(Calendar.DAY_OF_MONTH)
        val sMonth = second.get(Calendar.MONTH)
        val sYear = second.get(Calendar.YEAR)
        return fDay == sDay && fMonth == sMonth && fYear == sYear
    }

    fun format(locale: Locale, datetime: String, @FORMAT inFormat: String, @FORMAT outFormat: String, isUtc: Boolean): String? {
        try {

            val input = SimpleDateFormat(inFormat, locale)
            val date = input.parse(datetime)

            val output = SimpleDateFormat(outFormat, locale)
            if (isUtc)
                output.timeZone = TimeZone.getTimeZone("UTC")
            output.dateFormatSymbols = symbols

            return output.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @JvmOverloads
    fun format(datetime: String, @FORMAT inFormat: String, @FORMAT outFormat: String, isUtc: Boolean = false): String? {
        return format(Locale.US, datetime, inFormat, outFormat, isUtc)
    }

    fun utcToLocal(datetime: String, @FORMAT inFormat: String, @FORMAT outFormat: String): String {
        val df = SimpleDateFormat(inFormat, Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = df.parse(datetime)
        val output = SimpleDateFormat(outFormat, Locale.ENGLISH)
        output.timeZone = TimeZone.getDefault()
        return output.format(date)
    }

    fun simpleConvert(datetime: String, @FORMAT inFormat: String, @FORMAT outFormat: String): String {
        val df = SimpleDateFormat(inFormat, Locale.ENGLISH)
        val date = df.parse(datetime)
        val output = SimpleDateFormat(outFormat, Locale.ENGLISH)
        output.timeZone = TimeZone.getDefault()
        return output.format(date)
    }

    @JvmOverloads
    fun format(datetime: Calendar, @FORMAT outFormat: String, isUtc: Boolean = false): String? {
        try {

            val date = datetime.time
            val output = SimpleDateFormat(outFormat, Locale.US)

            if (isUtc)
                output.timeZone = TimeZone.getTimeZone(inputTimeZone)
            else
                output.timeZone = TimeZone.getDefault()

            output.dateFormatSymbols = symbols

            return output.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun format(date: Date, @FORMAT outFormat: String): String? {
        try {
            val output = SimpleDateFormat(outFormat, Locale.US)
            output.dateFormatSymbols = symbols
            output.timeZone = TimeZone.getDefault()
            return output.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @JvmOverloads
    fun getCalendar(datetime: String, @FORMAT inFormat: String, isUtc: Boolean = false): Calendar? {
        try {
            val output = SimpleDateFormat(inFormat, Locale.getDefault())
            output.dateFormatSymbols = symbols
            if (isUtc)
                output.timeZone = TimeZone.getTimeZone("GMT")
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getDefault();
            val date = output.parse(datetime)
            if (date != null)
                calendar.time = date
            return calendar
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    fun isCheckPervious(context: Context, locale: Locale, formattedString: String,
                        datetime: String, @FORMAT inFormat: String,
                        isUtc: Boolean): Boolean {

        try {
            val date = Formatter.getCalendar(datetime, inFormat, isUtc)!!.time

            //Initialize both calendar with set time
            val calendarDate = Calendar.getInstance()
            calendarDate.time = date

            val current = Calendar.getInstance()
            // current.setTimeZone(TimeZone.getDefault());

            DebugLog.i("InsertDate::::" + calendarDate.time + " Converted server date")
            DebugLog.i("CurrentDate::::" + current.time)

            val difference = current.timeInMillis - calendarDate.timeInMillis


            val year = difference / YEAR
            val month = difference / MONTH
            val day = difference / DAY
            val week = difference / WEEK
            val hour = difference / HOUR
            val minute = difference / MINUTE
            val second = difference / SECOND

            DebugLog.d("Hours = $hour")
            if (hour >= 0) {
                return true
            }

        } catch (e: Exception) {
        }

        return false

    }


    fun diffMinutes(startTime: String): Long {
        var min: Long = 0
        val difference: Long
        val string2 = format(Date().toString(), YYYY_MM_DD_T_HH_MM_SSS_Z, HH_mm_ss, true).toString()
        try {
            val simpleDateFormat = SimpleDateFormat(HH_mm_ss, Locale.ENGLISH) // for 12-hour system, hh should be used instead of HH
            // There is no minute different between the two, only 8 hours difference. We are not considering Date, So minute will always remain 0
            val date1 = simpleDateFormat.parse(utcToLocal(startTime, YYYY_MM_DD_HH_MM_SS_NEW, HH_mm_ss))
            val date2 = simpleDateFormat.parse(string2)

            difference = (date2.time - date1.time) / 1000
            val hours = difference % (24 * 3600) / 3600 // Calculating Hours
            val minute = difference % 3600 / 60 // Calculating minutes if there is any minutes difference
            min = minute + hours * 60 // This will be our final minutes. Multiplying by 60 as 1 hour contains 60 mins

            Log.d("hlink", "difference$difference")
            Log.d("hlink", "min$min")

        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return min
    }

    fun printDifference(startDate: Date, endDate: Date, leftminte: String): Long {
        //milliseconds
        //long different = startDate.getTime() -(endDate.getTime()+ (30*60000));
        val different = endDate.time + Integer.parseInt(leftminte) * 60000 - startDate.time
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        return different / (1000 * 60) % 60
    }

    fun isPrevious(context: Context, locale: Locale, formattedString: String,
                   datetime: String, @FORMAT inFormat: String,
                   isUtc: Boolean): Boolean {

        try {
            val date = Formatter.getCalendar(datetime, inFormat, isUtc)!!.time

            //Initialize both calendar with set time
            val calendarDate = Calendar.getInstance()
            calendarDate.time = date

            val current = Calendar.getInstance()
            // current.setTimeZone(TimeZone.getDefault());

            DebugLog.i("InsertDate::::" + calendarDate.time + " Converted server date")
            DebugLog.i("CurrentDate::::" + current.time)

            val difference = current.timeInMillis - calendarDate.timeInMillis


            val year = difference / YEAR
            val month = difference / MONTH
            val day = difference / DAY
            val week = difference / WEEK
            val hour = difference / HOUR
            val minute = difference / MINUTE
            val second = difference / SECOND

            DebugLog.d("Hours = $hour")
            if (hour <= -2) {
                return true
            }

        } catch (e: Exception) {
        }

        return false
    }

    fun getTimeorDate(datetime: String, @FORMAT inFormat: String, isUtc: Boolean): String? {
        try {
            //Initialize both calendar with set time
            val calendarDate = Formatter.getCalendar(datetime, inFormat, isUtc)
            val current = Calendar.getInstance()

            return if (matches(calendarDate!!, current)) {
                format(calendarDate.time, hh_mm_aa)
            } else {
                format(calendarDate.time, CALL_LOG_TIME)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun getDateString(@FORMAT outFormat: String): String? {
        return format(Calendar.getInstance(), outFormat)
    }

    @JvmOverloads
    fun getTimeInMiliseconds(datetime: String, @FORMAT inFormat: String): Long {
        val output = SimpleDateFormat(inFormat, Locale.getDefault())
        val date = output.parse(datetime)
        return date.time
    }

    fun convert(timestamp: String): String? {
        var localTime: String? = null
        try {
            val time = java.lang.Long.valueOf(timestamp)
            val cal = Calendar.getInstance()
            val tz = cal.timeZone

            /* debug: is it local time? */
            // DebugLog.d("Time zone: ", tz.getDisplayName());

            /* date formatter in local timezone */
            val sdf = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS)
            sdf.timeZone = tz

            /* print your timestamp and double check it's the date you expect */
            // I assume your timestamp is in seconds and you're converting to milliseconds?
            localTime = sdf.format(Date(time * 1000))
            //DebugLog.d("Time: ", localTime);
            return localTime
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }

        return ""
    }

    fun resetSeconds(calendar: Calendar) {
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef(MM_DD_YYYY, YYYY_MM_DD, JAN_2017, DD_MMM_YYYY, MM_YY, YYYY_MM_DD_HH_MM_SS, YYYY_MM_DD_HH_MM, YYYY_MM_DD_T_HH_MM_SSS_Z, YYYY_MM_DD_hh_mm_aa, CALL_LOG_TIME, DD_MMMM_YYYY_hh_mm_aa, DD_MMM_YYYY_hh_mm_aa, DD_MM_YYYY, MM, MMM, MMMM, YYYY, DD_MMMM_YYYY, DD_MMMM_YYYY_FULL, HH_mm, HH_mm_ss, hh_mm_aa)
    annotation class FORMAT
}
