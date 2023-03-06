package com.jcalendar.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.jcalendar.library.model.Day
import com.jcalendar.library.model.Month
import com.jcalendar.library.model.Week
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun JCalendar(
    modifier: Modifier = Modifier,
    calendarState: JCalendarState = rememberJCalendarState(),
) {
    if (calendarState.startMonth.isAfter(calendarState.endMonth)) {
        throw RuntimeException("End month should be greater or equal to start month")
    }
    val currentYearMonth = YearMonth.from(calendarState.selectedDate)
    if (currentYearMonth.isBefore(calendarState.startMonth) || currentYearMonth.isAfter(
            calendarState.endMonth
        )
    ) {
        throw RuntimeException("Current date should be within startMonth..endMonth range")
    }
    Box(modifier = modifier) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            count = calendarState.months.count(),
            verticalAlignment = Alignment.Top
        ) {
            MonthContent(calendarState.months[it], calendarState)
        }
    }
}

@Composable
fun MonthContent(month: Month, calendarState: JCalendarState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        month.weeks.forEach {
            WeekContent(it, calendarState)
        }
    }
}

@Composable
fun WeekContent(week: Week, calendarState: JCalendarState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        week.days.forEach {
            DayContent(it, calendarState)
        }
    }
}

@Composable
fun RowScope.DayContent(day: Day, calendarState: JCalendarState) {
    Box(
        modifier = Modifier
            .weight(1f)
            .background(if (day.isSelected) Color.Cyan else Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = { calendarState.selectDay(day) }
            )
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = day.date.dayOfMonth.toString()
        )
    }
}

@Composable
fun rememberJCalendarState(
    startMonth: YearMonth = YearMonth.now(),
    endMonth: YearMonth = startMonth,
    selectedDate: LocalDate = LocalDate.now()
): JCalendarState {
    return rememberSaveable(
        startMonth, endMonth, selectedDate,
        saver = JCalendarSaver.Saver
    ) {
        JCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            selectedDate = selectedDate
        )
    }
}

data class JCalendarState(
    val startMonth: YearMonth = YearMonth.now(),
    val endMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY
) {

    val months: List<Month>

    init {
        months = getMonths(startMonth, endMonth, selectedDate, firstDayOfWeek)
    }

    private fun getMonths(
        startMonth: YearMonth,
        endMonth: YearMonth,
        selectedDate: LocalDate,
        firstDayOfWeek: DayOfWeek
    ): List<Month> {
        var month = startMonth
        val months = mutableListOf<YearMonth>()
        if (month == endMonth) {
            months.add(month)
        } else {
            while (month.isBefore(endMonth)) {
                months.add(month)
                month = month.plusMonths(1)
            }
        }
        return months.map { Month(it.getMonthWeeks(firstDayOfWeek, selectedDate)) }
    }

    private fun YearMonth.getMonthWeeks(
        firstDayOfWeek: DayOfWeek,
        selectedDate: LocalDate
    ): List<Week> {
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
                    days = daysOfWeekSorted.map { dayOfWeek ->
                        val date = datesInWeek.first { it.dayOfWeek == dayOfWeek }
                        Day(
                            dayOfWeek = dayOfWeek,
                            date = date,
                            isSelected = selectedDate == date
                        )
                    }
                )
            )
        }
        return weeks
    }

    private fun YearMonth.getMonthDatesList(): List<LocalDate> {
        var date = atDay(1)
        val monthEndDate = date.plusMonths(1).withDayOfMonth(1)
        val monthDates = mutableListOf<LocalDate>()
        while (date.isBefore(monthEndDate)) {
            monthDates.add(date)
            date = date.plusDays(1)
        }
        return monthDates
    }

    fun selectDay(day: Day) {

    }
}

class JCalendarSaver {
    companion object {

        val Saver: Saver<JCalendarState, *> = listSaver(
            save = { calendarState: JCalendarState ->
                listOf(calendarState.startMonth, calendarState.endMonth, calendarState.selectedDate)
            },
            restore = { restorationList: List<Any?> ->
                JCalendarState(
                    startMonth = restorationList[0] as YearMonth,
                    endMonth = restorationList[1] as YearMonth,
                    selectedDate = restorationList[2] as LocalDate

                )
            }
        )
    }
}