package com.jcalendar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jcalendar.app.ui.theme.JCalendarTheme
import com.jcalendar.library.JCalendar
import com.jcalendar.library.rememberJCalendarState
import java.time.YearMonth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JCalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val currentMonth = YearMonth.now()
    val calendarState = rememberJCalendarState(
        startMonth = currentMonth.minusMonths(3),
        endMonth = currentMonth.plusMonths(3)
    )
    JCalendar(calendarState = calendarState)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JCalendarTheme {
        Greeting()
    }
}