package com.jcalendar.library.model

import java.time.DayOfWeek
import java.time.LocalDate

data class Week(
    val days: Map<DayOfWeek, LocalDate>
)
