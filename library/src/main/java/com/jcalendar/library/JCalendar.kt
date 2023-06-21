package com.jcalendar.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jcalendar.library.model.CalendarMode
import com.jcalendar.library.model.Day
import com.jcalendar.library.model.Month
import com.jcalendar.library.model.Week
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale


/**
 * Swipeable calendar with month and week modes
 * @param modifier The modifier to be applied to the component.
 * @param calendarState The calendarState to adjust calendar setting or select day.
 * @param dayContent A block which describes the content of month date. By default [DayContent]
 * is used, which can be customized or replaced with another composable.
 * @param outDayContent A block which describes the content of day from previous or next month
 * (if exist). Only applicable for [CalendarMode.MONTH] mode. By default modified [DayContent]
 * is used, which can be customized or replaced with another composable.
 * @param dayOfWeekTitleContent A block which describes the content of day title
 * (e.g. M, T, W .. etc). By default modified [DayOfWeekTitleContent] is used, which can be
 * customized or replaced with another composable.
 */
@Composable
fun JCalendar(
    modifier: Modifier = Modifier,
    calendarState: JCalendarState = rememberJCalendarState(),
    dayContent: @Composable (Day) -> Unit = { day: Day ->
        DayContent(day) {
            calendarState.selectDay(day)
        }
    },
    outDayContent: (@Composable (Day) -> Unit)? = { day: Day ->
        DayContent(day = day, defaultTextColor = Color.Gray) {
            calendarState.selectDay(day)
        }
    },
    dayOfWeekTitleContent: (@Composable (DayOfWeek) -> Unit)? = { dayOfWeek: DayOfWeek ->
        DayOfWeekTitleContent(dayOfWeek)
    },
) {
    Column(modifier = modifier) {
        when (calendarState.mode) {
            CalendarMode.MONTH -> {
                MonthCalendar(
                    calendarState = calendarState,
                    dayOfWeekTitleContent = dayOfWeekTitleContent,
                    dayContent = dayContent,
                    outDayContent = outDayContent
                )
            }

            CalendarMode.WEEK -> {
                WeekCalendar(
                    calendarState = calendarState,
                    dayOfWeekTitleContent = dayOfWeekTitleContent,
                    dayContent = dayContent
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun MonthCalendar(
    calendarState: JCalendarState,
    dayOfWeekTitleContent: @Composable ((DayOfWeek) -> Unit)?,
    dayContent: @Composable (Day) -> Unit,
    outDayContent: @Composable ((Day) -> Unit)?
) {
    val pagerState = rememberPagerState(initialPage = calendarState.scrollPosition)

    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        state = pagerState,
        count = calendarState.months.count(),
        verticalAlignment = Alignment.Top
    ) {
        Column {
            dayOfWeekTitleContent?.let {
                DayOfWeekTitlesContent(
                    firstDayOfWeek = calendarState.firstDayOfWeek,
                    dayOfWeekTitleContent = dayOfWeekTitleContent
                )
            }
            MonthContent(
                month = calendarState.months[it],
                dayContent = dayContent,
                outDayContent = outDayContent,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun WeekCalendar(
    calendarState: JCalendarState,
    dayOfWeekTitleContent: @Composable ((DayOfWeek) -> Unit)?,
    dayContent: @Composable (Day) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = calendarState.scrollPosition)

    HorizontalPager(
        modifier = Modifier.fillMaxWidth(),
        state = pagerState,
        count = calendarState.weeks.count(),
        verticalAlignment = Alignment.Top
    ) {
        Column {
            dayOfWeekTitleContent?.let {
                DayOfWeekTitlesContent(
                    firstDayOfWeek = calendarState.firstDayOfWeek,
                    dayOfWeekTitleContent = dayOfWeekTitleContent
                )
            }
            WeekContent(
                week = calendarState.weeks[it],
                dayContent = dayContent
            )
        }
    }
}


@Composable
private fun DayOfWeekTitlesContent(
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    dayOfWeekTitleContent: @Composable (DayOfWeek) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        firstDayOfWeek.getSortedDaysOfWeek().forEach {
            Box(modifier = Modifier.weight(1f)) {
                dayOfWeekTitleContent.invoke(it)
            }
        }
    }
}

@Composable
private fun MonthContent(
    month: Month,
    dayContent: @Composable (Day) -> Unit,
    outDayContent: @Composable ((Day) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        month.weeks.forEach {
            WeekContent(it, dayContent, outDayContent)
        }
    }
}

@Composable
private fun WeekContent(
    week: Week,
    dayContent: @Composable (Day) -> Unit,
    outDayContent: @Composable ((Day) -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        week.days.forEach { day ->
            Box(modifier = Modifier.weight(1f)) {
                if (day.isOutDay && outDayContent != null) {
                    outDayContent.invoke(day)
                } else {
                    dayContent.invoke(day)
                }
            }
        }
    }
}

@Composable
fun DayContent(
    day: Day,
    modifier: Modifier = Modifier,
    defaultTextColor: Color = Color.Black,
    selectedTextColor: Color = Color.White,
    textStyle: TextStyle = LocalTextStyle.current,
    height: Dp = Dp.Unspecified,
    onClick: (LocalDate) -> Unit = {}
) {
    val dayHeight = if (height == Dp.Unspecified) {
        Modifier.aspectRatio(1f)
    } else {
        Modifier.height(height)
    }
    Box(
        modifier = Modifier
            .background(
                color = if (day.isSelected) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.surface
                },
                shape = CircleShape
            )
            .then(dayHeight)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = { onClick.invoke(day.date) }
            )
            .then(modifier)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            text = day.date.dayOfMonth.toString(),
            textAlign = TextAlign.Center,
            style = textStyle,
            color = if (day.isSelected) selectedTextColor else defaultTextColor
        )
    }
}

@Composable
fun DayOfWeekTitleContent(
    dayOfWeek: DayOfWeek,
    textColor: Color = Color.DarkGray,
    textStyle: TextStyle = LocalTextStyle.current
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = dayOfWeek.getDisplayName(java.time.format.TextStyle.NARROW, Locale.getDefault()),
        textAlign = TextAlign.Center,
        color = textColor,
        style = textStyle
    )
}

@Composable
fun rememberJCalendarState(
    startMonth: YearMonth = YearMonth.now(),
    endMonth: YearMonth = startMonth,
    selectedDate: LocalDate = LocalDate.now(),
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    mode: CalendarMode = CalendarMode.MONTH,
    onDateSelected: (LocalDate) -> Unit = {}
): JCalendarState {
    return rememberSaveable(
        startMonth, endMonth, selectedDate, firstDayOfWeek, mode, onDateSelected,
        saver = JCalendarSaver.Saver
    ) {
        JCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            selectedDate = selectedDate,
            firstDayOfWeek = firstDayOfWeek,
            mode = mode,
            onDateSelected = onDateSelected
        )
    }
}
