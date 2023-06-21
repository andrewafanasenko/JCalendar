package com.jcalendar.library

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.jcalendar.library.model.CalendarMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


class JCalendarSaver {
    companion object {

        val Saver: Saver<JCalendarState, *> = listSaver(
            save = { calendarState: JCalendarState ->
                listOf(
                    calendarState.startMonth,
                    calendarState.endMonth,
                    calendarState.selectedDate,
                    calendarState.firstDayOfWeek,
                    calendarState.mode
                )
            },
            restore = { restorationList: List<Any?> ->
                JCalendarState(
                    startMonth = restorationList[0] as YearMonth,
                    endMonth = restorationList[1] as YearMonth,
                    selectedDate = restorationList[2] as LocalDate,
                    firstDayOfWeek = restorationList[3] as DayOfWeek,
                    mode = restorationList[4] as CalendarMode
                )
            }
        )
    }
}
