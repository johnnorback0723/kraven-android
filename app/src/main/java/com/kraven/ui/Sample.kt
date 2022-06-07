package com.kraven.ui

import java.text.SimpleDateFormat
import java.util.*

fun main() {
    val timingList = mutableListOf<Timing>()

    val timingNewList = mutableListOf<Timing>()

    timingList.add(Timing(2208, "18:00:00", "00:00:00", "2019-09-18"))
    timingList.add(Timing(2206, "00:00:00", "18:00:00", "2019-09-19"))
    timingList.add(Timing(2207, "20:00:00", "00:00:00", "2019-09-19"))
    timingList.add(Timing(2209, "00:00:00", "17:00:00", "2019-09-20"))


    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val calenderYe = Calendar.getInstance()
    calenderYe.add(Calendar.DATE, -1)
    val dateYe = formatter.format(calenderYe.time)
   // print(dateYe)

    val calenderTo = Calendar.getInstance()
    calenderTo.add(Calendar.DATE, 1)
    val dateTo = formatter.format(calenderTo.time)

    val calendatToday = Calendar.getInstance()
    val dateToday = formatter.format(calendatToday.time)

    val findYesterday = timingList.filter{  dateYe ==it.date && it.endTime=="00:00:00"}
    val findTomorrow = timingList.filter { dateTo ==it.date && it.startTime=="00:00:00" }
    val findTodayStartTime = timingList.filter { dateToday == it.date && it.startTime == "00:00:00" && findYesterday.isNotEmpty()}
    //print(findYesterday)
    if(findYesterday.isNotEmpty()){
        timingList.remove(findYesterday[0])
    }
    if(findTodayStartTime.isNotEmpty()){
        timingList.remove(findTodayStartTime[0])
    }

    if(findTomorrow.isNotEmpty()){
        timingList.remove(findTomorrow[0])
    }

    //print(timingList)
    timingList.forEachIndexed { index, it ->
        /*if (dateToday == it.date && it.startTime == "00:00:00" && findYesterday.isNotEmpty()) {
            *//* val timing = Timing()
             timing.startTime = findYesterday.get(index = 0).startTime
             timing.endTime = it.endTime
             timingNewList.add(timing)*//*
            timingList.removeAt(index)
            print(timingList)

        } else*/ if (dateToday ==it.date && it.endTime=="00:00:00" && findTomorrow.isNotEmpty()) {
            val timing = Timing()
            timing.startTime = it.startTime
            timing.endTime = findTomorrow.get(index = 0).endTime
            timingNewList.add(timing)
        }else{
            val timing = Timing()
            timing.startTime = it.startTime
            timing.endTime = it.endTime
            timingNewList.add(timing)
        }
    }

    /*timingList.forEach {

        if (dateToday == it.date && it.startTime == "00:00:00" && findYesterday.isNotEmpty()) {
           *//* val timing = Timing()
            timing.startTime = findYesterday.get(index = 0).startTime
            timing.endTime = it.endTime
            timingNewList.add(timing)*//*
            timingList.remove(it)

        } else if (dateToday ==it.date && it.endTime=="00:00:00" && findTomorrow.isNotEmpty()) {
            val timing = Timing()
            timing.startTime = it.startTime
            timing.endTime = findTomorrow.get(index = 0).endTime
            timingNewList.add(timing)
        }else{
            val timing = Timing()
            timing.startTime = it.startTime
            timing.endTime = it.endTime
            timingNewList.add(timing)
        }
    }*/
    print(timingNewList)
    /* // Get yesterday
     val yesterdayTiming = getYesterday(timingList)
     println("Found Yesterday: ${yesterdayTiming?.id}")
     timingList.remove(yesterdayTiming)
 
     val tomorrowTiming = getTomorrow(timingList)
     println("Found Tomorrow: ${tomorrowTiming?.id}")
     timingList.remove(tomorrowTiming)
 
     if (yesterdayTiming != null) {
         val iterator = timingList.listIterator()
 
         while (iterator.hasNext()) {
             val timing = iterator.next()
             if (timing.startTime == yesterdayTiming.endTime) {
                 iterator.remove()
             }
         }
     }
 
     if(tomorrowTiming != null) {
         timingList.forEachIndexed { index, t: Timing? ->
             if (t!!.endTime.equals(tomorrowTiming.startTime)) {
                 t.endTime = tomorrowTiming.endTime
             }
         }
     }*/



}

fun getYesterday(timingList: List<Timing>): Timing? {
    var timing: Timing? = null
    var calenderToday = Calendar.getInstance()
    calenderToday.set(Calendar.HOUR_OF_DAY, 0);
    calenderToday.set(Calendar.MINUTE, 0);
    calenderToday.set(Calendar.SECOND, 0);
    calenderToday.set(Calendar.MILLISECOND, 0);

    var calendarSecond = Calendar.getInstance()

    // calenderToday.set(calenderToday.get(Calendar.YEAR),calenderToday.get(Calendar.MONTH),calenderToday.get(Calendar.DAY_OF_MONTH),0,0,0)


    for (obj in timingList) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.parse(obj.date)
        calendarSecond.time = date
        //calendarSecond.set(obj.date.split("-")[0].toInt(),obj.date.split("-")[1].toInt(),obj.date.split("-")[2].toInt(),0,0,0)
        calendarSecond.set(Calendar.HOUR_OF_DAY, 0);
        calendarSecond.set(Calendar.MINUTE, 0);
        calendarSecond.set(Calendar.SECOND, 0);
        calendarSecond.set(Calendar.MILLISECOND, 0);
        if (calenderToday.time.after(calendarSecond.time)) {
            timing = obj
            break
        }
    }

    return timing
}

fun getTomorrow(timingList: List<Timing>): Timing? {
    var timing: Timing? = null
    var calenderToday = Calendar.getInstance()
    calenderToday.set(Calendar.HOUR_OF_DAY, 0);
    calenderToday.set(Calendar.MINUTE, 0);
    calenderToday.set(Calendar.SECOND, 0);
    calenderToday.set(Calendar.MILLISECOND, 0);

    var calendarSecond = Calendar.getInstance()

    // calenderToday.set(calenderToday.get(Calendar.YEAR),calenderToday.get(Calendar.MONTH),calenderToday.get(Calendar.DAY_OF_MONTH),0,0,0)


    for (obj in timingList) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.parse(obj.date)
        calendarSecond.time = date
        //calendarSecond.set(obj.date.split("-")[0].toInt(),obj.date.split("-")[1].toInt(),obj.date.split("-")[2].toInt(),0,0,0)
        calendarSecond.set(Calendar.HOUR_OF_DAY, 0);
        calendarSecond.set(Calendar.MINUTE, 0);
        calendarSecond.set(Calendar.SECOND, 0);
        calendarSecond.set(Calendar.MILLISECOND, 0);
        if (calenderToday.time.before(calendarSecond.time)) {
            timing = obj
            break
        }
    }

    return timing
}

data class Timing(
        var id: Long? = null,
        var startTime: String? = null,
        var endTime: String? = null,
        var date: String? = null
)