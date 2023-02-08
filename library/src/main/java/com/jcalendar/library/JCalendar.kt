package com.jcalendar.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.jcalendar.library.model.Month
import com.jcalendar.library.model.Week
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun JCalendar(
    modifier: Modifier = Modifier,
    startMonth: YearMonth = YearMonth.now(),
    endMonth: YearMonth = YearMonth.now(),
    currentDate: LocalDate = LocalDate.now(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
) {
    if (startMonth.isAfter(endMonth)) {
        throw RuntimeException("End month should be greater or equal to start month")
    }
    val currentYearMonth = YearMonth.from(currentDate)
    if (currentYearMonth.isBefore(startMonth) || currentYearMonth.isAfter(endMonth)) {
        throw RuntimeException("Current date should be in startMonth..endMonth range")
    }
    Box(modifier = modifier) {
        val months = getMonths(
            startMonth = startMonth,
            endMonth = endMonth,
            currentDate = currentDate,
            firstDayOfWeek = firstDayOfWeek
        )
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            count = months.count(),
            verticalAlignment = Alignment.Top
        ) {
            MonthContent(months[it])
        }
    }
}

@Composable
fun MonthContent(month: Month) {
    Column(modifier = Modifier.fillMaxWidth()) {
        month.weeks.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                it.days.forEach {
                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = it.value.dayOfMonth.toString()
                        )
                    }
                }
            }
        }
    }
}

fun getMonths(
    startMonth: YearMonth,
    endMonth: YearMonth,
    currentDate: LocalDate,
    firstDayOfWeek: DayOfWeek
): List<Month> {
    var month = startMonth
    val months = mutableListOf<YearMonth>()
    while (month.isBefore(endMonth)) {
        months.add(month)
        month = month.plusMonths(1)
    }
    return months.map { Month(it.getMonthWeeks(firstDayOfWeek)) }
}

fun YearMonth.getMonthWeeks(firstDayOfWeek: DayOfWeek): List<Week> {
    val dates = mutableListOf<LocalDate>()
    val daysOfWeek = DayOfWeek.values().toMutableList()
    val startDayIndex = daysOfWeek.indexOf(firstDayOfWeek)
    val daysBeforeStart = daysOfWeek.subList(0, startDayIndex)
    val daysAfterStart = daysOfWeek.subList(startDayIndex, daysOfWeek.size)
    val daysOfWeekSorted = daysAfterStart + daysBeforeStart
    val weeks = mutableListOf<Week>()

    val currentMonthDates = getMonthDatesList()
    dates.addAll(currentMonthDates)

    val shift = daysOfWeekSorted.first().value - currentMonthDates.first().dayOfWeek.value
    if (shift.absoluteValue > 0) {
        val previousMonthDates = minusMonths(1).getMonthDatesList()
        dates.addAll(0, previousMonthDates.takeLast(shift.absoluteValue))
    }
    val daysToGetFromNextMonth = dates.count() % daysOfWeekSorted.count()
    val daysFromNextMonth = if (daysToGetFromNextMonth > 0) {
        daysOfWeekSorted.count() - daysToGetFromNextMonth
    } else {
        0
    }
    if (daysFromNextMonth > 0) {
        val nextMonthDates = plusMonths(1).getMonthDatesList()
        dates.addAll(nextMonthDates.take(daysFromNextMonth))
    }

    dates.chunked(daysOfWeekSorted.count()).map { datesInWeek ->
        weeks.add(
            Week(
                days = daysOfWeekSorted.mapIndexed { index, dayOfWeek ->
                    dayOfWeek to datesInWeek.first { it.dayOfWeek == dayOfWeek }
                }.toMap()
            )
        )
    }
    return weeks
}


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