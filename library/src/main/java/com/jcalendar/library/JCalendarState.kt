package com.jcalendar.library

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jcalendar.library.model.CalendarMode
import com.jcalendar.library.model.Day
import com.jcalendar.library.model.Month
import com.jcalendar.library.model.Week
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class JCalendarState(
    val startMonth: YearMonth = YearMonth.now(),
    val endMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val mode: CalendarMode = CalendarMode.MONTH,
    val onDateSelected: (LocalDate) -> Unit = {}
) {

    var months by mutableStateOf(listOf<Month>())
    var weeks by mutableStateOf(listOf<Week>())
    var scrollPosition by mutableStateOf(0)

    init {
        require(startMonth.isAfter(endMonth).not()) {
            "End month should be greater or equal to start month"
        }
        val currentYearMonth = YearMonth.from(selectedDate)
        require(currentYearMonth.isBefore(startMonth).not()) {
            "Selected date should be within startMonth..endMonth range"
        }
        require(currentYearMonth.isAfter(endMonth).not()) {
            "Selected date should be within startMonth..endMonth range"
        }
        months = getMonths(startMonth, endMonth, selectedDate, firstDayOfWeek)
        weeks = months.map { it.weeks }.flatten().toSet().toList()

        when (mode) {
            CalendarMode.MONTH -> {
                months.indexOfFirst { month ->
                    month.weeks.any { week ->
                        week.days.any { day ->
                            day.isSelected
                        }
                    }
                }.let {
                    scrollPosition = if (it == -1) 0 else it
                }
            }

            CalendarMode.WEEK -> {
                weeks.indexOfFirst { week ->
                    week.days.any { day ->
                        day.isSelected
                    }
                }.let {
                    scrollPosition = if (it == -1) 0 else it
                }
            }
        }
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
            while (month.isBefore(endMonth.plusMonths(1))) {
                months.add(month)
                month = month.plusMonths(1)
            }
        }
        return months.map { Month(it.getMonthWeeks(firstDayOfWeek, selectedDate)) }
    }

    fun selectDay(selectedDay: Day) {
        fun Week.markSelectedDay() = days.map { day ->
            onDateSelected.invoke(selectedDay.date)
            day.copy(isSelected = day.date == selectedDay.date)
        }

        when (mode) {
            CalendarMode.MONTH -> {
                months = months.map { month ->
                    month.copy(
                        weeks = month.weeks.map { week ->
                            week.copy(days = week.markSelectedDay())
                        }
                    )
                }
            }

            CalendarMode.WEEK -> {
                weeks = weeks.map { week ->
                    week.copy(days = week.markSelectedDay())
                }
            }
        }
    }
}
