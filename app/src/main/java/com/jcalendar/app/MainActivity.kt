package com.jcalendar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jcalendar.app.ui.theme.JCalendarTheme
import com.jcalendar.library.DayContent
import com.jcalendar.library.DayOfWeekTitleContent
import com.jcalendar.library.JCalendar
import com.jcalendar.library.model.CalendarMode
import com.jcalendar.library.model.Day
import com.jcalendar.library.rememberJCalendarState
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JCalendarTheme(darkTheme = false) {
                Calendars()
            }
        }
    }
}

@Composable
private fun Calendars() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item { DefaultMonthCalendar() }
        item { DefaultWeekCalendar() }
        item { MonthCalendarWithoutTitle() }
        item { WeekCalendarWithoutTitle() }
        item { MonthCalendarWithoutOutDates() }
        item { MonthCalendarWithModifiedDay() }
    }

}

@Composable
private fun DefaultMonthCalendar() {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val currentMonth = YearMonth.now()
    val calendarState = rememberJCalendarState(
        startMonth = currentMonth.minusMonths(3),
        endMonth = currentMonth.plusMonths(3),
        mode = CalendarMode.MONTH
    )
    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.selectedDate }
            .distinctUntilChanged()
            .collect {
                selectedDay = it
            }
    }

    CalendarContainer(
        title = "Default month calendar",
        selectedDay = selectedDay,
        calendar = {
            JCalendar(calendarState = calendarState)
        }
    )
}

@Composable
private fun DefaultWeekCalendar() {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val currentMonth = YearMonth.now()
    val calendarState = rememberJCalendarState(
        startMonth = currentMonth.minusMonths(3),
        endMonth = currentMonth.plusMonths(3),
        mode = CalendarMode.WEEK,
    )

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.selectedDate }
            .distinctUntilChanged()
            .collect {
                selectedDay = it
            }
    }

    CalendarContainer(
        title = "Default week calendar",
        selectedDay = selectedDay,
        calendar = {
            JCalendar(calendarState = calendarState)
        }
    )
}

@Composable
private fun MonthCalendarWithoutTitle() {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val currentMonth = YearMonth.now()
    val calendarState = rememberJCalendarState(
        startMonth = currentMonth.minusMonths(3),
        endMonth = currentMonth.plusMonths(3),
        mode = CalendarMode.MONTH
    )

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.selectedDate }
            .distinctUntilChanged()
            .collect {
                selectedDay = it
            }
    }

    CalendarContainer(
        title = "Month calendar without title",
        selectedDay = selectedDay,
        calendar = {
            JCalendar(
                calendarState = calendarState,
                dayOfWeekTitleContent = null
            )
        }
    )
}

@Composable
private fun WeekCalendarWithoutTitle() {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val currentMonth = YearMonth.now()
    val calendarState = rememberJCalendarState(
        startMonth = currentMonth.minusMonths(3),
        endMonth = currentMonth.plusMonths(3),
        mode = CalendarMode.WEEK
    )

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.selectedDate }
            .distinctUntilChanged()
            .collect {
                selectedDay = it
            }
    }

    CalendarContainer(
        title = "Week calendar without title",
        selectedDay = selectedDay,
        calendar = {
            JCalendar(
                calendarState = calendarState,
                dayOfWeekTitleContent = null
            )
        }
    )
}

@Composable
private fun MonthCalendarWithoutOutDates() {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val currentMonth = YearMonth.now()
    val calendarState = rememberJCalendarState(
        startMonth = currentMonth.minusMonths(3),
        endMonth = currentMonth.plusMonths(3),
        mode = CalendarMode.MONTH
    )

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.selectedDate }
            .distinctUntilChanged()
            .collect {
                selectedDay = it
            }
    }

    CalendarContainer(
        title = "Month calendar without out dates",
        selectedDay = selectedDay,
        calendar = {
            JCalendar(
                calendarState = calendarState,
                outDayContent = null
            )
        }
    )
}

@Composable
private fun MonthCalendarWithModifiedDay() {
    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    val currentMonth = YearMonth.now()
    val calendarState = rememberJCalendarState(
        startMonth = currentMonth.minusMonths(3),
        endMonth = currentMonth.plusMonths(3),
        mode = CalendarMode.MONTH
    )

    LaunchedEffect(calendarState) {
        snapshotFlow { calendarState.currentMonth }
            .distinctUntilChanged()
            .collect {
                selectedMonth = it
            }
    }

    val emptyDates = listOf<LocalDate>(
        LocalDate.now().minusDays(5),
        LocalDate.now().minusDays(6),
        LocalDate.now().minusDays(7),
        LocalDate.now().plusDays(1),
        LocalDate.now().plusDays(2),
        LocalDate.now().plusDays(8),
        LocalDate.now().plusDays(10),
    )

    @Composable
    fun dayModifier(day: Day) = Modifier
        .background(
            color = if (day.isSelected) {
                Color(0xFF006C4C)
            } else {
                MaterialTheme.colors.surface
            },
            shape = RoundedCornerShape(8.dp)
        )

    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.secondary)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Month calendar with modified day",
                style = MaterialTheme.typography.h6
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 8.dp)
                .background(Color(0xFF89F8C7)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { calendarState.scrollBack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = { calendarState.scrollForward() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }

        JCalendar(
            calendarState = calendarState,
            dayContent = { day: Day ->
                DayContent(
                    day = day,
                    modifier = dayModifier(day),
                    defaultTextColor = if (emptyDates.contains(day.date)) {
                        Color.Red
                    } else {
                        Color.DarkGray
                    },
                    selectedTextColor = Color.White,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    size = 40.dp,
                    onClick = {
                        calendarState.selectDay(day)
                    }
                )
            },
            outDayContent = { day: Day ->
                DayContent(
                    day = day,
                    modifier = dayModifier(day),
                    defaultTextColor = Color.LightGray,
                    selectedTextColor = Color.White,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    size = 40.dp,
                    onClick = {
                        calendarState.selectDay(day)
                    }
                )
            },
            dayOfWeekTitleContent = { dayOfWeek: DayOfWeek ->
                DayOfWeekTitleContent(
                    dayOfWeek = dayOfWeek,
                    modifier = Modifier.padding(bottom = 8.dp),
                    dayOfWeekStyle = java.time.format.TextStyle.SHORT,
                    textColor = Color(0xFF002114),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            },
        )
    }
}

@Composable
private fun CalendarContainer(
    title: String,
    selectedDay: LocalDate,
    calendar: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.secondary)
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6
            )
        }
        calendar.invoke()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = selectedDay.format(DateTimeFormatter.ofPattern("yyyy MMM dd")),
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.subtitle1
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JCalendarTheme {
        Calendars()
    }
}
