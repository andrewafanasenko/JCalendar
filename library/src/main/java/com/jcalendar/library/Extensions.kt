package com.jcalendar.library

import com.jcalendar.library.model.Day
import com.jcalendar.library.model.Week
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.absoluteValue

/**
 * Returns list of [Week]s related to [YearMonth] on which this function was invoked.
 * Each week will have 7 days, first and last week can contain days from previous or next month.
 * @param firstDayOfWeek day to be selected as first day in each week
 * @param selectedDate month selected date
 */
fun YearMonth.getMonthWeeks(
    firstDayOfWeek: DayOfWeek,
    selectedDate: LocalDate
): List<Week> {
    val dates = mutableListOf<LocalDate>()
    val weeks = mutableListOf<Week>()

    val currentMonthDates = getMonthDatesList()
    dates.addAll(currentMonthDates)

    val daysOfWeekSorted = firstDayOfWeek.getSortedDaysOfWeek()

    val shift = daysOfWeekSorted.first().value - currentMonthDates.first().dayOfWeek.value

    val previousMonthDates = minusMonths(1).getMonthDatesList()
    if (shift.absoluteValue > 0) {
        dates.addAll(0, previousMonthDates.takeLast(shift.absoluteValue))
    }
    val daysToGetFromNextMonth = dates.count() % daysOfWeekSorted.count()
    val daysFromNextMonth = if (daysToGetFromNextMonth > 0) {
        daysOfWeekSorted.count() - daysToGetFromNextMonth
    } else {
        0
    }

    val nextMonthDates = plusMonths(1).getMonthDatesList()
    if (daysFromNextMonth > 0) {
        dates.addAll(nextMonthDates.take(daysFromNextMonth))
    }

    dates.chunked(daysOfWeekSorted.count()).map { datesInWeek ->
        weeks.add(
            Week(
                days = daysOfWeekSorted.map { dayOfWeek ->
                    val date = datesInWeek.first { it.dayOfWeek == dayOfWeek }
                    Day(
                        dayOfWeek = dayOfWeek,
                        date = date,
                        isSelected = selectedDate == date,
                        isOutDay = previousMonthDates.contains(date) ||
                                nextMonthDates.contains(date)
                    )
                }
            )
        )
    }
    return weeks
}

/**
 * Returns list of [LocalDate]s related to [YearMonth] on which this function was invoked
 */
fun YearMonth.getMonthDatesList(): List<LocalDate> {
    var date = atDay(1)
    val monthEndDate = date.plusMonths(1).withDayOfMonth(1)
    val monthDates = mutableListOf<LocalDate>()
    while (date.isBefore(monthEndDate)) {
        monthDates.add(date)
        date = date.plusDays(1)
    }
    return monthDates
}

/**
 * Returns list of sorted [DayOfWeek]s based on [DayOfWeek] on which this function was invoked
 *
 * Example DayOfWeek.TUESDAY.getSortedDaysOfWeek() will return
 * [TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, MONDAY]
 */
fun DayOfWeek.getSortedDaysOfWeek(): List<DayOfWeek> {
    val daysOfWeek = DayOfWeek.values().toMutableList()
    val startDayIndex = daysOfWeek.indexOf(this)
    val daysBeforeStart = daysOfWeek.subList(0, startDayIndex)
    val daysAfterStart = daysOfWeek.subList(startDayIndex, daysOfWeek.size)
    return daysAfterStart + daysBeforeStart
}
