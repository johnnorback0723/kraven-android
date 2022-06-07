package com.kraven.extensions

import android.util.Log
import com.kraven.ui.home.model.Timing
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

fun getYesterday(timingList: List<Timing>): Timing? {
    var timing: Timing? = null
    val calenderToday = Calendar.getInstance()
    calenderToday.set(Calendar.HOUR_OF_DAY, 0)
    calenderToday.set(Calendar.MINUTE, 0)
    calenderToday.set(Calendar.SECOND, 0)
    calenderToday.set(Calendar.MILLISECOND, 0)

    val calendarSecond = Calendar.getInstance()

    for (obj in timingList) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val date = formatter.format(cal.time)
        val newDate = formatter.parse(date)
        calendarSecond.time = newDate
        calendarSecond.set(Calendar.HOUR_OF_DAY, 0)
        calendarSecond.set(Calendar.MINUTE, 0)
        calendarSecond.set(Calendar.SECOND, 0)
        calendarSecond.set(Calendar.MILLISECOND, 0)
        if (calenderToday.time.after(calendarSecond.time)) {
            timing = obj
            break
        }
    }

    return timing

}

fun getTomorrow(timingList: List<Timing>): Timing? {
    var timing: Timing? = null
    val calenderToday = Calendar.getInstance()
    calenderToday.set(Calendar.HOUR_OF_DAY, 0)
    calenderToday.set(Calendar.MINUTE, 0)
    calenderToday.set(Calendar.SECOND, 0)
    calenderToday.set(Calendar.MILLISECOND, 0)

    val calendarSecond = Calendar.getInstance()


    for (obj in timingList) {
        /* val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
         val date = formatter.parse(obj.date)
         calendarSecond.time = date*/

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        val date = formatter.format(cal.time)
        val newDate = formatter.parse(date)
        calendarSecond.time = newDate
        calendarSecond.set(Calendar.HOUR_OF_DAY, 0)
        calendarSecond.set(Calendar.MINUTE, 0)
        calendarSecond.set(Calendar.SECOND, 0)
        calendarSecond.set(Calendar.MILLISECOND, 0)
        if (calenderToday.time.before(calendarSecond.time)) {
            timing = obj
            break
        }
    }

    return timing
}


fun getList(timingList: ArrayList<Timing>): ArrayList<Timing> {

    val timingNewList = ArrayList<Timing>()
    val tempList = ArrayList<Timing>()
    tempList.addAll(timingList)

    val formatter = SimpleDateFormat("EEEE", Locale.US)

    val calenderYe = Calendar.getInstance(Locale.US)
    calenderYe.add(Calendar.DATE, -1)
    val dateYe = formatter.format(calenderYe.time)

    val calenderTo = Calendar.getInstance(Locale.US)
    calenderTo.add(Calendar.DATE, 1)
    val dateTo = formatter.format(calenderTo.time)

    val calenderToday = Calendar.getInstance(Locale.US)
    val dateToday = formatter.format(calenderToday.time)

    val findYesterday = timingList.filter { dateYe == it.day && it.endTime == "00:00:00" }
    val findTomorrow = timingList.filter { dateTo == it.day && it.startTime == "00:00:00" }

    val findTodayStartTime = timingList.filter { dateToday == it.day && it.startTime == "00:00:00" && findYesterday.isNotEmpty() }
    val findTodayEndTime = timingList.filter { dateToday == it.day && it.endTime == "00:00:00" && findTomorrow.isNotEmpty() }

    if (findYesterday.isNotEmpty()) {
        timingList.remove(findYesterday[0])
    }

    if (findTomorrow.isNotEmpty()) {
        timingList.remove(findTomorrow[0])
    }

    if (timingList.isNotEmpty()) {
        timingList.forEach {
            if (dateToday == it.day && it.startTime == "00:00:00" && findYesterday.isNotEmpty() && findTodayStartTime.isNotEmpty() && findTodayStartTime[0].startTime == "00:00:00") {
                val timing = Timing()
                timing.id = it.id
                timing.restaurantId = it.restaurantId
                timing.startTime = findYesterday[0].startTime
                timing.endTime = findTodayStartTime[0].endTime
                timing.status = it.status
                timingNewList.add(timing)
            } else if (dateToday == it.day && it.endTime == "00:00:00" && findTomorrow.isNotEmpty() && findTodayEndTime.isNotEmpty() && findTodayEndTime[0].endTime == "00:00:00") {
                val timing = Timing()
                timing.startTime = it.startTime
                timing.status = it.status
                timing.endTime = findTomorrow[0].endTime
                timingNewList.add(timing)
            } else {
                val timing = Timing()
                timing.startTime = it.startTime
                timing.endTime = it.endTime
                timing.status = it.status
                timingNewList.add(timing)
            }
        }

    } else {
        tempList.forEach {
            val timing = Timing()
            timing.startTime = it.startTime
            timing.endTime = it.endTime
            timing.status = it.status
            timingNewList.add(timing)
        }
    }
    return timingNewList

}
