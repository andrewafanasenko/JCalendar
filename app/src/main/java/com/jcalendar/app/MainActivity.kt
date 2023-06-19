package com.jcalendar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jcalendar.app.ui.theme.JCalendarTheme
import com.jcalendar.library.JCalendar
import com.jcalendar.library.rememberJCalendarState
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JCalendarTheme(darkTheme = false) {
                Greeting()
            }
        }
    }
}

@Composable
fun Greeting() {
    var selectedDay by remember { mutableStateOf(LocalDate.now()) }
    val currentMonth = YearMonth.now()
    val calendarState = rememberJCalendarState(
        startMonth = currentMonth.minusMonths(3),
        endMonth = currentMonth.plusMonths(3),
        isWeekMode = false,
        onDateSelected = { selectedDay = it }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        JCalendar(
            calendarState = calendarState
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = selectedDay.format(DateTimeFormatter.ofPattern("yyyy MMM dd")),
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.h4
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JCalendarTheme {
        Greeting()
    }
}
