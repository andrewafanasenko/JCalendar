package com.jcalendar.library

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import com.jcalendar.library.model.CalendarMode
import com.jcalendar.library.model.Day
import com.jcalendar.library.model.Month
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Stable
class JCalendarState constructor(
    startMonth: YearMonth = YearMonth.now(),
    endMonth: YearMonth = YearMonth.now(),
    selectedDate: LocalDate = LocalDate.now(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    calendarMode: CalendarMode = CalendarMode.MONTH,
) {

    private val startMonth by mutableStateOf(startMonth)
    private val endMonth by mutableStateOf(endMonth)
    val firstDayOfWeek by mutableStateOf(firstDayOfWeek)
    val calendarMode by mutableStateOf(calendarMode)
    var currentMonth: YearMonth by mutableStateOf(YearMonth.of(selectedDate.year, selectedDate.month))
        private set

    var selectedDate: LocalDate by mutableStateOf(selectedDate)
        private set


    val months by derivedStateOf {
        getMonths(this.startMonth, this.endMonth, this.selectedDate, this.firstDayOfWeek)

    }

    val weeks by derivedStateOf {
        getMonths(this.startMonth, this.endMonth, this.selectedDate, this.firstDayOfWeek)
            .asSequence()
            .map { it.weeks }
            .flatten()
            .toSet()
            .distinctBy { week ->
                week.days.map { day -> day.date }.joinToString()
            }
            .toList()
    }

    val scrollPosition by derivedStateOf {
        when (this.calendarMode) {
            CalendarMode.MONTH -> {
                months.indexOfFirst { month ->
                    month.weeks.any { week ->
                        week.days.any { day ->
                            day.isSelected
                        }
                    }
                }.let {
                    if (it == -1) 0 else it
                }
            }

            CalendarMode.WEEK -> {
                weeks.indexOfFirst { week ->
                    week.days.any { day ->
                        day.isSelected
                    }
                }.let {
                    if (it == -1) 0 else it
                }
            }
        }
    }


    var scrollForward: () -> Unit = {}
    var scrollBack: () -> Unit = {}

    init {
        require(startMonth.isAfter(endMonth).not()) {
            "End month should be greater or equal to start month"
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
        selectedDate = selectedDay.date
    }

    fun selectMonth(month: YearMonth) {
        currentMonth = month
    }

    fun scrollForward() {
        scrollForward.invoke()
    }

    fun scrollBack() {
        scrollBack.invoke()
    }

    companion object {

        val Saver: Saver<JCalendarState, *> = listSaver(
            save = {
                listOf(
                    it.startMonth,
                    it.endMonth,
                    it.selectedDate,
                    it.firstDayOfWeek,
                    it.calendarMode
                )
            },
            restore = { restorationList: List<Any?> ->
                JCalendarState(
                    startMonth = restorationList[0] as YearMonth,
                    endMonth = restorationList[1] as YearMonth,
                    selectedDate = restorationList[2] as LocalDate,
                    firstDayOfWeek = restorationList[3] as DayOfWeek,
                    calendarMode = restorationList[4] as CalendarMode
                )
            }
        )
    }
}
